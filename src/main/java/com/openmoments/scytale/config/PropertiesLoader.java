package com.openmoments.scytale.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static final String DEFAULT_PROPERITES = "./local.properties";

    private String propertiesFile = DEFAULT_PROPERITES;

    public PropertiesLoader file(String propertiesFile) {
        this.propertiesFile = propertiesFile;
        return this;
    }

    public Properties getProperties() throws IOException, IllegalArgumentException {
        return readProperties();
    }

    private Properties readProperties() throws IOException, IllegalArgumentException {
        Properties scytaleProperties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFile)) {
            if (inputStream == null) {
                String error = "Scytale could not load properties file ["+propertiesFile+"] as resource is null";
                throw new IllegalArgumentException(error);
            }
            scytaleProperties.load(inputStream);
        }

        return scytaleProperties;
    }
}
