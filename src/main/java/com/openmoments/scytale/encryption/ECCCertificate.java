package com.openmoments.scytale.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

public final class ECCCertificate implements AsymmetricCertificate {
    private static final int DEFAULT_KEY_SIZE = -1;
    private static final String ALGORITHM = "EC";
    private static final String PROVIDER = "BC";
    private static final String EC_GEN_PARAMETER_SPEC = "brainpoolP384r1";
    private int keySize = DEFAULT_KEY_SIZE;

    ECCCertificate() {
        Security.addProvider(new BouncyCastleProvider());
    }


    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);

        try {
            keyPairGenerator.initialize(new ECGenParameterSpec(EC_GEN_PARAMETER_SPEC));
        } catch (InvalidAlgorithmParameterException e) {
            return null;
        }

        return keyPairGenerator.generateKeyPair();
    }

    @Override
    public ECCCertificate length(int keyLength) {
        this.keySize = keyLength;
        return this;
    }

    @Override
    public void toStream(OutputStream privateKeyOutputStream, OutputStream publicKeyOutputStream) throws IOException, NoSuchAlgorithmException {

    }

    @Override
    public KeyPair fromStream(InputStream privateKeyInputStream, InputStream publicKeyInputStream) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return null;
    }
}
