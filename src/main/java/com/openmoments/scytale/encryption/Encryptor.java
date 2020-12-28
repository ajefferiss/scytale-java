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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Encryptor {

    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPwithSHA1andMGF1Padding";
    private static final String ECC_TRANSFORMATION = "ECIES";
    private final IESParameterSpec iesParameterSpec;
    protected static final String PROVIDER = "BC";

    public Encryptor() {
        Security.addProvider(new BouncyCastleProvider());
        byte[]  decrypt = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        byte[]  encrypt = new byte[] { 8, 7, 6, 5, 4, 3, 2, 1 };
        iesParameterSpec = new IESParameterSpec(decrypt, encrypt, 256);
    }

    /***
     * Performs encryption for each of the public keys provided
     * @param data {@link String String} to be encrypted
     * @param privateKey {@link PrivateKey PrivateKey} the private key of the user performing the encryption
     * @param scytalePublicKeys {@link List List} of {@link ScytalePublicKey ScytalePublicKey}'s of the recipient
     * @return {@link List List} of {@link String String}'s containing cipher text
     * @throws NoSuchPaddingException - Padding requested by not available
     * @throws InvalidAlgorithmParameterException - Incorrect ECC parameters
     * @throws NoSuchAlgorithmException - Encryption algorithm is not available
     * @throws IllegalBlockSizeException - Block size for block cipher is incorrect
     * @throws BadPaddingException - Input data is not correctly padding for selected padding mechanism
     * @throws NoSuchProviderException - Security provider is not available
     * @throws InvalidKeyException - Public Key invalid (invalid encoding, wrong length, uninitialized, etc).
     */
    public List<String> encrypt(String data, PrivateKey privateKey, List<ScytalePublicKey> scytalePublicKeys) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        List<String> encryptedData = new ArrayList<>();
        if (scytalePublicKeys.isEmpty()) {
            return encryptedData;
        }

        String privateKeyType = privateKey.getAlgorithm();
        String publicKeyType;

        for (ScytalePublicKey scytalePublicKey : scytalePublicKeys) {
            publicKeyType = scytalePublicKey.getPublicKey().getAlgorithm();

            if (publicKeyType.equalsIgnoreCase(RSACertificate.ALGORITHM)) {
                encryptedData.add(rsaEncrypt(data, scytalePublicKey));
            } else if (publicKeyType.equalsIgnoreCase(ECCCertificate.ALGORITHM) && publicKeyType.equalsIgnoreCase(privateKeyType)) {
                encryptedData.add(eccEncrypt(data, privateKey, scytalePublicKey));
            }
        }

        return encryptedData;
    }

    /***
     * Attempts decryption of ciphertext
     * @param data {@link String String} to be decrypted
     * @param privateKey {@link PrivateKey PrivateKey} of cipher text recipient to decrypt with
     * @param scytalePublicKeys {@link List List} of {@link ScytalePublicKey ScytalePublicKey}'s of encryptor to decrypt with
     * @return {@link String String} plaintext
     * @throws NoSuchPaddingException - Padding requested by not available
     * @throws NoSuchAlgorithmException - Decryption algorithm is not available
     * @throws IllegalBlockSizeException - Block size for block cipher is incorrect
     * @throws BadPaddingException - Input data is not correctly padding for selected padding mechanism
     * @throws NoSuchProviderException - Security provider is not available
     * @throws InvalidKeyException - Key invalid (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException - Incorrect ECC parameters
     */
    public String decrypt(String data, PrivateKey privateKey, List<ScytalePublicKey> scytalePublicKeys) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        String privateKeyType = privateKey.getAlgorithm();

        if (privateKeyType.equalsIgnoreCase(RSACertificate.ALGORITHM)) {
            return rsaDecrypt(data, privateKey);
        } else if (privateKeyType.equalsIgnoreCase(ECCCertificate.ALGORITHM)) {
            return eccDecrypt(data, privateKey, scytalePublicKeys);
        }

        return "";
    }

    private String rsaEncrypt(String data, ScytalePublicKey scytalePublicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, scytalePublicKey.getPublicKey());
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    private String eccEncrypt(String data, PrivateKey privateKey, ScytalePublicKey scytalePublicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ECC_TRANSFORMATION, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, new IEKeySpec(privateKey, scytalePublicKey.getPublicKey()), iesParameterSpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }


    private String rsaDecrypt(String data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION, PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes())));
    }

    private String eccDecrypt(String data, PrivateKey privateKey, List<ScytalePublicKey> scytalePublicKeys) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ECC_TRANSFORMATION, PROVIDER);
        for (ScytalePublicKey scytalePublicKey : scytalePublicKeys) {
            cipher.init(Cipher.DECRYPT_MODE, new IEKeySpec(privateKey, scytalePublicKey.getPublicKey()), iesParameterSpec);
            try {
                return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes())));
            } catch (BadPaddingException | IllegalBlockSizeException e) {}
        }

        return "";
    }
}
