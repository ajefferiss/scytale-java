package com.openmoments.scytale.encryption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EC Certificate")
class ECCCertificateTest {

    @Test
    @DisplayName("Should create key pair")
    void shouldGenerateKeyPair() throws Exception {
        KeyPair keyPair = new ECCCertificate().generateKeyPair();

        assertEquals("EC", keyPair.getPrivate().getAlgorithm());
        assertEquals("EC", keyPair.getPublic().getAlgorithm());
    }

    @Test
    @DisplayName("Should create new cert to stream")
    void shouldWriteKeysToStreams(@TempDir Path tempDir) throws Exception {
        Path keyPath = tempDir.resolve("test.key");
        Path pubPath = tempDir.resolve("test.pub");
        OutputStream keyOutputStream = Files.newOutputStream(keyPath);
        OutputStream pubOutputStream = Files.newOutputStream(pubPath);

        new ECCCertificate().toStream(keyOutputStream, pubOutputStream);

        assertNotEquals(0, Files.size(keyPath));
        assertNotEquals(0, Files.size(pubPath));
    }

    @Test
    @DisplayName("Should load cert from streams")
    void shouldLoadKeysFromStream(@TempDir Path tempDir) throws Exception {
        Path keyPath = tempDir.resolve("test.key");
        Path pubPath = tempDir.resolve("test.pub");
        OutputStream keyOutputStream = Files.newOutputStream(keyPath);
        OutputStream pubOutputStream = Files.newOutputStream(pubPath);

        new ECCCertificate().toStream(keyOutputStream, pubOutputStream);

        InputStream keyInputStream = Files.newInputStream(keyPath);
        InputStream pubInputStream = Files.newInputStream(pubPath);

        KeyPair loadedPair = new ECCCertificate().fromStream(keyInputStream, pubInputStream);

        assertNotNull(loadedPair);
        assertNotEquals(0, loadedPair.getPrivate().getEncoded().length);
        assertEquals("EC", loadedPair.getPublic().getAlgorithm());
        assertNotEquals(0, loadedPair.getPublic().getEncoded().length);
    }
}