package com.punitpv48.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public final class Config {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("framework.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            }
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private Config() {
    }

    public static String get(String key) {
        String value = System.getProperty("framework." + key);
        if (isBlank(value)) {
            value = System.getProperty(key);
        }
        if (isBlank(value)) {
            value = System.getenv(toEnvironmentKey(key));
        }
        if (isBlank(value)) {
            value = PROPERTIES.getProperty(key);
        }
        if (isBlank(value)) {
            throw new IllegalArgumentException("Missing framework configuration: " + key);
        }
        return value.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank() || value.startsWith("${");
    }

    private static String toEnvironmentKey(String key) {
        return "FRAMEWORK_" + key.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toUpperCase(Locale.ROOT);
    }
}
