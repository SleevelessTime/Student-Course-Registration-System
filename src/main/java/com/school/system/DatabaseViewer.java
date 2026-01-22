package com.school.system;

import java.sql.*;

/**
 * Veritabanı içeriğini görüntülemek için yardımcı sınıf.
 */
public class DatabaseViewer {
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:school.db");
            
            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    VERITABANI İÇERİĞİ                          ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
            
            // Öğrenciler
            System.out.println("┌─────────────────────────────────────────────────────────────────┐");
            System.out.println("│                         ÖĞRENCILER                              │");
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
            System.out.printf("│ %-6s │ %-15s │ %-15s │ %-8s │ %-12s │%n", "ID", "Ad", "Soyad", "Tip", "Tez Konusu");
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            while (rs.next()) {
                String tip = rs.getInt("is_graduate") == 1 ? "Y.Lisans" : "Lisans";
                String thesis = rs.getString("thesis_topic");
                thesis = thesis != null ? thesis : "-";
                System.out.printf("│ %-6d │ %-15s │ %-15s │ %-8s │ %-12s │%n",
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    tip,
                    thesis.length() > 12 ? thesis.substring(0, 12) : thesis
                );
            }
            System.out.println("└─────────────────────────────────────────────────────────────────┘\n");
            
            // Eğitmenler
            System.out.println("┌─────────────────────────────────────────────────────────────────┐");
            System.out.println("│                         EĞİTMENLER                              │");
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            rs = stmt.executeQuery("SELECT * FROM instructors");
            System.out.printf("│ %-6s │ %-15s │ %-15s │ %-20s │%n", "ID", "Ad", "Soyad", "Bölüm");
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            while (rs.next()) {
                System.out.printf("│ %-6d │ %-15s │ %-15s │ %-20s │%n",
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("department")
                );
            }
            System.out.println("└─────────────────────────────────────────────────────────────────┘\n");
            
            // Dersler
            System.out.println("┌─────────────────────────────────────────────────────────────────┐");
            System.out.println("│                          DERSLER                                │");
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            rs = stmt.executeQuery("SELECT c.*, i.first_name as inst_name FROM courses c LEFT JOIN instructors i ON c.instructor_id = i.id");
            System.out.printf("│ %-8s │ %-18s │ %-6s │ %-10s │ %-12s │%n", "Kod", "Ad", "Kredi", "Gün", "Eğitmen");
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            while (rs.next()) {
                String instName = rs.getString("inst_name");
                instName = instName != null ? instName : "TBA";
                System.out.printf("│ %-8s │ %-18s │ %-6d │ %-10s │ %-12s │%n",
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getInt("credit"),
                    rs.getString("day"),
                    instName
                );
            }
            System.out.println("└─────────────────────────────────────────────────────────────────┘\n");
            
            // Kayıtlar
            System.out.println("┌─────────────────────────────────────────────────────────────────┐");
            System.out.println("│                      DERS KAYITLARI                             │");
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            rs = stmt.executeQuery("""
                SELECT r.*, s.first_name, s.last_name, c.course_name 
                FROM registrations r 
                JOIN students s ON r.student_id = s.id 
                JOIN courses c ON r.course_code = c.course_code
            """);
            System.out.printf("│ %-6s │ %-15s │ %-10s │ %-18s │ %-6s │%n", "ID", "Öğrenci", "Ders Kodu", "Ders Adı", "Not");
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            boolean hasRegistrations = false;
            while (rs.next()) {
                hasRegistrations = true;
                double grade = rs.getDouble("grade");
                String gradeStr = rs.wasNull() ? "-" : String.format("%.1f", grade);
                System.out.printf("│ %-6d │ %-15s │ %-10s │ %-18s │ %-6s │%n",
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    gradeStr
                );
            }
            if (!hasRegistrations) {
                System.out.println("│                    Henüz kayıt yok                              │");
            }
            System.out.println("└─────────────────────────────────────────────────────────────────┘\n");
            
            conn.close();
            
        } catch (Exception e) {
            System.err.println("Hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
