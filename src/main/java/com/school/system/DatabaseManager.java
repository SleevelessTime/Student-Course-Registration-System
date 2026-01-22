package com.school.system;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite Veritabanı Yönetici Sınıfı.
 * Tüm veritabanı işlemlerini yönetir.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:school.db";
    private static DatabaseManager instance;
    private Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver bulunamadı: " + e.getMessage());
        }
    }

    private DatabaseManager() {
        connect();
        createTables();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true); // Anlık kayıt için auto-commit açık
            System.out.println("SQLite veritabanına bağlanıldı.");
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantı hatası: " + e.getMessage());
        }
    }

    private void createTables() {
        String instructorTable = """
            CREATE TABLE IF NOT EXISTS instructors (
                id INTEGER PRIMARY KEY,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                department TEXT
            )
        """;

        String courseTable = """
            CREATE TABLE IF NOT EXISTS courses (
                course_code TEXT PRIMARY KEY,
                course_name TEXT NOT NULL,
                credit INTEGER NOT NULL,
                capacity INTEGER NOT NULL,
                day TEXT,
                start_hour INTEGER,
                end_hour INTEGER,
                instructor_id INTEGER,
                FOREIGN KEY (instructor_id) REFERENCES instructors(id)
            )
        """;

        String studentTable = """
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                is_graduate INTEGER DEFAULT 0,
                thesis_topic TEXT
            )
        """;

        String registrationTable = """
            CREATE TABLE IF NOT EXISTS registrations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                course_code TEXT NOT NULL,
                grade REAL,
                FOREIGN KEY (student_id) REFERENCES students(id),
                FOREIGN KEY (course_code) REFERENCES courses(course_code),
                UNIQUE(student_id, course_code)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(instructorTable);
            stmt.execute(courseTable);
            stmt.execute(studentTable);
            stmt.execute(registrationTable);
            System.out.println("Veritabanı tabloları oluşturuldu.");
        } catch (SQLException e) {
            System.err.println("Tablo oluşturma hatası: " + e.getMessage());
        }
    }

    // ==================== INSTRUCTOR İŞLEMLERİ ====================

    public void saveInstructor(Instructor instructor) {
        String sql = "INSERT OR REPLACE INTO instructors (id, first_name, last_name, department) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, instructor.getId());
            pstmt.setString(2, instructor.getFirstName());
            pstmt.setString(3, instructor.getLastName());
            pstmt.setString(4, instructor.getDepartment());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Eğitmen kaydetme hatası: " + e.getMessage());
        }
    }

    public Instructor getInstructorById(int id) {
        String sql = "SELECT * FROM instructors WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Instructor(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("department")
                );
            }
        } catch (SQLException e) {
            System.err.println("Eğitmen getirme hatası: " + e.getMessage());
        }
        return null;
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT * FROM instructors";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                instructors.add(new Instructor(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("department")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Eğitmenleri listeleme hatası: " + e.getMessage());
        }
        return instructors;
    }

    // ==================== COURSE İŞLEMLERİ ====================

    public void saveCourse(Course course) {
        String sql = """
            INSERT OR REPLACE INTO courses 
            (course_code, course_name, credit, capacity, day, start_hour, end_hour, instructor_id) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredit());
            pstmt.setInt(4, course.getCapacity());
            pstmt.setString(5, course.getDay());
            pstmt.setInt(6, course.getStartHour());
            pstmt.setInt(7, course.getEndHour());
            if (course.getInstructor() != null) {
                pstmt.setInt(8, course.getInstructor().getId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ders kaydetme hatası: " + e.getMessage());
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Course course = new Course(
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getInt("credit"),
                    rs.getInt("capacity"),
                    rs.getString("day"),
                    rs.getInt("start_hour"),
                    rs.getInt("end_hour")
                );
                int instructorId = rs.getInt("instructor_id");
                if (!rs.wasNull()) {
                    Instructor instructor = getInstructorById(instructorId);
                    course.setInstructor(instructor);
                }
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Dersleri listeleme hatası: " + e.getMessage());
        }
        return courses;
    }

    public Course getCourseByCode(String code) {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Course course = new Course(
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getInt("credit"),
                    rs.getInt("capacity"),
                    rs.getString("day"),
                    rs.getInt("start_hour"),
                    rs.getInt("end_hour")
                );
                int instructorId = rs.getInt("instructor_id");
                if (!rs.wasNull()) {
                    course.setInstructor(getInstructorById(instructorId));
                }
                return course;
            }
        } catch (SQLException e) {
            System.err.println("Ders getirme hatası: " + e.getMessage());
        }
        return null;
    }

    // ==================== STUDENT İŞLEMLERİ ====================

    public void saveStudent(Student student) {
        String sql = "INSERT OR REPLACE INTO students (id, first_name, last_name, is_graduate, thesis_topic) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            if (student instanceof GraduateStudent) {
                pstmt.setInt(4, 1);
                pstmt.setString(5, ((GraduateStudent) student).getThesisTopic());
            } else {
                pstmt.setInt(4, 0);
                pstmt.setNull(5, Types.VARCHAR);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Öğrenci kaydetme hatası: " + e.getMessage());
        }
    }

    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Student student;
                if (rs.getInt("is_graduate") == 1) {
                    student = new GraduateStudent(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("thesis_topic")
                    );
                } else {
                    student = new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                    );
                }
                // Kayıtlı dersleri ve notları yükle
                loadStudentRegistrations(student);
                return student;
            }
        } catch (SQLException e) {
            System.err.println("Öğrenci getirme hatası: " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student student;
                if (rs.getInt("is_graduate") == 1) {
                    student = new GraduateStudent(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("thesis_topic")
                    );
                } else {
                    student = new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                    );
                }
                loadStudentRegistrations(student);
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Öğrencileri listeleme hatası: " + e.getMessage());
        }
        return students;
    }

    // ==================== REGISTRATION İŞLEMLERİ ====================

    public void saveRegistration(int studentId, String courseCode, Double grade) {
        String sql = "INSERT OR REPLACE INTO registrations (student_id, course_code, grade) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, courseCode);
            if (grade != null) {
                pstmt.setDouble(3, grade);
            } else {
                pstmt.setNull(3, Types.REAL);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kayıt kaydetme hatası: " + e.getMessage());
        }
    }

    public void deleteRegistration(int studentId, String courseCode) {
        String sql = "DELETE FROM registrations WHERE student_id = ? AND course_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, courseCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kayıt silme hatası: " + e.getMessage());
        }
    }

    public void updateGrade(int studentId, String courseCode, double grade) {
        String sql = "UPDATE registrations SET grade = ? WHERE student_id = ? AND course_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, grade);
            pstmt.setInt(2, studentId);
            pstmt.setString(3, courseCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Not güncelleme hatası: " + e.getMessage());
        }
    }

    private void loadStudentRegistrations(Student student) {
        String sql = "SELECT * FROM registrations WHERE student_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, student.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = getCourseByCode(rs.getString("course_code"));
                if (course != null) {
                    student.getRegisteredCourses().add(course);
                    double grade = rs.getDouble("grade");
                    if (!rs.wasNull()) {
                        student.getGrades().put(course, grade);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Kayıtları yükleme hatası: " + e.getMessage());
        }
    }

    // ==================== VARSAYILAN VERİLER ====================

    public void initializeDefaultData() {
        // Önce veritabanında veri var mı kontrol et
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM courses")) {
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Veritabanında zaten veri mevcut.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Veri kontrolü hatası: " + e.getMessage());
            return;
        }

        System.out.println("Varsayılan veriler ekleniyor...");

        // Eğitmenler
        Instructor instructor1 = new Instructor(1, "Ahmet", "Hoca", "CS");
        Instructor instructor2 = new Instructor(2, "Ayşe", "Prof", "Math");
        saveInstructor(instructor1);
        saveInstructor(instructor2);

        // Dersler
        Course c1 = new Course("CS101", "Intro to CS", 3, 30, "Monday", 9, 11);
        c1.setInstructor(instructor1);
        
        Course c2 = new Course("MATH101", "Calculus I", 4, 40, "Tuesday", 10, 12);
        c2.setInstructor(instructor2);

        Course c3 = new Course("CS102", "Data Structures", 3, 25, "Monday", 10, 12);
        c3.setInstructor(instructor1);

        Course c4 = new Course("ENG101", "English", 2, 50, "Friday", 14, 16);

        saveCourse(c1);
        saveCourse(c2);
        saveCourse(c3);
        saveCourse(c4);

        // Öğrenciler
        Student student1 = new Student(101, "Ali", "Demir");
        GraduateStudent student2 = new GraduateStudent(201, "Veli", "Yılmaz", "AI Research");
        saveStudent(student1);
        saveStudent(student2);

        System.out.println("Varsayılan veriler eklendi.");
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Veritabanı bağlantısı kapatıldı.");
            }
        } catch (SQLException e) {
            System.err.println("Bağlantı kapatma hatası: " + e.getMessage());
        }
    }
}
