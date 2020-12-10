package com.openmoments.scytale.encryption;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public interface AsymmetricCertificate {
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException;
    AsymmetricCertificate length(int keyLength);
}
