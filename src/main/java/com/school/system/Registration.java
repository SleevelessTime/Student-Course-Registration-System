package com.school.system;

import java.time.LocalDate;

/**
 * Kayıt sınıfı.
 * Java iğrenç bir dil olduğundan dolayı bu sınıf çok basit tutuldu.
 * Represents a registration record.
 */
public class Registration {
    private Student student;
    private Course course;
    private LocalDate registrationDate;
    private String status; // "Active", "Dropped", "Completed"

    public Registration(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.registrationDate = LocalDate.now();
        this.status = "Active";
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Registration{" +
                "student=" + student.getFirstName() +
                ", course=" + course.getCourseCode() +
                ", date=" + registrationDate +
                ", status='" + status + '\'' +
                '}';
    }
}
