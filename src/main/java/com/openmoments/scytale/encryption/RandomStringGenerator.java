package com.openmoments.scytale.encryption;

import java.security.SecureRandom;

public class RandomStringGenerator {
    public static int STRING_LENGTH = 40;
    private static final String STRING_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-";
    private static final SecureRandom RNG = new SecureRandom();

    private int stringLength = STRING_LENGTH;

    public RandomStringGenerator() {}

    public String buildString() {
        return generateString();
    }

    public RandomStringGenerator length(int stringLength) {
        this.stringLength = stringLength;
        return this;
    }

    private String generateString() {
        StringBuilder stringBuilder = new StringBuilder(stringLength);
        for (int x = 0; x < stringLength; x++) {
            stringBuilder.append(STRING_CHARACTERS.charAt(RNG.nextInt(STRING_CHARACTERS.length())));
        }
        return stringBuilder.toString();
    }
}
