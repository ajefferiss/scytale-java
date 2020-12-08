package com.openmoments.scytale.authentication;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesLoader {
    private static final Logger LOGGER = Logger.getLogger(PropertiesLoader.class.getName());
    private static final String DEFAULT_PROPERITES = "./local.properties";

    private String propertiesFile = DEFAULT_PROPERITES;

    public PropertiesLoader() {}

    public PropertiesLoader file(String propertiesFile) {
        this.propertiesFile = propertiesFile;
        return this;
    }

    Properties getProperties() throws IOException, IllegalArgumentException {
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
