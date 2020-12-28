package com.openmoments.scytale.entities;

import com.openmoments.scytale.encryption.CertificateEncoder;
import com.openmoments.scytale.encryption.CertificateType;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

public final class ScytalePublicKey {
    private final Long id;
    private final PublicKey publicKey;

    public ScytalePublicKey(Long id, String publicKey) {
        this.id = id;
        this.publicKey = getPublicKeyFromBase64(publicKey);
    }

    public Long getId() {
        return id;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScytalePublicKey scytalePublicKey1 = (ScytalePublicKey) o;
        return id.equals(scytalePublicKey1.id) && publicKey.equals(scytalePublicKey1.publicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicKey);
    }

    @Override
    public String toString() {
        return "PublicKey{" + "id=" + id + ", publicKey='" + publicKey + "'}";
    }

    private PublicKey getPublicKeyFromBase64(String key) {
        CertificateEncoder certificateEncoder = new CertificateEncoder();
        try {
            return certificateEncoder.base64DecodePublicKey(key, CertificateType.RSA);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            try {
                return certificateEncoder.base64DecodePublicKey(key, CertificateType.ECC);
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException noSuchAlgorithmException) {
                return null;
            }
        }

    }
}
