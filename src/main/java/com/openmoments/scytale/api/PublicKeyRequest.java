package com.openmoments.scytale.api;

import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.entities.PublicKey;
import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PublicKeyRequest extends ScytaleRequest {
    private static final String KEYS_URI_FORMAT = KeyStoreRequest.KEYSTORE_URI + "/%d/keys";
    private static final String ID_ATTR = "id";
    private static final String PUBLIC_KEY_ATTR = "publicKey";

    /***
     * Constructor requiring an APIRequest
     * @param apiRequest - Implementation of APIRequest interface
     * @throws IllegalArgumentException - if the APIRequest is invalid
     */
    public PublicKeyRequest(APIRequest apiRequest) {
        super(apiRequest);
    }

    /***
     * Retrieve all keys associated with a keystore
     * @param keyStore {@link KeyStore KeyStore} to retriever keys for
     * @return {@link String String} containing JSON result from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    public List<PublicKey> getAll(KeyStore keyStore) throws IOException, InterruptedException, ScytaleException {
        String getURL = String.format(KEYS_URI_FORMAT, keyStore.getId());

        JSONArray apiResult = new JSONArray(this.get(getURL));
        List<JSONObject> jsonObjects = StreamSupport.stream(apiResult.spliterator(), false)
                .map(JSONObject.class::cast)
                .collect(Collectors.toList());

        if (jsonObjects.isEmpty()) {
            return new ArrayList<>();
        }

        return jsonObjects.stream()
                .map(o -> new PublicKey(o.getLong(ID_ATTR), o.getString(PUBLIC_KEY_ATTR)))
                .collect(Collectors.toList());
    }

    /***
     * Add a {@link PublicKey PublicKey} to a {@link KeyStore KeyStore}
     * @param publicKey {@link PublicKey PublicKey} to add
     * @param keyStore {@link KeyStore KeyStore} to add public to
     * @return {@link String String} containing JSON result from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    public String add(PublicKey publicKey, KeyStore keyStore) throws IOException, InterruptedException, ScytaleException {
        String addUrl = String.format(KEYS_URI_FORMAT, keyStore.getId());
        JSONObject addKeyJson = new JSONObject().put(PUBLIC_KEY_ATTR, publicKey.getPublicKey());

        return this.post(addUrl, addKeyJson);
    }

    /***
     * Update a {@link PublicKey PublicKey}
     * @param updatedKey {@link PublicKey PublicKey}
     * @param keyStore {@link KeyStore KeyStore} associated with {@link PublicKey PublicKey} to update
     * @return {@link String String} containing JSON result from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    public String update(PublicKey updatedKey, KeyStore keyStore) throws IOException, InterruptedException, ScytaleException {
        String updateUrl = String.format(KEYS_URI_FORMAT, keyStore.getId());
        JSONObject updateJson = new JSONObject().put(ID_ATTR, updatedKey.getId()).put(PUBLIC_KEY_ATTR, updatedKey.getPublicKey());

        return this.put(updateUrl, updateJson);
    }
}
