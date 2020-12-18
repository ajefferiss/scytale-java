package com.openmoments.scytale.encryption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Random String Generator")
class RandomStringGeneratorTest {

    @Test
    @DisplayName("Should not create same string twice")
    void shouldNotCreateSameStringTwice() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator();
        assertNotEquals(randomStringGenerator.buildString(), randomStringGenerator.buildString());
    }

    @Test
    @DisplayName("Should create strings of different lengths")
    void shouldCreateStringOfDifferentLength() {
        String randomString = new RandomStringGenerator().length(10).buildString();
        assertEquals(10, randomString.length());
    }
}