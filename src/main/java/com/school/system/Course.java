package com.school.system;

import java.util.ArrayList;
import java.util.List;

/**
 * Ders sınıfı.
 * Represents a course in the catalog.
 */
public class Course {
    private String courseCode;
    private String courseName;
    private int credit;
    private int capacity;
    private Instructor instructor;
    private List<Student> enrolledStudents;
    
    // Zaman çakışması kontrolü için basit saat alanları (Örn: "Monday", 10, 12)
    private String day;
    private int startHour;
    private int endHour;

    /**
     * Constructor for Course.
     * @param courseCode Ders Kodu
     * @param courseName Ders Adı
     * @param credit Kredi
     * @param capacity Kontenjan
     * @param day Gün (Örn: "Monday")
     * @param startHour Başlangıç saati (0-23)
     * @param endHour Bitiş saati (0-23)
     */
    public Course(String courseCode, String courseName, int credit, int capacity, String day, int startHour, int endHour) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credit = credit;
        this.capacity = capacity;
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
        this.enrolledStudents = new ArrayList<>();
    }

    public boolean isFull() {
        return enrolledStudents.size() >= capacity;
    }

    public boolean enrollStudent(Student student) {
        if (!isFull() && !enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
            return true;
        }
        return false;
    }
    
    public void removeStudent(Student student) {
        enrolledStudents.remove(student);
    }

    // Getters and Setters
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredit() { return credit; }
    public int getCapacity() { return capacity; }
    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public List<Student> getEnrolledStudents() { return enrolledStudents; }
    public String getDay() { return day; }
    public int getStartHour() { return startHour; }
    public int getEndHour() { return endHour; }

    @Override
    public String toString() {
        return String.format("%s - %s (%d Credit) [%s %d:00-%d:00] Instructor: %s", 
            courseCode, courseName, credit, day, startHour, endHour, 
            (instructor != null ? instructor.getFirstName() + " " + instructor.getLastName() : "TBA"));
    }
}
