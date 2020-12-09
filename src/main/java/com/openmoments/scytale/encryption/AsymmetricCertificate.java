package com.openmoments.scytale.encryption;

import java.security.KeyPair;

public interface AsymmetricCertificate {
    public KeyPair generateKeyPair();

}
