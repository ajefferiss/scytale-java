package com.openmoments.scytale.encryption;

import com.openmoments.scytale.entities.PublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Encryptor {

    public Encryptor() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /***
     * Encrypts data
     * @param data {@link String String} data to encrypt
     * @param publicKeyList {@link List List} of {@link PublicKey PublicKey}'s to use for encryption
     * @return {@link String String} of encrypted data
     * @throws NoSuchPaddingException - Padding requested by not available
     * @throws NoSuchAlgorithmException - Encryption algorithm is not available
     * @throws InvalidKeySpecException - Key specifications are invalid
     * @throws InvalidKeyException - Key invalid (invalid encoding, wrong length, uninitialized, etc).
     * @throws BadPaddingException - Input data is not correctly padding for selected padding mechanism
     * @throws IllegalBlockSizeException - Block size for block cipher is incorrect
     * @throws NoSuchProviderException - Security provider is not available
     */
    public List<String> encrypt(String data, List<PublicKey> publicKeyList) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        List<String> encryptedData = new ArrayList<>();

        Provider[] providers = Security.getProviders();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");

        for (PublicKey publicKey : publicKeyList) {
            cipher.init(Cipher.ENCRYPT_MODE, getKey(publicKey.getPublicKey()));
            byte[] encByte = cipher.doFinal(data.getBytes());

            encryptedData.add(Base64.getEncoder().encodeToString(encByte));
        }

        return encryptedData;
    }

    /***
     * Attempts decryption of ciphertext
     * @param data {@link String String} to be decrypted
     * @param privateKey {@link PrivateKey PrivateKey} to decrypt with
     * @return {@link String String} plaintext
     * @throws NoSuchPaddingException - Padding requested by not available
     * @throws NoSuchAlgorithmException - Encryption algorithm is not available
     * @throws BadPaddingException - Input data is not correctly padding for selected padding mechanism
     * @throws IllegalBlockSizeException - Block size for block cipher is incorrect
     * @throws InvalidKeyException - Key invalid (invalid encoding, wrong length, uninitialized, etc).
     * @throws NoSuchProviderException - Security provider is not available
     */
    public String decrypt(String data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes())));
    }

    /***
     * Attempts decryption of ciphertext
     * @param data {@link String String} to be decrypted
     * @param base64PrivateKey {@link Base64 Base64} encoded private key to decrypt with
     * @return {@link String String} plaintext
     * @throws NoSuchPaddingException - Padding requested by not available
     * @throws NoSuchAlgorithmException - Encryption algorithm is not available
     * @throws BadPaddingException - Input data is not correctly padding for selected padding mechanism
     * @throws IllegalBlockSizeException - Block size for block cipher is incorrect
     * @throws InvalidKeyException - Key invalid (invalid encoding, wrong length, uninitialized, etc).
     * @throws NoSuchProviderException - Security provider is not available
     */
    public String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, NoSuchProviderException {
        return decrypt(data, getPrivateKey(base64PrivateKey));
    }

    private java.security.PublicKey getKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        key = new CertificateEncoder().stripHeaderFooter(key);
        key = key.replace("\n", "");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private java.security.PrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = new CertificateEncoder().stripHeaderFooter(key);
        key = key.replace("\n", "");

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }
}
