package com.openmoments.scytale.encryption;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

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

        encodedKeys.put(KeyType.PRIVATE, base64EncodePrivateKey(keyPair.getPrivate()));
        encodedKeys.put(KeyType.PUBLIC, base64EncodePublicKey(keyPair.getPublic()));

        return encodedKeys;
    }

    /***
     * Remove the RSA Public and Private header and footer from a string
     * @param key {@link String String} to strip header and footer from
     * @return {@link String String} without header or footer
     */
    public String stripHeaderFooter(String key) {
        return key.replace(RSA_PUBLIC_HEADER, "").replace(RSA_PUBLIC_FOOTER, "")
                .replace(RSA_PRIVATE_HEADER, "").replace(RSA_PRIVATE_FOOTER, "");
    }

    /***
     * Encode a {@link PublicKey PublicKey} as {@link Base64 Base64}
     * @param publicKey {@link PublicKey PublicKey} to encode
     * @return {@link String String} containing Base64 encoded string
     */
    public String base64EncodePublicKey(PublicKey publicKey) {
        if (publicKey.getAlgorithm().equalsIgnoreCase(RSACertificate.ALGORITHM)) {
            return base64EncodeRSAPublicKey(publicKey);
        } else if (publicKey.getAlgorithm().equalsIgnoreCase(ECCCertificate.ALGORITHM)) {
            return base64EncodeECCPublicKey(publicKey);
        }

        throw new IllegalArgumentException("Public Key algorithm is unsupported");
    }

    /***
     * Attempt to decode a given Base64 encoded Public Key
     * @param encoded {@link String String} containing base64 encoded PublicKey
     * @param certificateType {@link CertificateType CertificateType} to decode String as
     * @return {@link PublicKey PublicKey}
     * @throws InvalidKeySpecException - When loaded key has invalid specification
     * @throws NoSuchAlgorithmException - Key algorithm is not available
     */
    public PublicKey base64DecodePublicKey(String encoded, CertificateType certificateType) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        switch (certificateType) {
            case RSA:
                return getRSAPublicKey(encoded);
            case ECC:
                return getECCPublicKey(encoded);
            default:
                throw new IllegalArgumentException("Certificate type " + certificateType + " not supported");
        }
    }

    /***
     * Base64 encode a given private key
     * @param privateKey {@link PrivateKey PrivateKey} to encode
     * @return {@link String String} containing encoded private key
     */
    public String base64EncodePrivateKey(PrivateKey privateKey) {
        if (privateKey.getAlgorithm().equalsIgnoreCase(RSACertificate.ALGORITHM)) {
            return base64EncodeRSAPrivate(privateKey);
        } else if (privateKey.getAlgorithm().equalsIgnoreCase(ECCCertificate.ALGORITHM)) {
            return base64EncodeECCPrivate(privateKey);
        }

        throw new IllegalArgumentException("Public Key algorithm is unsupported");
    }

    /***
     * Decode a Base64 encoded private key
     * @param encoded {@link String String} base64 key to decode
     * @param certificateType {@link CertificateType CertificateType} of the encoded key
     * @return {@link PrivateKey PrivateKey} instance of private key
     * @throws NoSuchAlgorithmException - Private Key algorithm is not available
     * @throws NoSuchProviderException - Security provider is not available
     * @throws InvalidKeySpecException - When loaded key has invalid specification
     * @throws IllegalArgumentException - {@link CertificateType CertificateType} is not supported
     */
    public PrivateKey base64DecodePrivateKey(String encoded, CertificateType certificateType) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        switch (certificateType) {
            case RSA:
                return getRSAPrivateKey(encoded);
            case ECC:
                return getECCPrivateKey(encoded);
            default:
                throw new IllegalArgumentException("Certificate type " + certificateType + " not supported");
        }
    }

    private String base64EncodeRSAPublicKey(PublicKey publicKey) {
        StringBuilder certificateBuilder = new StringBuilder();
        String base64String = base64Encoder.encodeToString(publicKey.getEncoded());
        certificateBuilder.append(RSA_PUBLIC_HEADER);
        certificateBuilder.append("\n");
        certificateBuilder.append(base64String.replaceAll(RSA_LINE_REGEX, "$1\n"));
        certificateBuilder.append("\n");
        certificateBuilder.append(RSA_PUBLIC_FOOTER);

        return certificateBuilder.toString();
    }

    private String base64EncodeECCPublicKey(PublicKey publicKey) {
        return base64Encoder.encodeToString(publicKey.getEncoded());
    }

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

    private String base64EncodeECCPrivate(PrivateKey privateKey) {
        return base64Encoder.encodeToString(privateKey.getEncoded());
    }

    private PrivateKey getRSAPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = stripHeaderFooter(key);
        key = key.replace("\n", "");

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(RSACertificate.ALGORITHM);

        return keyFactory.generatePrivate(keySpec);
    }

    private PrivateKey getECCPrivateKey(String key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance(ECCCertificate.ALGORITHM, Encryptor.PROVIDER);
        byte[] bytes = Base64.getDecoder().decode(key);
        return kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private PublicKey getRSAPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = stripHeaderFooter(key);
        key = key.replace("\n", "");

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(RSACertificate.ALGORITHM);

        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    private PublicKey getECCPublicKey(String key) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(ECCCertificate.ALGORITHM, Encryptor.PROVIDER);
        byte[] bytes = Base64.getDecoder().decode(key);
        return keyFactory.generatePublic(new X509EncodedKeySpec(bytes));
    }
}
