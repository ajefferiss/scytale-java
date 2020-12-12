package com.openmoments.scytale.encryption;

import java.security.KeyPair;
import java.util.*;

public final class CertificateEncoder {

    protected static final String RSA_PRIVATE_HEADER = "-----BEGIN RSA PRIVATE KEY-----\n";
    protected static final String RSA_PRIVATE_FOOTER = "\n-----END RSA PRIVATE KEY-----\n";
    protected static final String RSA_PUBLIC_HEADER = "-----BEGIN RSA PUBLIC KEY-----\n";
    protected static final String RSA_PUBLIC_FOOTER = "\n-----END RSA PUBLIC KEY-----\n";

    private static final int RSA_LINE_LENGTH = 67;
    private static final String RSA_LINE_REGEX = "(.{"+RSA_LINE_LENGTH+"})";
    private final Base64.Encoder base64Encoder = Base64.getEncoder();

    public enum KeyType {
        PRIVATE,
        PUBLIC
    }

    /***
     * Returns a base64 encoded keypair
     * @param keyPair The {@link java.security.KeyPair KeyPair} to be encoded
     * @return A map containing the encoded keys with the map keys of {@link KeyType KeyType}
     */
    public Map<KeyType, String> base64Encode(KeyPair keyPair) {
        Map<KeyType, String> encodedKeys = new EnumMap<>(KeyType.class);

        if (keyPair == null) {
            return new EnumMap<>(KeyType.class);
        }

        StringBuilder certificateBuilder = new StringBuilder();
        String base64String = base64Encoder.encodeToString(keyPair.getPrivate().getEncoded());

        /*
         * A RSA certificate needs line breaks every 67 characters, so add those with the replaceAll below
         */
        certificateBuilder.append(RSA_PRIVATE_HEADER);
        certificateBuilder.append(base64String.replaceAll(RSA_LINE_REGEX, "$1\n"));
        certificateBuilder.append(RSA_PRIVATE_FOOTER);
        encodedKeys.put(KeyType.PRIVATE, certificateBuilder.toString());

        certificateBuilder.setLength(0);
        base64String = base64Encoder.encodeToString(keyPair.getPublic().getEncoded());
        certificateBuilder.append(RSA_PUBLIC_HEADER);
        certificateBuilder.append(base64String.replaceAll(RSA_LINE_REGEX, "$1\n"));
        certificateBuilder.append(RSA_PUBLIC_FOOTER);
        encodedKeys.put(KeyType.PUBLIC, certificateBuilder.toString());

        return encodedKeys;
    }
}
