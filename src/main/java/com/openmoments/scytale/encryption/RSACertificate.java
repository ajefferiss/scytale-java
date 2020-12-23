package com.openmoments.scytale.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public final class RSACertificate implements AsymmetricCertificate {

    protected static final String ALGORITHM = "RSA";
    private static final int DEFAULT_KEY_SIZE = 4096;
    private int keySize = DEFAULT_KEY_SIZE;

    /***
     * Generate a RSA Keypair
     * @return KeyPair
     * @throws NoSuchAlgorithmException Indicates that a RSA certificate instance could not be created
     */
    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg;
        kpg = KeyPairGenerator.getInstance(ALGORITHM);
        kpg.initialize(keySize);

        return kpg.generateKeyPair();
    }

    /***
     * Specify the key length to use when creating certificates, defaults to 4096 bits
     * @param keyLength Key length value to use
     * @return Instance of RSACertificate
     */
    @Override
    public RSACertificate length(int keyLength) {
        this.keySize = keyLength;
        return this;
    }

    /***
     * Generates a public and private key combination which are written to output streams
     * @param privateKeyOutputStream Output stream for the private key
     * @param publicKeyOutputStream Output stream for the public key
     * @throws IOException Indicates an error writing to a output stream
     * @throws NoSuchAlgorithmException Indicates that a RSA certificate instance could not be created
     */
    @Override
    public void toStream(OutputStream privateKeyOutputStream, OutputStream publicKeyOutputStream) throws IOException, NoSuchAlgorithmException {
        KeyPair keyPair = generateKeyPair();
        privateKeyOutputStream.write(keyPair.getPrivate().getEncoded());
        publicKeyOutputStream.write(keyPair.getPublic().getEncoded());
    }

    /***
     * Create a KeyPair from input streams
     * @param privateKeyInputStream Private key input stream
     * @param publicKeyInputStream Public Key input stream
     * @return KeyPair created
     * @throws IOException Indicates error reading from the input stream
     * @throws NoSuchAlgorithmException Indicates that a RSA certificate instance could not be created
     * @throws InvalidKeySpecException Indicates that the RSA key was an invalid type
     */
    @Override
    public KeyPair fromStream(InputStream privateKeyInputStream, InputStream publicKeyInputStream) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = privateKeyInputStream.readAllBytes();
        byte[] publicKeyBytes = publicKeyInputStream.readAllBytes();

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        return new KeyPair(publicKey, privateKey);
    }
}
