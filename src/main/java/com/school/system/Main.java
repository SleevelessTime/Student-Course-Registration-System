package com.school.system;

import java.util.Scanner;

/**
 * Ana Uygulama Sınıfı.
 * * JAVA Kötü iğrenç bir dil olduğundan dolayı buradaki main metodu çok büyük ve karmaşık görünebilir.
 * Entry point for the Student Course Registration System.
 */
public class Main {
    private static CourseCatalog catalog = new CourseCatalog();
    private static Student currentStudent; // Simüle edilmiş giriş yapan öğrenci

    public static void main(String[] args) {
        initializeData();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Öğrenci Ders Kayıt Sistemine Hoşgeldiniz!");

        // Basit bir giriş simülasyonu
        System.out.println("Lütfen Öğrenci ID'nizi girin (Örn: 101 veya 201): ");
        // Demo için sabit öğrenciler kullanacağız.
        // 101 -> Ali (Lisans)
        // 201 -> Veli (Yüksek Lisans)
        int studentId = 0;
        try {
            studentId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Geçersiz ID.");
        }

        if (studentId == 201) {
            currentStudent = new GraduateStudent(201, "Veli", "Yılmaz", "AI Research");
        } else {
            currentStudent = new Student(101, "Ali", "Demir");
        }
        System.out.println("Hoşgeldin, " + currentStudent.getFirstName() + " (" + currentStudent.getClass().getSimpleName() + ")");

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    catalog.printCatalog();
                    break;
                case "2":
                    System.out.print("Kayıt olunacak ders kodu: ");
                    String code = scanner.nextLine();
                    catalog.findCourseByCode(code).ifPresentOrElse(
                        course -> currentStudent.registerForCourse(course),
                        () -> System.out.println("Ders bulunamadı.")
                    );
                    break;
                case "3":
                    System.out.print("Bırakılacak ders kodu: ");
                    String dropCode = scanner.nextLine();
                    catalog.findCourseByCode(dropCode).ifPresentOrElse(
                        course -> currentStudent.dropCourse(course),
                        () -> System.out.println("Ders bulunamadı.")
                    );
                    break;
                case "4":
                    System.out.println("--- Kayıtlı Dersleriniz ---");
                    for (Course c : currentStudent.getRegisteredCourses()) {
                        System.out.println(c);
                    }
                    break;
                case "5":
                    System.out.println("Toplam Öğrenim Ücreti: " + currentStudent.calculateTuition() + " TL");
                    break;
                case "0":
                    running = false;
                    System.out.println("Çıkış yapılıyor...");
                    break;
                default:
                    System.out.println("Geçersiz seçim.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- MENÜ ---");
        System.out.println("1. Ders Kataloğunu Listele");
        System.out.println("2. Derse Kayıt Ol");
        System.out.println("3. Ders Bırak");
        System.out.println("4. Kayıtlı Derslerimi Gör");
        System.out.println("5. Öğrenim Ücretini Hesapla");
        System.out.println("0. Çıkış");
        System.out.print("Seçiminiz: ");
    }

    private static void initializeData() {
        Instructor instructor1 = new Instructor(1, "Ahmet", "Hoca", "CS");
        Instructor instructor2 = new Instructor(2, "Ayşe", "Prof", "Math");

        Course c1 = new Course("CS101", "Intro to CS", 3, 30, "Monday", 9, 11);
        c1.setInstructor(instructor1);
        
        Course c2 = new Course("MATH101", "Calculus I", 4, 40, "Tuesday", 10, 12);
        c2.setInstructor(instructor2);

        Course c3 = new Course("CS102", "Data Structures", 3, 25, "Monday", 10, 12); // CS101 ile çakışıyor (10-11 arası)
        c3.setInstructor(instructor1);

        Course c4 = new Course("ENG101", "English", 2, 50, "Friday", 14, 16);

        catalog.addCourse(c1);
        catalog.addCourse(c2);
        catalog.addCourse(c3);
        catalog.addCourse(c4);
    }
}
