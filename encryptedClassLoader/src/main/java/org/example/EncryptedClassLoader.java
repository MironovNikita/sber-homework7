package org.example;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class EncryptedClassLoader extends ClassLoader {
    private final String key;
    private final File dir;

    public EncryptedClassLoader(String key, File dir, ClassLoader parent) {
        super(parent);
        Objects.requireNonNull(key, "Ключ шифрования не может быть null!");
        Objects.requireNonNull(dir, "Папка для работы с фалами не может быть null!");
        this.key = key;
        this.dir = dir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            String[] splitName = name.split("\\.");
            String classFileName = splitName[splitName.length - 1];

            Path classPath = dir.toPath().resolve(classFileName + ".encrypted");

            if (!Files.isRegularFile(classPath))
                throw new ClassNotFoundException(String.format("Класс по данной ссылке: %s не найден", classPath));

            byte[] bytes = decrypt(classPath);
            return defineClass(name, bytes, 0, bytes.length);

        } catch (IOException | ClassFormatError exception) {
            throw new ClassNotFoundException("Ошибка загрузки класса: " + name, exception);
        }
    }

    private byte[] decrypt(Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            byte[] bytes = inputStream.readAllBytes();
            byte code = (byte) key.length();

            for (var i = 0; i < bytes.length; i++) {
                bytes[i] += code;
            }
            return bytes;
        }
    }
}