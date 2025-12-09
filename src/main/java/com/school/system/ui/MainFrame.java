package com.school.system.ui;

import com.school.system.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ana pencere sınıfı.
 * Ders kayıt sisteminin saçma sapan grafiksel arayüzü.
 * Sibel Hocamız sağolsun gtp de öğrendiğimiz kadarı iel denedim.
 */
public class MainFrame extends JFrame {
    private CourseCatalog catalog;
    private Student currentStudent;
    
    // Tablolar
    private JTable catalogTable;
    private JTable myCoursesTable;
    private DefaultTableModel catalogModel;
    private DefaultTableModel myCoursesModel;
    
    // Durum çubuğu
    private JLabel statusLabel;

    public MainFrame() {
        initializeData();
        showLoginDialog();
        setupUI();
    }

    /**
     * Giriş penceresi göster.
     */
    private void showLoginDialog() {
        String[] options = {"Lisans (101)", "Yüksek Lisans (201)"};
        int choice = JOptionPane.showOptionDialog(this,
            "Öğrenci tipini seçin:",
            "Giriş",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);

        if (choice == 1) {
            currentStudent = new GraduateStudent(201, "Veli", "Yılmaz", "AI Research");
        } else {
            currentStudent = new Student(101, "Ali", "Demir");
        }
    }

    /**
     * Arayüzü kur.
     */
    private void setupUI() {
        setTitle("Ders Kayıt Sistemi - " + currentStudent.getFirstName() + " (" + currentStudent.getClass().getSimpleName() + ")");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Sekmeli panel
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("📚 Ders Kataloğu", createCatalogPanel());
        tabs.addTab("📝 Derslerim", createMyCoursesPanel());
        tabs.addTab("📊 Notlar & GPA", createGradesPanel());

        add(tabs, BorderLayout.CENTER);

        // Durum çubuğu
        statusLabel = new JLabel("Hoşgeldiniz, " + currentStudent.getFirstName() + "!");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Ders kataloğu paneli.
     */
    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tablo
        String[] columns = {"Kod", "Ders Adı", "Kredi", "Gün", "Saat", "Eğitmen", "Kontenjan"};
        catalogModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        catalogTable = new JTable(catalogModel);
        refreshCatalogTable();

        // Buton
        JButton btnRegister = new JButton("✅ Seçili Derse Kayıt Ol");
        btnRegister.addActionListener(e -> registerSelectedCourse());

        panel.add(new JScrollPane(catalogTable), BorderLayout.CENTER);
        panel.add(btnRegister, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Kayıtlı dersler paneli.
     */
    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tablo
        String[] columns = {"Kod", "Ders Adı", "Kredi", "Gün", "Saat"};
        myCoursesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        myCoursesTable = new JTable(myCoursesModel);

        // Butonlar
        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnDrop = new JButton("❌ Dersi Bırak");
        JButton btnTuition = new JButton("💰 Ücret Hesapla");

        btnDrop.addActionListener(e -> dropSelectedCourse());
        btnTuition.addActionListener(e -> {
            double tuition = currentStudent.calculateTuition();
            JOptionPane.showMessageDialog(this, 
                "Toplam Öğrenim Ücreti: " + tuition + " TL", 
                "Ücret", JOptionPane.INFORMATION_MESSAGE);
        });

        buttons.add(btnDrop);
        buttons.add(btnTuition);

        panel.add(new JScrollPane(myCoursesTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Notlar ve GPA paneli.
     */
    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // GPA göstergesi
        JPanel gpaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel gpaLabel = new JLabel("GPA: 0.00");
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gpaPanel.add(gpaLabel);

        // Not girişi
        JPanel inputPanel = new JPanel(new FlowLayout());
        JComboBox<String> courseCombo = new JComboBox<>();
        JTextField gradeField = new JTextField(5);
        JButton btnSetGrade = new JButton("Not Gir");

        // Combo'yu güncelle
        Runnable updateCombo = () -> {
            courseCombo.removeAllItems();
            for (Course c : currentStudent.getRegisteredCourses()) {
                courseCombo.addItem(c.getCourseCode() + " - " + c.getCourseName());
            }
        };

        btnSetGrade.addActionListener(e -> {
            if (courseCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Önce bir derse kayıt olun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String selected = courseCombo.getSelectedItem().toString();
                String code = selected.split(" - ")[0];
                double grade = Double.parseDouble(gradeField.getText());
                
                if (grade < 0 || grade > 4) {
                    JOptionPane.showMessageDialog(this, "Not 0-4 arasında olmalı!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                catalog.findCourseByCode(code).ifPresent(course -> {
                    currentStudent.setGrade(course, grade);
                    gpaLabel.setText(String.format("GPA: %.2f", currentStudent.getGpa()));
                    statusLabel.setText(code + " için not girildi: " + grade);
                });
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Geçersiz not formatı!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(new JLabel("Ders:"));
        inputPanel.add(courseCombo);
        inputPanel.add(new JLabel("Not (0-4):"));
        inputPanel.add(gradeField);
        inputPanel.add(btnSetGrade);

        // Not tablosu
        JTextArea gradeInfo = new JTextArea();
        gradeInfo.setEditable(false);
        gradeInfo.setText("Not Sistemi:\n" +
            "AA = 4.0  |  BA = 3.5  |  BB = 3.0\n" +
            "CB = 2.5  |  CC = 2.0  |  DC = 1.5\n" +
            "DD = 1.0  |  FF = 0.0");

        // Yenile butonu
        JButton btnRefresh = new JButton("🔄 Ders Listesini Yenile");
        btnRefresh.addActionListener(e -> {
            updateCombo.run();
            gpaLabel.setText(String.format("GPA: %.2f", currentStudent.getGpa()));
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputPanel, BorderLayout.NORTH);
        bottomPanel.add(btnRefresh, BorderLayout.CENTER);
        bottomPanel.add(gradeInfo, BorderLayout.SOUTH);

        panel.add(gpaPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);
        return panel;
    }

    // --- İş Mantığı Metodları ---

    private void refreshCatalogTable() {
        catalogModel.setRowCount(0);
        for (Course c : catalog.getCourses()) {
            catalogModel.addRow(new Object[]{
                c.getCourseCode(),
                c.getCourseName(),
                c.getCredit(),
                c.getDay(),
                c.getStartHour() + ":00 - " + c.getEndHour() + ":00",
                c.getInstructor() != null ? c.getInstructor().getFirstName() + " " + c.getInstructor().getLastName() : "TBA",
                c.getEnrolledStudents().size() + "/" + c.getCapacity()
            });
        }
    }

    private void refreshMyCoursesTable() {
        myCoursesModel.setRowCount(0);
        for (Course c : currentStudent.getRegisteredCourses()) {
            myCoursesModel.addRow(new Object[]{
                c.getCourseCode(),
                c.getCourseName(),
                c.getCredit(),
                c.getDay(),
                c.getStartHour() + ":00 - " + c.getEndHour() + ":00"
            });
        }
    }

    private void registerSelectedCourse() {
        int row = catalogTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir ders seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = (String) catalogModel.getValueAt(row, 0);
        catalog.findCourseByCode(code).ifPresent(course -> {
            // System.out çıktılarını yakala java harika değil mi?    :(
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream old = System.out;
            System.setOut(new java.io.PrintStream(baos));
            
            boolean success = currentStudent.registerForCourse(course);
            
            System.setOut(old);
            String message = baos.toString().trim();

            if (success) {
                statusLabel.setText("✅ " + code + " dersine kayıt yapıldı.");
                JOptionPane.showMessageDialog(this, message, "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLabel.setText("❌ Kayıt başarısız: " + code);
                JOptionPane.showMessageDialog(this, message, "Hata", JOptionPane.ERROR_MESSAGE);
            }
            refreshCatalogTable();
            refreshMyCoursesTable();
        });
    }

    private void dropSelectedCourse() {
        int row = myCoursesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir ders seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = (String) myCoursesModel.getValueAt(row, 0);
        catalog.findCourseByCode(code).ifPresent(course -> {
            currentStudent.dropCourse(course);
            statusLabel.setText("Ders bırakıldı: " + code);
            refreshCatalogTable();
            refreshMyCoursesTable();
        });
    }

    private void initializeData() {
        catalog = new CourseCatalog();
        
        Instructor i1 = new Instructor(1, "Ahmet", "Hoca", "CS");
        Instructor i2 = new Instructor(2, "Ayşe", "Prof", "Math");

        Course c1 = new Course("CS101", "Intro to CS", 3, 30, "Monday", 9, 11);
        c1.setInstructor(i1);
        
        Course c2 = new Course("MATH101", "Calculus I", 4, 40, "Tuesday", 10, 12);
        c2.setInstructor(i2);
        
        Course c3 = new Course("CS102", "Data Structures", 3, 25, "Monday", 10, 12);
        c3.setInstructor(i1);
        
        Course c4 = new Course("ENG101", "English", 2, 50, "Friday", 14, 16);

        catalog.addCourse(c1);
        catalog.addCourse(c2);
        catalog.addCourse(c3);
        catalog.addCourse(c4);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}
