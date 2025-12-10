# Öğrenci Ders Kayıt Sistemi (Student Course Registration System)

Bu proje, Nesne Tabanlı Programlama (OOP) prensipleri kullanılarak geliştirilmiş, hem **Konsol** hem de **Grafik Arayüz (GUI)** desteği sunan kapsamlı bir üniversite ders kayıt simülasyonudur. Öğrencilerin ders seçimi, kayıt işlemleri, ders bırakma, not görüntüleme ve genel not ortalaması (GPA) hesaplama gibi temel akademik süreçlerini yönetir.

## 🚀 Kurulum ve Çalıştırma

Projeyi çalıştırmak için bilgisayarınızda Java Development Kit (JDK) kurulu olmalıdır.

**1. Derleme (Compile):**
Tüm kaynak kodlarını derlemek için:
```bash
mkdir bin
javac -d bin -encoding UTF-8 src/main/java/com/school/system/*.java src/main/java/com/school/system/ui/*.java
```

**2. Konsol Modunda Çalıştırma:**
```bash
java -cp bin com.school.system.Main
```

**3. Grafik Arayüz (GUI) Modunda Çalıştırma:**
```bash
java -cp bin com.school.system.ui.MainFrame
```

**4. Testleri Çalıştırma:**
Proje dizininde `junit-platform-console-standalone-1.9.3.jar` dosyasının bulunduğundan emin olun.
```bash
# Testleri derle
javac -d bin -cp "junit-platform-console-standalone-1.9.3.jar" src/test/java/com/school/system/StudentTest.java

# Testleri çalıştır
java -jar junit-platform-console-standalone-1.9.3.jar -cp bin -c com.school.system.StudentTest
```

## 📂 Proje Yapısı

```
src/
├── main/java/com/school/system/
│   ├── Main.java            # Ana uygulama ve menü döngüsü
│   ├── Student.java         # Temel öğrenci sınıfı (GPA ve Notlar eklendi)
│   ├── GraduateStudent.java # Yüksek lisans öğrencisi (Kalıtım örneği)
│   ├── Course.java          # Ders özellikleri ve mantığı
│   ├── Instructor.java      # Eğitmen bilgileri
│   ├── Registration.java    # Kayıt kaydı tutan sınıf
│   ├── CourseCatalog.java   # Dersleri yöneten katalog
│   ├── Registrable.java     # Kayıt yeteneği kazandıran arayüz
│   └── ui/
│       └── MainFrame.java   # Swing tabanlı Grafik Kullanıcı Arayüzü (GUI)
└── test/java/com/school/system/
    └── StudentTest.java     # Unit testler (GPA, Çakışma, Kayıt vb.)
```

---

## 🛠️ Uygulanan OOP Prensipleri

Bu proje, OOP'nin dört temel direği üzerine inşa edilmiştir. İşte kod örnekleriyle açıklamalar:

### 1. Kalıtım (Inheritance)
`GraduateStudent` sınıfı, `Student` sınıfının tüm özelliklerini miras alır ancak kendine has özellikler (tez konusu gibi) ekler. Bu sayede kod tekrarı önlenir.

**Kod Örneği (`GraduateStudent.java`):**
```java
// Student sınıfından miras alıyor (extends)
public class GraduateStudent extends Student {
    private String thesisTopic;

    public GraduateStudent(int id, String firstName, String lastName, String thesisTopic) {
        super(id, firstName, lastName); // Üst sınıfın kurucusunu çağırır
        this.thesisTopic = thesisTopic;
    }
}
```

### 2. Çok Biçimlilik (Polymorphism)
Aynı metodun (`calculateTuition`), farklı nesneler (`Student` ve `GraduateStudent`) üzerinde farklı davranmasıdır. Lisans ve Yüksek Lisans öğrencileri için ücret hesaplaması farklıdır.

**Kod Örneği:**
```java
// Student.java (Temel Sınıf)
public double calculateTuition() {
    // Kredi başı 100 birim
    return totalCredits * 100.0;
}

// GraduateStudent.java (Override Edilmiş Metot)
@Override
public double calculateTuition() {
    // Yüksek lisans için kredi başı 150 birim
    return totalCredits * 150.0;
}
```

### 3. Arayüz (Interface)
`Registrable` arayüzü, sisteme "kayıt olunabilir" yeteneği tanımlar. `Student` sınıfı bu arayüzü uygulayarak (implement) bir sözleşmeye uyar.

**Kod Örneği (`Registrable.java`):**
```java
public interface Registrable {
    // Bu arayüzü kullanan her sınıf bu metodu içermek ZORUNDADIR.
    boolean registerForCourse(Course course);
}
```

### 4. Kapsülleme (Encapsulation)
Sınıf değişkenleri `private` yapılarak dışarıdan doğrudan erişim engellenir. Veri bütünlüğü `public` getter ve setter metodları ile korunur.

**Kod Örneği (`Course.java`):**
```java
public class Course {
    private String courseCode; // Dışarıdan doğrudan değiştirilemez
    private int capacity;

    // Okumak için Getter
    public String getCourseCode() { return courseCode; }
    
    // Kontrollü veri girişi için Setter
    public void setCapacity(int capacity) {
        if(capacity > 0) this.capacity = capacity;
    }
}
```

---

## 🌟 Önemli Özellikler ve Kod Detayları

### 1. Ders Çakışma Kontrolü (Conflict Detection)
Öğrenci bir derse kayıt olurken, sistem mevcut dersleriyle yeni dersin gün ve saatlerinin çakışıp çakışmadığını kontrol eder.

**Mantık (`Student.java`):**
```java
for (Course c : registeredCourses) {
    // Aynı gün mü?
    if (c.getDay().equals(course.getDay())) {
        // Saat aralıkları kesişiyor mu?
        // Mantık: (Mevcut.Başlangıç < Yeni.Bitiş) VE (Yeni.Başlangıç < Mevcut.Bitiş)
        if (c.getStartHour() < course.getEndHour() && course.getStartHour() < c.getEndHour()) {
            System.out.println("Hata: Zaman çakışması var!");
            return false; // Kaydı engelle
        }
    }
}
```

### 2. Eğitmen Atama
Her dersin bir eğitmeni (`Instructor`) olabilir. Bu ilişki "Composition/Aggregation" (Sahiplik/İlişki) prensibi ile kurulmuştur.

**Kod Örneği (`Main.java`):**
```java
// Eğitmen oluştur
Instructor instructor1 = new Instructor(1, "Ahmet", "Hoca", "CS");

// Dersi oluştur
Course c1 = new Course("CS101", "Intro to CS", ...);

// Eğitmeni derse ata
c1.setInstructor(instructor1);
```

### 3. Mükerrer Kayıt ve Kontenjan Kontrolü
Sistem, öğrencinin aynı derse iki kez kayıt olmasını ve dolu derslere kayıt yapılmasını engeller.

```java
// 1. Zaten kayıtlı mı?
if (registeredCourses.contains(course)) return false;

// 2. Ders dolu mu?
if (course.isFull()) return false;
```

### 4. Notlandırma ve GPA Hesaplama
Öğrencilerin aldıkları derslere not girilebilir ve bu notlar üzerinden ağırlıklı genel not ortalaması (GPA) hesaplanır.

**Mantık (`Student.java`):**
```java
public double calculateGPA() {
    // Toplam Puan / Toplam Kredi
    // Puan = Ders Kredisi * Harf Notu Katsayısı (A=4.0, B=3.0 vb.)
}
```

## 🧪 Testler

Proje, iş mantığının doğruluğunu garanti altına almak için `JUnit` testleri içerir.
*   **Kayıt Testi:** Başarılı kaydı doğrular.
*   **Çakışma Testi:** Çakışan saatlerde kaydın başarısız olduğunu doğrular.
*   **Ücret Testi:** Kredi başına ücretin doğru hesaplandığını doğrular.
*   **GPA Testi:** Farklı ders notlarına göre ortalamanın doğru hesaplandığını doğrular.

---
*Bu proje, Nesne Tabanlı Programlama dersi final ödevi gereksinimlerini karşılamak üzere hazırlanmıştır. JAVA berbattır*
