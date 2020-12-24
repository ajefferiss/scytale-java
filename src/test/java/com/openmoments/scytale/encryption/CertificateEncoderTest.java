package com.openmoments.scytale.encryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Certificate Encoder")
class CertificateEncoderTest {

    //TODO: Add ECC Tests

    private CertificateEncoder certificateEncoder;

    @BeforeEach
    void setup() {
        certificateEncoder = new CertificateEncoder();
    }

    @Test
    @DisplayName("Should return empty when null passed")
    void shouldReturnEmptyListWhenNull() {
        assertTrue(certificateEncoder.base64Encode(null).isEmpty());
    }

    @Test
    @DisplayName("Should base64 encode RSA Certificate")
    void shouldReturnRSAKeys() throws Exception {
        KeyPair keyPair = new CertificateFactory().get(CertificateType.RSA).generateKeyPair();
        assertNotNull(keyPair);

        Map<CertificateEncoder.KeyType, String> encodedKeys = certificateEncoder.base64Encode(keyPair);

        assertEquals(2, encodedKeys.size());
        assertTrue(encodedKeys.containsKey(CertificateEncoder.KeyType.PRIVATE));
        assertTrue(encodedKeys.containsKey(CertificateEncoder.KeyType.PUBLIC));

        String privateKey = encodedKeys.get(CertificateEncoder.KeyType.PRIVATE);
        String publicKey = encodedKeys.get(CertificateEncoder.KeyType.PUBLIC);

        assertTrue(privateKey.startsWith(CertificateEncoder.RSA_PRIVATE_HEADER));
        assertTrue(privateKey.endsWith(CertificateEncoder.RSA_PRIVATE_FOOTER));
        assertTrue(privateKey.length() >= 3200);

        assertTrue(publicKey.startsWith(CertificateEncoder.RSA_PUBLIC_HEADER));
        assertTrue(publicKey.endsWith(CertificateEncoder.RSA_PUBLIC_FOOTER));
        assertTrue(publicKey.length() >= 800);
    }

    @Test
    @DisplayName("Should base64 encode ECC Certificate")
    void shouldReturnECCKeys() throws Exception {
        KeyPair keyPair = new CertificateFactory().get(CertificateType.ECC).generateKeyPair();
        assertNotNull(keyPair);

        Map<CertificateEncoder.KeyType, String> encodedKeys = certificateEncoder.base64Encode(keyPair);

        assertEquals(2, encodedKeys.size());
        assertNotEquals("", encodedKeys.get(CertificateEncoder.KeyType.PRIVATE));
        assertNotEquals("", encodedKeys.get(CertificateEncoder.KeyType.PUBLIC));
    }
}