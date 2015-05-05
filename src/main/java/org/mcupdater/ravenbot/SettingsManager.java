package org.mcupdater.ravenbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SettingsManager {
    private static SettingsManager instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Settings settings;
    private final Path configFile = new File(".").toPath().resolve("raven.json");

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public SettingsManager() {
        if (!configFile.toFile().exists()) {
            System.out.println("Creating default settings");
            this.settings = getDefaultSettings();
            saveSettings();
            return;
        }
        loadSettings();
        System.out.println("Settings loaded");
    }

    public void loadSettings() {
        try {
            BufferedReader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8);
            this.settings = gson.fromJson(reader, Settings.class);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public void saveSettings() {
        String jsonOut = gson.toJson(this.settings);
        try {
            BufferedWriter writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8);
            writer.append(jsonOut);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Settings getDefaultSettings() {
        Settings newSettings = new Settings();
        newSettings.setNick("RavenBot");
        newSettings.setLogin("ravenbot");
        newSettings.setRealName("MCUpdater's RavenBot 2.0");
        newSettings.setServer("irc.esper.net");
        newSettings.setPort(6667);
        newSettings.setNsPassword("");
        newSettings.setTwitCKey("");
        newSettings.setTwitCSecret("");
        newSettings.setTwitToken("");
        newSettings.setTwitTSecret("");
        return newSettings;
    }
}
