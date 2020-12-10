package com.openmoments.scytale.encryption;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;

import static org.junit.jupiter.api.Assertions.*;

class RSACertificateTest {

    @Test
    void shouldGenerateCertificate() throws NoSuchAlgorithmException {
        KeyPair keyPair = new RSACertificate().generateKeyPair();

        assertEquals("RSA", keyPair.getPrivate().getAlgorithm());
        assertEquals("RSA", keyPair.getPublic().getAlgorithm());
        assertEquals("X.509", keyPair.getPublic().getFormat());
    }

    @Test
    void shouldGenerateCertificateWithKeyLength() throws NoSuchAlgorithmException {
        KeyPair keyPair = new RSACertificate().length(1024).generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();

        assertEquals("RSA", keyPair.getPrivate().getAlgorithm());
        assertEquals(1024, privateKey.getModulus().bitLength());
    }
}