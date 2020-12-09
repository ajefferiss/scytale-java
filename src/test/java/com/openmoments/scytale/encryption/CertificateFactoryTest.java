package com.openmoments.scytale.encryption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertificateFactoryTest {

    @Test
    void shouldThrowWhenInvalidCertificateCreated() {
        Exception illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> new CertificateFactory().get(null));

        assertEquals("Certificate type cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    void shouldReturnNewInstanceOfRSA() {
        AsymmetricCertificate certificate = new CertificateFactory().get(CertificateType.RSA);

        assertNotNull(certificate);
        assertTrue(certificate instanceof RSACertificate);
    }
}