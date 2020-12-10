package com.openmoments.scytale.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/*
https://www.novixys.com/blog/how-to-generate-rsa-keys-java/
 */

public class RSACertificate implements AsymmetricCertificate {

    private static final Logger LOGGER = Logger.getLogger(RSACertificate.class.getName());
    private static final int DEFAULT_KEY_SIZE = 4096;
    private static final String ALGORITHM = "RSA";
    private int keySize = DEFAULT_KEY_SIZE;

    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg;
        kpg = KeyPairGenerator.getInstance(ALGORITHM);
        kpg.initialize(keySize);

        return kpg.generateKeyPair();
    }

    @Override
    public RSACertificate length(int keyLength) {
        this.keySize = keyLength;
        return this;
    }
}
