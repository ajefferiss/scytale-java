package com.openmoments.scytale.encryption;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

public final class CertificateEncoder {

    protected static final String RSA_PRIVATE_HEADER = "-----BEGIN RSA PRIVATE KEY-----";
    protected static final String RSA_PRIVATE_FOOTER = "-----END RSA PRIVATE KEY-----";
    protected static final String RSA_PUBLIC_HEADER = "-----BEGIN RSA PUBLIC KEY-----";
    protected static final String RSA_PUBLIC_FOOTER = "-----END RSA PUBLIC KEY-----";

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

        if (keyPair.getPrivate().getAlgorithm().equalsIgnoreCase("RSA")) {
            return encodeRSAKeys(keyPair);
        } else if (keyPair.getPrivate().getAlgorithm().equalsIgnoreCase("EC")) {
            return encodeECKeys(keyPair);
        }

        return encodedKeys;
    }

    private Map<KeyType, String> encodeECKeys(KeyPair keyPair) {
        Map<KeyType, String> encodedKeys = new EnumMap<>(KeyType.class);
        encodedKeys.put(KeyType.PRIVATE, base64Encoder.encodeToString(keyPair.getPrivate().getEncoded()));
        encodedKeys.put(KeyType.PUBLIC, base64Encoder.encodeToString(keyPair.getPublic().getEncoded()));

        return encodedKeys;
    }

    /***
     *
     * @param key
     * @return
     */
    public String stripHeaderFooter(String key) {
        return key.replace(RSA_PUBLIC_HEADER, "").replace(RSA_PUBLIC_FOOTER, "")
                .replace(RSA_PRIVATE_HEADER, "").replace(RSA_PRIVATE_FOOTER, "");
    }

    private Map<KeyType, String> encodeRSAKeys(KeyPair keyPair) {
        Map<KeyType, String> encodedKeys = new EnumMap<>(KeyType.class);

        encodedKeys.put(KeyType.PRIVATE, base64EncodeRSAPrivate(keyPair.getPrivate()));
        encodedKeys.put(KeyType.PUBLIC, base64EncodePublicKey(keyPair.getPublic()));

        return encodedKeys;
    }

    /***
     *
     * @param privateKey
     * @return
     */
    private String base64EncodeRSAPrivate(PrivateKey privateKey) {
        StringBuilder certificateBuilder = new StringBuilder();
        String base64String = base64Encoder.encodeToString(privateKey.getEncoded());
        certificateBuilder.append(RSA_PRIVATE_HEADER);
        certificateBuilder.append("\n");
        certificateBuilder.append(base64String.replaceAll(RSA_LINE_REGEX, "$1\n"));
        certificateBuilder.append("\n");
        certificateBuilder.append(RSA_PRIVATE_FOOTER);

        return certificateBuilder.toString();
    }

    /***
     *
     * @param publicKey
     * @return
     */
    public String base64EncodePublicKey(PublicKey publicKey) {
        StringBuilder certificateBuilder = new StringBuilder();
        String base64String = base64Encoder.encodeToString(publicKey.getEncoded());
        certificateBuilder.append(RSA_PUBLIC_HEADER);
        certificateBuilder.append("\n");
        certificateBuilder.append(base64String.replaceAll(RSA_LINE_REGEX, "$1\n"));
        certificateBuilder.append("\n");
        certificateBuilder.append(RSA_PUBLIC_FOOTER);

        return certificateBuilder.toString();
    }
}
