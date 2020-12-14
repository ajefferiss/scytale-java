package com.openmoments.scytale.encryption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Certificate Factory")
class CertificateFactoryTest {

    @Test
    @DisplayName("Should throw for invalid certs")
    void shouldThrowWhenInvalidCertificateCreated() {
        Exception illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> new CertificateFactory().get(null));

        assertEquals("Certificate type cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    @DisplayName("Should return a RSA certificate")
    void shouldReturnNewInstanceOfRSA() {
        AsymmetricCertificate certificate = new CertificateFactory().get(CertificateType.RSA);

        assertNotNull(certificate);
        assertTrue(certificate instanceof RSACertificate);
    }
}