package com.openmoments.scytale.demo;

import com.openmoments.scytale.api.APIRequest;
import com.openmoments.scytale.api.APIRequestCallback;
import com.openmoments.scytale.api.KeyStore;
import com.openmoments.scytale.api.Request;
import com.openmoments.scytale.encryption.CertificateEncoder;
import com.openmoments.scytale.encryption.CertificateFactory;
import com.openmoments.scytale.encryption.CertificateType;
import com.openmoments.scytale.exception.InvalidKeystoreException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExampleUsage implements APIRequestCallback {
    private static final Logger LOG = Logger.getLogger(ExampleUsage.class.getName());

    public static void main(String[] args) {
        ExampleUsage examples = new ExampleUsage();

        examples.saveToFileAsString();
        examples.createNewKeyStore();
    }

    void saveToFileAsString() {
        String homePath = System.getProperty("user.home");

        try (FileOutputStream privateKey = new FileOutputStream(homePath + "/private.key");
             FileOutputStream publicKey = new FileOutputStream(homePath + "/public.pem")) {
            KeyPair keyPair = new CertificateFactory().get(CertificateType.RSA).generateKeyPair();
            Map<CertificateEncoder.KeyType, String> keyMap = new CertificateEncoder().base64Encode(keyPair);

            privateKey.write(keyMap.get(CertificateEncoder.KeyType.PRIVATE).getBytes());
            publicKey.write(keyMap.get(CertificateEncoder.KeyType.PUBLIC).getBytes());

            LOG.log(Level.INFO, "Created private.key and public.pem under: {0}", homePath);
        } catch (IOException | NoSuchAlgorithmException e) {
            LOG.log(Level.SEVERE, "Failed to save keys", e);
        }
    }

    void createNewKeyStore() {
        try {
            String newID = "test@gmail.com";
            APIRequest apiRequest = new Request();

            KeyStore newKeystore = new KeyStore(apiRequest).create(newID);
            LOG.log(Level.INFO, "Create a new keystore for {0} of {1}", new String[]{newID, String.valueOf(newKeystore)});
        } catch (IOException | InterruptedException | InvalidKeystoreException e) {
            LOG.log(Level.SEVERE, "Failed to create new keystore", e);
        }
    }

    @Override
    public void onSuccess(HttpResponse<String> response) {

    }

    @Override
    public void onError(HttpResponse<String> error) {

    }
}
