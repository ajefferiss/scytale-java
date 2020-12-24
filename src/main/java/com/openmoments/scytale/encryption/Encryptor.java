package com.openmoments.scytale.encryption;

import com.openmoments.scytale.entities.ScytalePublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IEKeySpec;
import org.bouncycastle.jce.spec.IESParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Encryptor {

    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPwithSHA1andMGF1Padding";
    private static final String ECC_TRANSFORMATION = "ECIES";
    private static final String PROVIDER = "BC";
    private final IESParameterSpec iesParameterSpec;

    public Encryptor() {
        Security.addProvider(new BouncyCastleProvider());
        byte[]  decrypt = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        byte[]  encrypt = new byte[] { 8, 7, 6, 5, 4, 3, 2, 1 };
        iesParameterSpec = new IESParameterSpec(decrypt, encrypt, 256);
    }

    /***
     * Encrypts data
     * @param data {@link String String} data to encrypt
     * @param scytalePublicKeyList {@link List List} of {@link ScytalePublicKey PublicKey}'s to use for encryption
     * @return {@link String String} of encrypted data
     * @throws NoSuchPaddingException - Padding requested by not available
     * @throws NoSuchAlgorithmException - Encryption algorithm is not available
     * @throws InvalidKeyException - Key invalid (invalid encoding, wrong length, uninitialized, etc).
     * @throws BadPaddingException - Input data is not correctly padding for selected padding mechanism
     * @throws IllegalBlockSizeException - Block size for block cipher is incorrect
     * @throws NoSuchProviderException - Security provider is not available
     */
    public List<String> rsaEncrypt(String data, List<ScytalePublicKey> scytalePublicKeyList) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {
        List<String> encryptedData = new ArrayList<>();
        if (scytalePublicKeyList.isEmpty()) {
            return encryptedData;
        }

        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION, PROVIDER);
        for (ScytalePublicKey scytalePublicKey : scytalePublicKeyList) {
            if (!scytalePublicKey.getPublicKey().getAlgorithm().equalsIgnoreCase(RSACertificate.ALGORITHM)) {
                continue;
            }

            cipher.init(Cipher.ENCRYPT_MODE, scytalePublicKey.getPublicKey());
            encryptedData.add(Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes())));
        }

        return encryptedData;
    }

    /***
     * Encrypts data
     * @param data {@link String String} data to encrypt
     * @param privateKey ECC {@link PrivateKey PrivateKey} to encrypt with
     * @param scytalePublicKeys {@link List List} of {@link ScytalePublicKey ScytalePublicKey}'s to use for encryption
     * @return {@link List List} of String encrypted data
     * @throws NoSuchPaddingException - Padding requested by not available
     * @throws NoSuchAlgorithmException - Encryption algorithm is not available
     * @throws NoSuchProviderException - Security provider is not available
     * @throws InvalidAlgorithmParameterException - Incorrect ECC parameters
     * @throws InvalidKeyException - Key invalid (invalid encoding, wrong length, uninitialized, etc).
     * @throws BadPaddingException - Input data is not correctly padding for selected padding mechanism
     * @throws IllegalBlockSizeException - Block size for block cipher is incorrect
     */
    public List<String> eccEncrypt(String data, PrivateKey privateKey, List<ScytalePublicKey> scytalePublicKeys) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        List<String> encryptedData = new ArrayList<>();
        if (scytalePublicKeys.isEmpty()) {
            return encryptedData;
        }

        Cipher cipher = Cipher.getInstance(ECC_TRANSFORMATION, PROVIDER);
        for (ScytalePublicKey scytalePublicKey : scytalePublicKeys) {
            if (!scytalePublicKey.getPublicKey().getAlgorithm().equalsIgnoreCase(ECCCertificate.ALGORITHM)) {
                continue;
            }

            cipher.init(Cipher.ENCRYPT_MODE, new IEKeySpec(privateKey, scytalePublicKey.getPublicKey()), iesParameterSpec);
            encryptedData.add(Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes())));
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
    public String rsaDecrypt(String data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchProviderException {
        if (!privateKey.getAlgorithm().equalsIgnoreCase(RSACertificate.ALGORITHM)) {
            return "";
        }

        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION, PROVIDER);
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
    public String rsaDecrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, NoSuchProviderException {
        return rsaDecrypt(data, getPrivateKey(base64PrivateKey));
    }

    /***
     * Attempts decryption of ciphertext
     * @param data {@link String String} to be decrypted
     * @param privateKey ECC {@link PrivateKey PrivateKey} to decrypt with
     * @param scytalePublicKeys {@link List List} of {@link ScytalePublicKey ScytalePublicKey}'s to use for decryption
     * @return {@link String String} plaintext
     * @throws NoSuchPaddingException - Padding requested by not available
     * @throws NoSuchAlgorithmException - Encryption algorithm is not available
     * @throws NoSuchProviderException - Security provider is not available
     * @throws InvalidAlgorithmParameterException - Incorrect ECC parameters
     * @throws InvalidKeyException - Key invalid (invalid encoding, wrong length, uninitialized, etc).
     */
    public String eccDecrypt(String data, PrivateKey privateKey, List<ScytalePublicKey> scytalePublicKeys) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        if (!privateKey.getAlgorithm().equalsIgnoreCase(ECCCertificate.ALGORITHM)) {
            return "";
        }

        Cipher cipher = Cipher.getInstance(ECC_TRANSFORMATION, PROVIDER);
        for (ScytalePublicKey scytalePublicKey : scytalePublicKeys) {
            cipher.init(Cipher.DECRYPT_MODE, new IEKeySpec(privateKey, scytalePublicKey.getPublicKey()), iesParameterSpec);
            try {
                return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes())));
            } catch (BadPaddingException | IllegalBlockSizeException e) {}
        }

        return "";
    }

    private PrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        try {
            return getRSAPrivateKey(key);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return getECCPrivateKey(key);
        }
    }

    private PrivateKey getRSAPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = new CertificateEncoder().stripHeaderFooter(key);
        key = key.replace("\n", "");

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }


    private PrivateKey getECCPrivateKey(String key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance(ECCCertificate.ALGORITHM, PROVIDER);
        byte[] bytes = Base64.getDecoder().decode(key);
        return kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }
}
