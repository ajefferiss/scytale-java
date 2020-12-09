package com.openmoments.scytale.encryption;

public class CertificateFactory {

    public AsymmetricCertificate get(CertificateType certificateType) {
        if (certificateType == null) {
            throw new IllegalArgumentException("Certificate type cannot be null");
        }

        String certValue = "";

        if (certificateType == CertificateType.RSA) {
            certValue = "RSA";
        }

        if (certValue.isEmpty()) {
            throw new IllegalArgumentException("Certificate type not set, cannot generate certificate");
        }

        return CertificateType.valueOf(certValue).factory.get();
    }

}
