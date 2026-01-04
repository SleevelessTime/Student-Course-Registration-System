package com.school.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    private Student student;
    private Course c1;
    private Course c2;
    private Course c3_conflict;

    @BeforeEach
    void setUp() {
        student = new Student(1, "Test", "Student");
        c1 = new Course("CS101", "Intro", 3, 10, "Monday", 9, 11);
        c2 = new Course("MATH101", "Calc", 4, 10, "Tuesday", 10, 12);
        c3_conflict = new Course("CS102", "Conflict", 3, 10, "Monday", 10, 12); // Overlaps with c1
    }

    @Test
    void testRegisterForCourseSuccess() {
        assertTrue(student.registerForCourse(c1));
        assertEquals(1, student.getRegisteredCourses().size());
        assertTrue(c1.getEnrolledStudents().contains(student));
    }

    @Test
    void testRegisterForDuplicateCourse() {
        student.registerForCourse(c1);
        assertFalse(student.registerForCourse(c1)); // Should fail
        assertEquals(1, student.getRegisteredCourses().size());
    }

    @Test
    void testTimeConflict() {
        student.registerForCourse(c1); // Mon 9-11
        assertFalse(student.registerForCourse(c3_conflict)); // Mon 10-12, should fail
    }

    @Test
    void testCalculateTuition() {
        student.registerForCourse(c1); // 3 credits
        student.registerForCourse(c2); // 4 credits
        // Total 7 credits * 100 = 700
        assertEquals(700.0, student.calculateTuition());
    }

    @Test
    void testGraduateStudentTuition() {
        GraduateStudent grad = new GraduateStudent(2, "Grad", "Student", "Thesis");
        grad.registerForCourse(c1); // 3 credits
        // 3 * 150 = 450
        assertEquals(450.0, grad.calculateTuition());
    }

    @Test
    void testGPACalculation() {
        student.registerForCourse(c1); // 3 credits
        student.registerForCourse(c2); // 4 credits
        
        student.setGrade(c1, 4.0); // AA
        student.setGrade(c2, 3.0); // BB
        
        // GPA = (3*4.0 + 4*3.0) / (3+4) = (12+12) / 7 = 24/7 = 3.428...
        assertEquals(3.43, student.getGpa(), 0.01);
    }
}
