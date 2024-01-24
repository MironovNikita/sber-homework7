package org.example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginManager {
    private final String pluginRootDirectory;

    public PluginManager(String pluginRootDirectory) {
        this.pluginRootDirectory = pluginRootDirectory;
    }

    public Plugin load(String jarFileName, String pluginName) throws PluginLoadException {
        String jarFilePath = pluginRootDirectory + "/" + jarFileName;

        try {
            URLClassLoader classLoader = createClassLoader(jarFilePath);
            JarFile jarFile = createJarFile(jarFilePath);

            return findAndLoadPlugin(classLoader, jarFile, pluginName);

        } catch (MalformedURLException exception) {
            throw new PluginLoadException(String.format("Ошибка создания URL для JAR файла по пути: %s.", jarFilePath),
                    exception.getMessage());
        } catch (IOException exception) {
            throw new PluginLoadException(
                    String.format("Ошибка создания объекта JarFile на основе файла JAR по пути: %s",
                            jarFilePath), exception.getMessage());
        } catch (IllegalArgumentException exception) {
            throw new PluginLoadException(String.format("Ошибка загрузки плагина из JAR файла: %s.", jarFilePath),
                    exception.getMessage());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException exception) {
            throw new PluginLoadException(String.format("Ошибка загрузки плагина.%n"),
                    exception.getMessage());
        }
    }

    private URLClassLoader createClassLoader(String jarFilePath) throws MalformedURLException {
        URL jarUrl = new File(jarFilePath).toURI().toURL();
        return new URLClassLoader(new URL[]{jarUrl}, getClass().getClassLoader());
    }

    private JarFile createJarFile(String jarFilePath) throws IOException {
        return new JarFile(new File(jarFilePath));
    }

    private Plugin findAndLoadPlugin(URLClassLoader classLoader, JarFile jarFile, String pluginName)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            if (entry.getName().endsWith(".class")) {
                String className = entry.getName()
                        .replace('/', '.').replace(".class", "");

                if (className.equals(pluginName)) {
                    System.out.printf("Обработка класса: %s%n", className);
                    Class<?> loadedClass = classLoader.loadClass(className);

                    if (Plugin.class.isAssignableFrom(loadedClass)) {
                        Plugin plugin = (Plugin) loadedClass.getDeclaredConstructor().newInstance();
                        System.out.printf("Плагин: %s успешно загружен!%n", plugin.getClass().getName());

                        return plugin;
                    } else {
                        throw new IllegalArgumentException(
                                String.format("Плагин не загружен. %s не имплементирует интерфейс Plugin",
                                        loadedClass.getName()));
                    }
                }
            }
        }
        throw new ClassNotFoundException(
                String.format("Запрашиваемый плагин %s не был найден в указанном JAR файле: %s",
                        pluginName, jarFile.getName()));
    }
}