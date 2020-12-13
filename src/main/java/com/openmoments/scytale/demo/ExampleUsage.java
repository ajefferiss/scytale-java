package com.openmoments.scytale.demo;

import com.openmoments.scytale.api.*;
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

        /*examples.saveToFileAsString();
        KeyStore keyStore = examples.createNewKeyStore();
        examples.getKeyStore(keyStore.getId());*/
        examples.searchKeyStore("test@gmail.com");
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

    KeyStore createNewKeyStore() {
        try {
            String newID = "test@gmail.com";
            APIRequest apiRequest = new Request();

            KeyStore newKeystore = new KeyStoreCreator().apiRequest(apiRequest).name(newID).create();
            LOG.log(Level.INFO, "Create a new keystore for {0} of {1}", new String[]{newID, String.valueOf(newKeystore)});
            return newKeystore;
        } catch (IOException | InterruptedException | InvalidKeystoreException e) {
            LOG.log(Level.SEVERE, "Failed to create new keystore", e);
        }
        return null;
    }

    void getKeyStore(Long id) {
        try {
            KeyStore retrievedKeySTore = new KeyStoreCreator().apiRequest(new Request()).id(id).byId();
            LOG.log(Level.INFO, "Retrieved keystore {0}", String.valueOf(retrievedKeySTore));
        } catch (IOException | InterruptedException | InvalidKeystoreException e) {
            LOG.log(Level.SEVERE, "Failed to retrieve keystore", e);
        }
    }

    void searchKeyStore(String name) {
        try {
            String response = new KeyStoreRequest(new Request()).searchByName(name);
            LOG.log(Level.INFO, "Retrieved {0}", response);
        } catch (InterruptedException | IOException | InvalidKeystoreException e) {
            LOG.log(Level.SEVERE, "Failed to search for keystore", e);
        }
    }

    @Override
    public void onSuccess(HttpResponse<String> response) {

    }

    @Override
    public void onError(HttpResponse<String> error) {

    }
}
