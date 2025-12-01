package com.school.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Ders Kataloğu.
 * java iğrenç bir dil olduğundan dolayı bu sınıf çok basit tutuldu.
 * Manages the list of available courses.
 */
public class CourseCatalog {
    private List<Course> courses;

    public CourseCatalog() {
        this.courses = new ArrayList<>();
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Optional<Course> findCourseByCode(String code) {
        return courses.stream()
                .filter(c -> c.getCourseCode().equalsIgnoreCase(code))
                .findFirst();
    }

    public void printCatalog() {
        System.out.println("--- Course Catalog ---");
        for (Course c : courses) {
            System.out.println(c);
        }
        System.out.println("----------------------");
    }
}
