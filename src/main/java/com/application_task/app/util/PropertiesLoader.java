package com.application_task.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

    public static String getProperty(String key) {
        return PROPS.getProperty(key);
    }

}
