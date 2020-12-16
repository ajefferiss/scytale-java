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
     *
     * @param data
     * @param publicKeyList
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public List<String> encrypt(String data, List<PublicKey> publicKeyList) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        List<String> encryptedData = new ArrayList<>();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");

        for (PublicKey publicKey : publicKeyList) {
            cipher.init(Cipher.ENCRYPT_MODE, getKey(publicKey.getPublicKey()));
            byte[] encByte = cipher.doFinal(data.getBytes());

            encryptedData.add(Base64.getEncoder().encodeToString(encByte));
        }

        return encryptedData;
    }

    public String decrypt(String data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes())));
    }

    public String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, NoSuchProviderException {
        //return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
        return decrypt(data, getPrivateKey(base64PrivateKey));
    }

    private java.security.PublicKey getKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        key = new CertificateEncoder().stripHeaderFooter(key);
        key = key.replaceAll("\n", "");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private java.security.PrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = new CertificateEncoder().stripHeaderFooter(key);
        key = key.replaceAll("\n", "");

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }
}
