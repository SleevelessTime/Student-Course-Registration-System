package com.school.system;

import java.util.ArrayList;
import java.util.List;

/**
 * Öğrenci sınıfı.
 * Represents a student.
 */
public class Student implements Registrable {
    private int id;
    private String firstName;
    private String lastName;
    private List<Course> registeredCourses;
    private double gpa;

    public Student(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registeredCourses = new ArrayList<>();
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

    // Getters and Setters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public List<Course> getRegisteredCourses() { return registeredCourses; }
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + firstName + " " + lastName + '\'' +
                ", courses=" + registeredCourses.size() +
                '}';
    }
}
