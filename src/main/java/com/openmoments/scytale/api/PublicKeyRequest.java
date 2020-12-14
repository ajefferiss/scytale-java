package com.openmoments.scytale.api;

import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.entities.PublicKey;
import com.openmoments.scytale.exception.InvalidKeysException;
import com.openmoments.scytale.exception.InvalidKeystoreException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

public class PublicKeyRequest {
    private static final String KEYS_URI_FORMAT = KeyStoreRequest.KEYSTORE_URI + "/%d/keys";
    private static final String ID_ATTR = "id";
    private static final String PUBLIC_KEY_ATTR = "publicKey";

    private APIRequest apiRequest;

    public PublicKeyRequest(APIRequest apiRequest) {
        this.apiRequest = apiRequest;
    }

    public String getAll(KeyStore keyStore) throws IOException, InterruptedException, InvalidKeysException {
        String getURL = String.format(KEYS_URI_FORMAT, keyStore.getId());

        HttpResponse<String> getResponse = apiRequest.get(getURL,null);
        if (getResponse.statusCode() != 200) {
            throw new InvalidKeysException("Failed to get keys " + getResponse.body());
        }

        return getResponse.body();
    }

    public String add(PublicKey publicKey, KeyStore keyStore) throws InvalidKeysException, IOException, InterruptedException {
        String addUrl = String.format(KEYS_URI_FORMAT, keyStore.getId());
        JSONObject addKeyJson = new JSONObject().put(PUBLIC_KEY_ATTR, publicKey.getPublicKey());

        HttpResponse<String> postResponse = apiRequest.post(addUrl, addKeyJson, null);
        if (postResponse.statusCode() != 200) {
            throw new InvalidKeysException("Could not add key " + postResponse.body());
        }

        return postResponse.body();
    }

    public String update(PublicKey updatedKey, KeyStore keyStore) throws IOException, InterruptedException, InvalidKeysException {
        String updateUrl = String.format(KEYS_URI_FORMAT, keyStore.getId());
        JSONObject updateJson = new JSONObject().put(ID_ATTR, updatedKey.getId()).put(PUBLIC_KEY_ATTR, updatedKey.getPublicKey());

        HttpResponse<String> updateResponse = apiRequest.put(updateUrl, updateJson, null);
        if (updateResponse.statusCode() != 200) {
            throw new InvalidKeysException("Could not update key " + updateResponse.body());
        }

        return updateResponse.body();
    }
}
