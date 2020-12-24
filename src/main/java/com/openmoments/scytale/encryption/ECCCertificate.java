package com.openmoments.scytale.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.*;

public final class ECCCertificate implements AsymmetricCertificate {
    protected static final String ALGORITHM = "EC";
    protected static final String EC_GEN_PARAMETER_SPEC = "secp256r1";
    private static final int DEFAULT_KEY_SIZE = -1;
    private static final String PROVIDER = "BC";
    private int keySize = DEFAULT_KEY_SIZE;

    ECCCertificate() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /***
     * Generate a EC KeyPair
     * @return {@link KeyPair KeyPair}
     * @throws NoSuchAlgorithmException - if a EC instance cannot be created
     * @throws NoSuchProviderException - if the Security Provider does not exist
     * @throws InvalidAlgorithmParameterException - If the EC generation algorithm does not exist
     */
    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
        keyPairGenerator.initialize(new ECGenParameterSpec(EC_GEN_PARAMETER_SPEC));

        return keyPairGenerator.generateKeyPair();
    }

    @Override
    public ECCCertificate length(int keyLength) {
        this.keySize = keyLength;
        return this;
    }

    /***
     * Generates a public and private key combination which are written to output streams
     * @param privateKeyOutputStream - Output stream for the private key
     * @param publicKeyOutputStream - Output stream for the public key
     * @throws IOException - Indicates an error writing to a output stream
     * @throws NoSuchAlgorithmException - if a EC instance cannot be created
     */
    @Override
    public void toStream(OutputStream privateKeyOutputStream, OutputStream publicKeyOutputStream) throws IOException, NoSuchAlgorithmException {
        try {
            KeyPair keyPair = generateKeyPair();
            privateKeyOutputStream.write(keyPair.getPrivate().getEncoded());
            publicKeyOutputStream.write(keyPair.getPublic().getEncoded());
        } catch (InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    /***
     * Create a KeyPair from input streams
     * @param privateKeyInputStream - Private key {@link InputStream InputStream}
     * @param publicKeyInputStream - Public Key {@link InputStream InputStream}
     * @return {@link KeyPair KeyPair}
     * @throws IOException - if there is an error reading from the input stream
     * @throws NoSuchAlgorithmException - if a EC certificate instance could not be created
     * @throws InvalidKeySpecException - if the EC key was invalid
     */
    @Override
    public KeyPair fromStream(InputStream privateKeyInputStream, InputStream publicKeyInputStream) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = privateKeyInputStream.readAllBytes();
        byte[] publicKeyBytes = publicKeyInputStream.readAllBytes();

        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        } catch (NoSuchProviderException e) {
            keyFactory = KeyFactory.getInstance(ALGORITHM);
        }

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        return new KeyPair(publicKey, privateKey);
    }
}
