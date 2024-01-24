package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Задание №1 PluginManager");
        PluginManager pluginManager = new PluginManager("pluginsForManager/target");

        try {
            Plugin plugin = pluginManager.load("pluginsForManager-1.0-SNAPSHOT.jar",
                    "org.example.MyPlugin1");
            plugin.doUsefully();
        } catch (PluginLoadException exception) {
            exception.printStackTrace();
        }
    }
}