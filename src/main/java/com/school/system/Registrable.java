package com.school.system;

/**
 * Kayıt işlemlerini tanımlayan arayüz.
 * Interface defining registration operations.
 */
public interface Registrable {
    /**
     * Bir derse kayıt olmayı sağlar.
     * @param course Kayıt olunacak ders.
     * @return Kayıt başarılıysa true, değilse false.
     */
    boolean registerForCourse(Course course);
}
