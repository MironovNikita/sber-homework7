package org.example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Задание №2 EncryptedClassLoader");
        String key = "secret";
        String workDir = "encryptedClassLoader/src/main/java/org/example/instance/";

        try {
            Encryptor.encrypt(workDir, "Sample", key);

            EncryptedClassLoader encryptedClassLoader = new EncryptedClassLoader(key,
                    new File(workDir), ClassLoader.getSystemClassLoader());

            try {
                Class<?> loadedClass = encryptedClassLoader.findClass("org.example.instance.Sample");
                Object instance = loadedClass.getDeclaredConstructor().newInstance();
                loadedClass.getMethod("printMessage").invoke(instance);
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException exception) {
                throw new RuntimeException("Ошибка загрузки или вызова класса", exception);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при шифровании или загрузке класса", exception);
        }
    }
}
