package com.openmoments.scytale.encryption;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public enum CertificateType {
    RSA(RSACertificate::new);

    public final Supplier<AsymmetricCertificate> factory;
    CertificateType(Supplier<AsymmetricCertificate> factory) {
        this.factory = requireNonNull(factory);
    }
}
