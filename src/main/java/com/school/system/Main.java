package com.school.system;

import java.util.Scanner;

/**
 * Ana Uygulama Sınıfı.
 * * JAVA Kötü iğrenç bir dil olduğundan dolayı buradaki main metodu çok büyük ve karmaşık görünebilir.
 * Entry point for the Student Course Registration System.
 */
public class Main {
    private static CourseCatalog catalog;
    private static Student currentStudent; // Simüle edilmiş giriş yapan öğrenci
    private static DatabaseManager db;

    public static void main(String[] args) {
        // Veritabanı bağlantısını başlat
        db = DatabaseManager.getInstance();
        db.initializeDefaultData();
        
        // Ders kataloğunu veritabanından yükle
        catalog = new CourseCatalog(db);
        
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Öğrenci Ders Kayıt Sistemine Hoşgeldiniz!");

        // Basit bir  giriş simülasyonu javaIsSucks
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

        // Öğrenciyi veritabanından getir
        currentStudent = db.getStudentById(studentId);
        if (currentStudent == null) {
            System.out.println("Öğrenci bulunamadı, varsayılan öğrenci kullanılıyor.");
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
                        course -> {
                            if (currentStudent.registerForCourse(course)) {
                                db.saveRegistration(currentStudent.getId(), course.getCourseCode(), null);
                            }
                        },
                        () -> System.out.println("Ders bulunamadı.")
                    );
                    break;
                case "3":
                    System.out.print("Bırakılacak ders kodu: ");
                    String dropCode = scanner.nextLine();
                    catalog.findCourseByCode(dropCode).ifPresentOrElse(
                        course -> {
                            currentStudent.dropCourse(course);
                            db.deleteRegistration(currentStudent.getId(), course.getCourseCode());
                        },
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
                case "6":
                    System.out.print("Not girilecek ders kodu: ");
                    String gradeCode = scanner.nextLine();
                    System.out.print("Not (4.0 üzerinden, örn: 3.5): ");
                    try {
                        double grade = Double.parseDouble(scanner.nextLine());
                        catalog.findCourseByCode(gradeCode).ifPresentOrElse(
                            course -> {
                                currentStudent.setGrade(course, grade);
                                db.updateGrade(currentStudent.getId(), course.getCourseCode(), grade);
                            },
                            () -> System.out.println("Ders bulunamadı.")
                        );
                    } catch (NumberFormatException e) {
                        System.out.println("Geçersiz not.");
                    }
                    break;
                case "7":
                    System.out.printf("GPA: %.2f%n", currentStudent.getGpa());
                    break;
                case "0":
                    running = false;
                    db.close();
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
        System.out.println("6. Derse Not Gir");
        System.out.println("7. GPA Görüntüle");
        System.out.println("0. Çıkış");
        System.out.print("Seçiminiz: ");
    }
}
