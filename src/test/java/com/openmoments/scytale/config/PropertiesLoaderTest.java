package com.openmoments.scytale.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesLoaderTest {

    @Test
    void shouldLoadLocalPropertiesByDefault() throws IOException {
        Properties testProperties = new PropertiesLoader().getProperties();

        assertEquals("key", testProperties.getProperty("api.type"));
        assertEquals("MY API KEY", testProperties.getProperty("api.authentication"));
        assertEquals("https://localhost:8443/api/v1", testProperties.getProperty("api.url"));
    }

    @Test
    void shouldThrowWhenPropertiesMissing() {
        Exception illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> new PropertiesLoader().file("invalid.properites").getProperties());
        String expectedMessage = "Scytale could not load properties file";

        assertTrue(illegalArgumentException.getMessage().contains(expectedMessage));
    }
}