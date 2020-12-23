package com.openmoments.scytale.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public interface AsymmetricCertificate {
    AsymmetricCertificate length(int keyLength);
    KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException;
    void toStream(OutputStream privateKeyOutputStream, OutputStream publicKeyOutputStream) throws IOException, NoSuchAlgorithmException;
    KeyPair fromStream(InputStream privateKeyInputStream, InputStream publicKeyInputStream) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;
}
