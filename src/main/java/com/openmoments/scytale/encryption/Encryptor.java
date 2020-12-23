package com.openmoments.scytale.encryption;

import com.openmoments.scytale.entities.ScytalePublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Encryptor {

    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPwithSHA1andMGF1Padding";
    private static final String ECC_TRANSFORMATION = "ECIESwithAES";
    private static final String PROVIDER = "BC";

    public Encryptor() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /***
     * Encrypts data
     * @param data {@link String String} data to encrypt
     * @param scytalePublicKeyList {@link List List} of {@link ScytalePublicKey PublicKey}'s to use for encryption
     * @return {@link String String} of encrypted data
     * @throws NoSuchPaddingException - Padding requested by not available
     * @throws NoSuchAlgorithmException - Encryption algorithm is not available
     * @throws InvalidKeySpecException - Key specifications are invalid
     * @throws InvalidKeyException - Key invalid (invalid encoding, wrong length, uninitialized, etc).
     * @throws BadPaddingException - Input data is not correctly padding for selected padding mechanism
     * @throws IllegalBlockSizeException - Block size for block cipher is incorrect
     * @throws NoSuchProviderException - Security provider is not available
     */
    public List<String> encrypt(String data, List<ScytalePublicKey> scytalePublicKeyList) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidParameterSpecException {
        List<String> encryptedData = new ArrayList<>();
        if (scytalePublicKeyList.isEmpty()) {
            return encryptedData;
        }

        Cipher cipher;
        for (ScytalePublicKey scytalePublicKey : scytalePublicKeyList) {
            if (scytalePublicKey.getPublicKey().getAlgorithm().equalsIgnoreCase(RSACertificate.ALGORITHM)) {
                cipher = Cipher.getInstance(RSA_TRANSFORMATION, PROVIDER);
            } else {
                cipher = Cipher.getInstance(ECC_TRANSFORMATION, PROVIDER);
            }

            cipher.init(Cipher.ENCRYPT_MODE, scytalePublicKey.getPublicKey());
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
        Cipher cipher;

        if (privateKey.getAlgorithm().equalsIgnoreCase(RSACertificate.ALGORITHM)) {
            cipher = Cipher.getInstance(RSA_TRANSFORMATION, PROVIDER);
        } else {
            cipher = Cipher.getInstance(ECC_TRANSFORMATION, PROVIDER);
        }

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

    private java.security.PublicKey getPublicKey(String key) throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeySpecException, NoSuchProviderException {
        try {
            return getRSAPublicKey(key);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return getECCPublicKey(key);
        }
    }

    private java.security.PrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        try {
            return getRSAPrivateKey(key);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return getECCPrivateKey(key);
        }
    }

    private java.security.PublicKey getRSAPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = new CertificateEncoder().stripHeaderFooter(key);
        key = key.replace("\n", "");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private java.security.PrivateKey getRSAPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = new CertificateEncoder().stripHeaderFooter(key);
        key = key.replace("\n", "");

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }

    private java.security.PublicKey getECCPublicKey(String key) throws InvalidParameterSpecException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        String hexX = key.substring(0, 32);
        String hexY = key.substring(32);
        ECPoint point = new ECPoint(new BigInteger(hexX, 16), new BigInteger(hexY, 16));

        AlgorithmParameters parameters = AlgorithmParameters.getInstance(ECCCertificate.ALGORITHM, PROVIDER);
        parameters.init(new ECGenParameterSpec(ECCCertificate.EC_GEN_PARAMETER_SPEC));
        ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);
        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, ecParameters);

        return KeyFactory.getInstance(ECCCertificate.ALGORITHM, PROVIDER).generatePublic(pubKeySpec);
    }

    private java.security.PrivateKey getECCPrivateKey(String key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance(ECCCertificate.ALGORITHM, PROVIDER);
        byte[] bytes = Base64.getDecoder().decode(key);
        return kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }
}
