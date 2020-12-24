package com.openmoments.scytale.encryption;

import com.openmoments.scytale.entities.ScytalePublicKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Encryptor")
class EncryptorTest {

    private Encryptor encryptor;
    private AsymmetricCertificate rsaCertificate;
    private AsymmetricCertificate eccCertificate;

    @BeforeEach
    void setup() {
        encryptor = new Encryptor();
        rsaCertificate = new CertificateFactory().get(CertificateType.RSA);
        eccCertificate = new CertificateFactory().get(CertificateType.ECC);
    }

    @Nested
    @DisplayName("Encryption Test")
    class Encryption {
        @Test
        @DisplayName("Should return empty list when no keys provided")
        void shouldReturnEmptyList() throws Exception {
            KeyPair rsaKeyPair = rsaCertificate.generateKeyPair();
            KeyPair eccKeyPair = eccCertificate.generateKeyPair();

            List<ScytalePublicKey> scytalePublicKeys = new ArrayList<>();

            List<String> rsaCipherText = encryptor.rsaEncrypt("Test", scytalePublicKeys);
            List<String> eccCipherText = encryptor.eccEncrypt("Test", eccKeyPair.getPrivate(), scytalePublicKeys);

            assertEquals(0, rsaCipherText.size());
            assertEquals(0, eccCipherText.size());
        }


        @Test
        @DisplayName("Encrypt to RSA")
        void shouldEncryptionRSA() throws Exception {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.rsaEncrypt("Test", scytalePublicKeys);

            assertEquals(1, cipherText.size());
            assertNotEquals("Test", cipherText.get(0));
        }

        @Test
        @DisplayName("Encrypt to EC")
        void shouldEncryptEC() throws Exception {
            KeyPair keyPair = eccCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.eccEncrypt("Test", keyPair.getPrivate(), scytalePublicKeys);

            assertEquals(1, cipherText.size());
            assertNotEquals("Test", cipherText.get(0));
        }

        @Test
        @DisplayName("Should not produce same RSA cipher text")
        void shouldNotProduceSameCipherText() throws Exception {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.rsaEncrypt("Test", scytalePublicKeys);
            List<String> cipherText2 = encryptor.rsaEncrypt("Test", scytalePublicKeys);

            assertNotEquals(cipherText2.get(0), cipherText.get(0));
        }

        @Test
        @DisplayName("Should encrypt for each RSA public key")
        void shouldEncryptWithEachPublicKey() throws Exception {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            KeyPair keyPair1 = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);
            Map<CertificateEncoder.KeyType, String> encodedKeys1 = new CertificateEncoder().base64Encode(keyPair1);

            List<ScytalePublicKey> scytalePublicKeys = List.of(
                new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)),
                new ScytalePublicKey(1L, encodedKeys1.get(CertificateEncoder.KeyType.PUBLIC))
            );

            List<String> cipherText = encryptor.rsaEncrypt("Test", scytalePublicKeys);

            assertEquals(2, cipherText.size());
            assertNotEquals(cipherText.get(0), cipherText.get(1));
         }
    }

    @Nested
    @DisplayName("Decryption Test")
    class Decryption {
        @Test
        @DisplayName("Should decrypt RSA text")
        void shouldDecryptText() throws Exception {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.rsaEncrypt("Test", scytalePublicKeys);

            assertEquals("Test", encryptor.rsaDecrypt(cipherText.get(0), keyPair.getPrivate()));
        }

        @Test
        @DisplayName("Should decrypt ECC text")
        void shouldDecryptECCText() throws Exception {
            KeyPair keyPair = eccCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.eccEncrypt("Test", keyPair.getPrivate(), scytalePublicKeys);
            assertEquals("Test", encryptor.eccDecrypt(cipherText.get(0), keyPair.getPrivate(), scytalePublicKeys));
        }

        @Test
        @DisplayName("Should decrypt when private key base64")
        void shouldDecryptWithStringPrivateKey() throws Exception {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.rsaEncrypt("Test", scytalePublicKeys);

            assertEquals("Test", encryptor.rsaDecrypt(cipherText.get(0), encodedKeys.get(CertificateEncoder.KeyType.PRIVATE)));
        }
    }
}