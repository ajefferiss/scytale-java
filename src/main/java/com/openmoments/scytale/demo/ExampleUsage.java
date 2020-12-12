package com.openmoments.scytale.demo;

import com.openmoments.scytale.encryption.CertificateEncoder;
import com.openmoments.scytale.encryption.CertificateFactory;
import com.openmoments.scytale.encryption.CertificateType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class ExampleUsage {
    public static void main(String[] args) {
        String homePath = System.getProperty("user.home");

        try (FileOutputStream privateKey = new FileOutputStream(homePath + "/private_bit.key");
             FileOutputStream publicKey = new FileOutputStream(homePath + "/public_bit.pub")) {
            new CertificateFactory().get(CertificateType.RSA).toStream(privateKey, publicKey);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream privateKey = new FileOutputStream(homePath + "/private.key");
             FileOutputStream publicKey = new FileOutputStream(homePath + "/public.pem")) {
            KeyPair keyPair = new CertificateFactory().get(CertificateType.RSA).generateKeyPair();
            Map<CertificateEncoder.KeyType, String> keyMap = new CertificateEncoder().base64Encode(keyPair);

            privateKey.write(keyMap.get(CertificateEncoder.KeyType.PRIVATE).getBytes());
            publicKey.write(keyMap.get(CertificateEncoder.KeyType.PUBLIC).getBytes());

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
