package com.openmoments.scytale.samples;

import com.openmoments.scytale.api.KeyStoreRequest;
import com.openmoments.scytale.api.PublicKeyRequest;
import com.openmoments.scytale.api.Request;
import com.openmoments.scytale.encryption.CertificateEncoder;
import com.openmoments.scytale.encryption.CertificateFactory;
import com.openmoments.scytale.encryption.CertificateType;
import com.openmoments.scytale.encryption.RSACertificate;
import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.entities.PublicKey;
import com.openmoments.scytale.exception.InvalidKeystoreException;
import com.openmoments.scytale.exception.ScytaleException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExampleUsage {
    private static final Logger LOG = Logger.getLogger(ExampleUsage.class.getName());
    private final KeyStoreRequest keyStoreRequest = new KeyStoreRequest(new Request());

    public ExampleUsage() throws IOException {}

    public static void main(String[] args) throws IOException {
        ExampleUsage examples = new ExampleUsage();

        examples.saveToFileAsString();
        KeyStore keyStore = examples.createNewKeyStore();
        examples.getKeyStore(keyStore.getId());
        examples.searchKeyStore("test@gmail.com");
        examples.updateKeyStore("test@gmail.com", "updated-test@gmail.com");
        examples.getKeysFor(keyStore);
        examples.addKeyTo(keyStore);
        examples.getKeysFor(keyStore);
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
            KeyStore newKeystore = keyStoreRequest.createKeyStore(newID);
            LOG.log(Level.INFO, "Create a new keystore for {0} of {1}", new String[]{newID, String.valueOf(newKeystore)});
            return newKeystore;
        } catch (IOException | InterruptedException | ScytaleException | CertificateException e) {
            LOG.log(Level.SEVERE, "Failed to create new keystore", e);
        }
        return null;
    }

    void getKeyStore(Long id) {
        try {
            KeyStore retrievedKeyStore = keyStoreRequest.getById(id);
            LOG.log(Level.INFO, "Retrieved keystore {0}", String.valueOf(retrievedKeyStore));
        } catch (IOException | InterruptedException | ScytaleException | CertificateException e) {
            LOG.log(Level.SEVERE, "Failed to retrieve keystore", e);
        }
    }

    void searchKeyStore(String name) {
        try {
            KeyStore response = keyStoreRequest.searchByName(name);
            LOG.log(Level.INFO, "Retrieved {0}", response);
        } catch (InterruptedException | IOException | InvalidKeystoreException | ScytaleException | CertificateException e) {
            LOG.log(Level.SEVERE, "Failed to search for keystore", e);
        }
    }

    void updateKeyStore(String name, String updatedName) {
        try {
            KeyStore foundKeyStore = keyStoreRequest.searchByName(name);
            KeyStore updateKeyStore = new KeyStore(foundKeyStore.getId(), updatedName);
            KeyStore updated = keyStoreRequest.updateKeyStore(updateKeyStore);

            LOG.log(Level.INFO, "Updated from " + foundKeyStore + " to " + updated);
        } catch (InterruptedException | IOException | InvalidKeystoreException | ScytaleException | CertificateException e) {
            LOG.log(Level.SEVERE, "Failed to search for keystore", e);
        }
    }

    void getKeysFor(KeyStore keyStore) {
        try {
            List<PublicKey> foundKeys = new PublicKeyRequest(new Request()).getAll(keyStore);
            LOG.log(Level.INFO, "Found keys: {0}", foundKeys);
        } catch (InterruptedException | ScytaleException | IOException | CertificateException e) {
            LOG.log(Level.SEVERE, "Failed to get keys", e);
        }
    }

    void addKeyTo(KeyStore keyStore) {
        try {
            KeyPair keyPair = new CertificateFactory().get(CertificateType.RSA).generateKeyPair();
            Map<CertificateEncoder.KeyType, String> keyMap = new CertificateEncoder().base64Encode(keyPair);

            PublicKey publicKey = new PublicKeyRequest(new Request()).add(
                    keyMap.get(CertificateEncoder.KeyType.PUBLIC),
                    keyStore);
            LOG.log(Level.INFO, "Created public key {0}", publicKey);
        } catch (NoSuchAlgorithmException | IOException | InterruptedException | ScytaleException | CertificateException e) {
            LOG.log(Level.SEVERE, "Failed to add key", e);
        }
    }
}
