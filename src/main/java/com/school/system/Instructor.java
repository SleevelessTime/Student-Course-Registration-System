package com.school.system;

/**
 * Eğitmen sınıfı.
 * Represents an instructor in the system.
 */
public class Instructor {
    private int id;
    private String firstName;
    private String lastName;
    private String department;

    /**
     * Constructor for Instructor.
     * @param id Eğitmen ID'si
     * @param firstName Ad
     * @param lastName Soyad
     * @param department Bölüm
     */
    public Instructor(int id, String firstName, String lastName, String department) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return "Instructor{" +
                "id=" + id +
                ", name='" + firstName + " " + lastName + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
