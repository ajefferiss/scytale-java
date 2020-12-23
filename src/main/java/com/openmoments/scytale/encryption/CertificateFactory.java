package com.openmoments.scytale.encryption;

public final class CertificateFactory {

    /***
     * Generate a asymmetric certificate of type {@link com.openmoments.scytale.encryption.CertificateType CertificateType}
     * @param certificateType Type of certificate to generate
     * @return Instance of the concrete class to implement {@code certificateType}
     */
    public AsymmetricCertificate get(CertificateType certificateType) {
        if (certificateType == null) {
            throw new IllegalArgumentException("Certificate type cannot be null");
        }

        String certValue;
        switch (certificateType) {
            case RSA:
                certValue = "RSA";
                break;
            case ECC:
                certValue = "ECC";
                break;
            default:
                throw new IllegalArgumentException("Certificate type not set, cannot generate certificate");
        }

        return CertificateType.valueOf(certValue).factory.get();
    }

}
