package com.openmoments.scytale.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Property Loader")
class PropertiesLoaderTest {

    @Test
    @DisplayName("Should use default file")
    void shouldLoadLocalPropertiesByDefault() throws IOException {
        Properties testProperties = new PropertiesLoader().getProperties();

        assertEquals("key", testProperties.getProperty("api.auth.type"));
        assertEquals("MY API KEY", testProperties.getProperty("api.auth.key"));
        assertEquals("", testProperties.getProperty("api.auth.cert"));
        assertEquals("https://localhost:8443/api/v1", testProperties.getProperty("api.url"));
    }

    @Test
    @DisplayName("Show throw when properties missing")
    void shouldThrowWhenPropertiesMissing() {
        PropertiesLoader propertiesLoader = new PropertiesLoader().file("invalid.properties");
        Exception illegalArgumentException = assertThrows(IllegalArgumentException.class, propertiesLoader::getProperties);
        String expectedMessage = "Scytale could not load properties file";

        assertTrue(illegalArgumentException.getMessage().contains(expectedMessage));
    }
}