package com.openmoments.scytale.encryption;

import com.openmoments.scytale.entities.ScytalePublicKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Encryptor")
class EncryptorTest {

    private Encryptor encryptor;
    private AsymmetricCertificate rsaCertificate;

    @BeforeEach
    void setup() {
        encryptor = new Encryptor();
        rsaCertificate = new CertificateFactory().get(CertificateType.RSA);
    }

    @Nested
    @DisplayName("Encryption Test")
    class Encryption {
        @Test
        @DisplayName("Encrypt to RSA")
        void shouldEncryptionRSA() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidKeySpecException, NoSuchPaddingException, NoSuchProviderException, InvalidParameterSpecException {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.encrypt("Test", scytalePublicKeys);

            assertEquals(1, cipherText.size());
            assertNotEquals("Test", cipherText.get(0));
        }

        @Test
        @DisplayName("Should not produce same cipher text")
        void shouldNotProduceSameCipherText() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidKeySpecException, NoSuchPaddingException, NoSuchProviderException, InvalidParameterSpecException {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.encrypt("Test", scytalePublicKeys);
            List<String> cipherText2 = encryptor.encrypt("Test", scytalePublicKeys);

            assertNotEquals(cipherText2.get(0), cipherText.get(0));
        }

        @Test
        @DisplayName("Should encrypt for each public key")
        void shouldEncryptWithEachPublicKey() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidKeySpecException, NoSuchPaddingException, NoSuchProviderException, InvalidParameterSpecException {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            KeyPair keyPair1 = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);
            Map<CertificateEncoder.KeyType, String> encodedKeys1 = new CertificateEncoder().base64Encode(keyPair1);

            List<ScytalePublicKey> scytalePublicKeys = List.of(
                new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)),
                new ScytalePublicKey(1L, encodedKeys1.get(CertificateEncoder.KeyType.PUBLIC))
            );

            List<String> cipherText = encryptor.encrypt("Test", scytalePublicKeys);

            assertEquals(2, cipherText.size());
            assertNotEquals(cipherText.get(0), cipherText.get(1));
         }
    }

    @Nested
    @DisplayName("Decryption Test")
    class Decryption {
        @Test
        @DisplayName("Should decrypt text")
        void shouldDecryptText() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidKeySpecException, NoSuchPaddingException, NoSuchProviderException, InvalidParameterSpecException {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.encrypt("Test", scytalePublicKeys);

            assertEquals("Test", encryptor.decrypt(cipherText.get(0), keyPair.getPrivate()));
        }

        @Test
        @DisplayName("Should decrypt when private key base64")
        void shouldDecryptWithStringPrivateKey() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException, InvalidParameterSpecException {
            KeyPair keyPair = rsaCertificate.generateKeyPair();
            Map<CertificateEncoder.KeyType, String> encodedKeys = new CertificateEncoder().base64Encode(keyPair);

            List<ScytalePublicKey> scytalePublicKeys = List.of(new ScytalePublicKey(1L, encodedKeys.get(CertificateEncoder.KeyType.PUBLIC)));
            List<String> cipherText = encryptor.encrypt("Test", scytalePublicKeys);

            assertEquals("Test", encryptor.decrypt(cipherText.get(0), encodedKeys.get(CertificateEncoder.KeyType.PRIVATE)));
        }
    }
}