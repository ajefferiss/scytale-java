package com.openmoments.scytale.api;

import com.openmoments.scytale.exception.InvalidKeystoreException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

public final class KeyStore {

    private static final String KEYSTORE_ID_ATTR = "id";
    private static final String KEYSTORE_NAME_ATTR = "name";
    private static final String KEYSTORE_URI = "keystores";

    private APIRequest apiRequest;
    private Integer id;
    private String name;

    public KeyStore(APIRequest apiRequest) throws IOException {
        this(apiRequest, 0, "");
    }

    public KeyStore(APIRequest apiRequest, Integer id, String name) throws IOException {
        this.id = id;
        this.name = name;
        this.apiRequest = apiRequest;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /***
     * Creates a new Keystore instance for the specified user.
     * @param id Unique identifier within the application for a user
     * @return {@link KeyStore KeyStore}
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws IllegalArgumentException - If the id passed is empty
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the Keystore was not created correctly
     */
    public KeyStore create(String id) throws IllegalArgumentException, InvalidKeystoreException, IOException, InterruptedException {
        if (id.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        JSONObject createJson = new JSONObject().put(KEYSTORE_NAME_ATTR, id);
        HttpResponse<String> createResponse = apiRequest.post(KEYSTORE_URI, createJson, null);

        if (createResponse.statusCode() != 200) {
            throw new InvalidKeystoreException("API response failed with " + createResponse.body());
        }

        JSONObject createdKeystore = new JSONObject(createResponse.body());

        this.id = createdKeystore.getInt(KEYSTORE_ID_ATTR);
        this.name = createdKeystore.getString(KEYSTORE_NAME_ATTR);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyStore keyStore = (KeyStore) o;
        return apiRequest.equals(keyStore.apiRequest) && id.equals(keyStore.id) && name.equals(keyStore.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiRequest, id, name);
    }

    @Override
    public String toString() {
        return "KeyStore{id='" + id + "', name='" + name + "'}";
    }
}
