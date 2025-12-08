package com.school.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Öğrenci sınıfı.
 * Represents a student.
 */
public class Student implements Registrable {
    private int id;
    private String firstName;
    private String lastName;
    private List<Course> registeredCourses;
    private Map<Course, Double> grades; // Ders -> Not (4.0 üzerinden)
    private double gpa;

    public Student(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registeredCourses = new ArrayList<>();
        this.grades = new HashMap<>();
        this.gpa = 0.0;
    }

    /**
     * Öğrenim ücretini hesaplar.
     * @return Ücret
     */
    public double calculateTuition() {
        // Basit bir hesaplama: Kredi başına ücret
        int totalCredits = registeredCourses.stream().mapToInt(Course::getCredit).sum();
        return totalCredits * 100.0; // Kredi başı 100 birim
    }

    @Override
    public boolean registerForCourse(Course course) {
        // 1. Kontrol: Zaten kayıtlı mı?
        if (registeredCourses.contains(course)) {
            System.out.println("Hata: Zaten bu derse kayıtlısınız: " + course.getCourseCode());
            return false;
        }

        // 2. Kontrol: Ders dolu mu?
        if (course.isFull()) {
            System.out.println("Hata: Ders kontenjanı dolu: " + course.getCourseCode());
            return false;
        }

        // 3. Kontrol: Zaman çakışması (Opsiyonel özellik ama ekleyelim)
        for (Course c : registeredCourses) {
            if (c.getDay().equals(course.getDay())) {
                // Saat aralıkları çakışıyor mu?
                // (Start1 < End2) and (Start2 < End1)
                if (c.getStartHour() < course.getEndHour() && course.getStartHour() < c.getEndHour()) {
                    System.out.println("Hata: Zaman çakışması var! " + c.getCourseCode() + " ile " + course.getCourseCode());
                    return false;
                }
            }
        }

        // Kayıt işlemi
        registeredCourses.add(course);
        course.enrollStudent(this);
        System.out.println("Başarılı: " + course.getCourseCode() + " dersine kayıt yapıldı.");
        return true;
    }

    public void dropCourse(Course course) {
        if (registeredCourses.contains(course)) {
            registeredCourses.remove(course);
            course.removeStudent(this);
            System.out.println("Ders bırakıldı: " + course.getCourseCode());
        } else {
            System.out.println("Hata: Bu derse kayıtlı değilsiniz.");
        }
    }

    /**
     * Derse not gir ve GPA'yı güncelle.
     * @param course Ders
     * @param grade Not (4.0 üzerinden: AA=4.0, BA=3.5, BB=3.0, CB=2.5, CC=2.0, DC=1.5, DD=1.0, FF=0)
     */
    public void setGrade(Course course, double grade) {
        if (registeredCourses.contains(course)) {
            grades.put(course, grade);
            calculateGPA();
            System.out.println(course.getCourseCode() + " için not girildi: " + grade);
        } else {
            System.out.println("Hata: Bu derse kayıtlı değilsiniz.");
        }
    }

    /**
     * GPA hesapla: Toplam(Kredi * Not) / Toplam Kredi
     */
    private void calculateGPA() {
        double totalPoints = 0;
        int totalCredits = 0;
        for (Map.Entry<Course, Double> entry : grades.entrySet()) {
            int credit = entry.getKey().getCredit();
            totalPoints += credit * entry.getValue();
            totalCredits += credit;
        }
        this.gpa = totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public List<Course> getRegisteredCourses() { return registeredCourses; }
    public double getGpa() { return gpa; }
    public Map<Course, Double> getGrades() { return grades; }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + firstName + " " + lastName + '\'' +
                ", courses=" + registeredCourses.size() +
                '}';
    }
}
