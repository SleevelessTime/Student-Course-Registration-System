package com.school.system;

/**
 * Yüksek Lisans Öğrencisi.
 * Inherits from Student.
 */
public class GraduateStudent extends Student {
    private String thesisTopic;

    public GraduateStudent(int id, String firstName, String lastName, String thesisTopic) {
        super(id, firstName, lastName);
        this.thesisTopic = thesisTopic;
    }

    @Override
    public double calculateTuition() {
        // Yüksek lisans öğrencileri için farklı tarife (Örn: %20 zamlı veya sabit ücret)
        // Burada örnek olarak kredi başı 150 birim diyelim.
        int totalCredits = getRegisteredCourses().stream().mapToInt(Course::getCredit).sum();
        return totalCredits * 150.0;
    }

    public String getThesisTopic() { return thesisTopic; }
    public void setThesisTopic(String thesisTopic) { this.thesisTopic = thesisTopic; }

    @Override
    public String toString() {
        return "GraduateStudent{" +
                "id=" + getId() +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", thesis='" + thesisTopic + '\'' +
                '}';
    }
}
