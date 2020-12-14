package com.openmoments.scytale.encryption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RSA Certficiate")
class RSACertificateTest {

    @Test
    @DisplayName("Should create key pair")
    void shouldGenerateCertificate() throws NoSuchAlgorithmException {
        KeyPair keyPair = new RSACertificate().generateKeyPair();

        assertEquals("RSA", keyPair.getPrivate().getAlgorithm());
        assertEquals("RSA", keyPair.getPublic().getAlgorithm());
        assertEquals("X.509", keyPair.getPublic().getFormat());
    }

    @Test
    @DisplayName("Should use specified key length")
    void shouldGenerateCertificateWithKeyLength() throws NoSuchAlgorithmException {
        KeyPair keyPair = new RSACertificate().length(1024).generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();

        assertEquals("RSA", keyPair.getPrivate().getAlgorithm());
        assertEquals(1024, privateKey.getModulus().bitLength());
    }

    @Test
    @DisplayName("Should create new cert to stream")
    void shouldWriteKeysToStreams(@TempDir Path tempDir) throws IOException, NoSuchAlgorithmException {
        Path keyPath = tempDir.resolve("test.key");
        Path pubPath = tempDir.resolve("test.pub");
        OutputStream keyOutputStream = Files.newOutputStream(keyPath);
        OutputStream pubOutputStream = Files.newOutputStream(pubPath);

        new RSACertificate().toStream(keyOutputStream, pubOutputStream);

        assertNotEquals(0, Files.size(keyPath));
        assertNotEquals(0, Files.size(pubPath));
    }

    @Test
    @DisplayName("Should load cert from streams")
    void shouldLoadKeysFromStreams(@TempDir Path tempDir) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path keyPath = tempDir.resolve("test.key");
        Path pubPath = tempDir.resolve("test.pub");
        OutputStream keyOutputStream = Files.newOutputStream(keyPath);
        OutputStream pubOutputStream = Files.newOutputStream(pubPath);

        new RSACertificate().toStream(keyOutputStream, pubOutputStream);

        InputStream keyInputStream = Files.newInputStream(keyPath);
        InputStream pubInputStream = Files.newInputStream(pubPath);

        KeyPair loadedPair = new RSACertificate().fromStream(keyInputStream, pubInputStream);

        assertNotNull(loadedPair);
        assertNotEquals(0, loadedPair.getPrivate().getEncoded().length);
        assertEquals("RSA", loadedPair.getPublic().getAlgorithm());
        assertNotEquals(0, loadedPair.getPublic().getEncoded().length);
    }
}