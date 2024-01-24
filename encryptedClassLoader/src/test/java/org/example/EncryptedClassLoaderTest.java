package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedClassLoaderTest {

    private static EncryptedClassLoader classLoader;

    @BeforeAll
    public static void initialize() throws IOException {
        Encryptor.encrypt("src/test/java/org/example/data/", "Sample", "secret");

        classLoader = new EncryptedClassLoader(
                "secret",
                new File("src/test/java/org/example/data/"),
                ClassLoader.getSystemClassLoader());
    }

    @DisplayName("Проверка загрузки зашифрованного класса")
    @Test
    void shouldFindAndDecryptClassAndLoadItIfEncryptedVersionExists() {
        var classToDecrypt = "org.example.data.Sample";
        Class<?> loadedClass = assertDoesNotThrow(() -> classLoader.findClass(classToDecrypt));

        assertNotNull(loadedClass);

        System.out.println(loadedClass.getName());
        System.out.println(loadedClass.getClassLoader());
        System.out.println(Arrays.toString(loadedClass.getConstructors()));
        System.out.println(Arrays.toString(loadedClass.getDeclaredFields()));
        System.out.println(Arrays.toString(loadedClass.getDeclaredMethods()));
    }

    @DisplayName("Проверка выброса ClassNotFoundException, если переданное имя класса некорректное")
    @Test
    void shouldThrowClassNotFoundExceptionIfClassNameIsIncorrect() {
        var classToDecrypt = "org.example.data.NonExistent";

        assertThrows(ClassNotFoundException.class, () -> classLoader.findClass(classToDecrypt));
    }

    @DisplayName("Проверка выброса ClassNotFoundException, если переданной класс не зашифрован или имеет иной шифр")
    @Test
    void shouldThrowClassNotFoundExceptionIfClassIsNotEncryptedOrHasAnotherCode() {
        var classToDecrypt = "org.example.data.NotImplement";

        assertThrows(ClassNotFoundException.class, () -> classLoader.findClass(classToDecrypt));
    }

}