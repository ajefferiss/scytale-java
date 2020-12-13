package com.openmoments.scytale.api;

import com.openmoments.scytale.exception.InvalidKeystoreException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;

public class KeyStoreRequest {

    protected static final String KEYSTORE_ID_ATTR = "id";
    protected static final String KEYSTORE_NAME_ATTR = "name";
    protected static final String KEYSTORE_URI = "keystores";

    private APIRequest apiRequest;

    public KeyStoreRequest(APIRequest apiRequest) {
        this.apiRequest = apiRequest;
    }

    public String getById(Long id) throws InvalidKeystoreException, IOException, InterruptedException {
        if (Optional.ofNullable(id).orElse(0L) == 0L) {
            throw new IllegalArgumentException("Keystore ID cannot be empty");
        }
        if (apiRequest == null) {
            throw  new IllegalArgumentException("API Request interface is required");
        }

        String getURL = KEYSTORE_URI + "/" + id;

        HttpResponse<String> getResponse = apiRequest.get(getURL,null);
        if (getResponse.statusCode() != 200) {
            throw new InvalidKeystoreException("API response failed with " + getResponse.body());
        }

        return getResponse.body();
    }

    public String post(String name) throws IOException, InterruptedException, InvalidKeystoreException {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Keystore name cannot be empty");
        }
        if (apiRequest == null) {
            throw  new IllegalArgumentException("API Request interface is required");
        }

        JSONObject createJson = new JSONObject().put(KEYSTORE_NAME_ATTR, name);
        HttpResponse<String> createResponse = apiRequest.post(KEYSTORE_URI, createJson, null);

        if (createResponse.statusCode() != 200) {
            throw new InvalidKeystoreException("API response failed with " + createResponse.body());
        }

        return createResponse.body();
    }

    public String searchByName(String name) throws IOException, InterruptedException, InvalidKeystoreException {
        String searchURL = KEYSTORE_URI + "/search?name=" + name;
        HttpResponse<String> searchResponse = apiRequest.get(searchURL, null);

        if (searchResponse.statusCode() != 200) {
            throw new InvalidKeystoreException("API Response failed with " + searchResponse.body());
        }

        return searchResponse.body();
    }
}
