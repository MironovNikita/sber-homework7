package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Encryptor {
    public static void encrypt(String path, String className, String key) throws IOException {
        byte code = (byte) key.length();

        Path classFilePath = Paths.get(path, className + ".class");
        byte[] bytes = Files.readAllBytes(classFilePath);

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] -= code;
        }

        Path encryptedFilePath = Paths.get(path, className + ".encrypted");
        Files.write(encryptedFilePath, bytes);
    }
}
