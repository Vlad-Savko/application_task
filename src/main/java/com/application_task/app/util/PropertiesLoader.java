package com.application_task.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Represents a properties loader, which loads properties from properties file
 */
public class PropertiesLoader {
    private static final Properties PROPS;
    private static final String PROPERTIES_PATH = "application.properties";

    static {
        PROPS = new Properties();
        try (InputStream resourceAsStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH)) {
            PROPS.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves property from file by given key
     *
     * @param key {@link String} which has the value of needed property key
     * @return property by given key as {@link String}
     */
    public static String getProperty(String key) {
        return PROPS.getProperty(key);
    }

}
