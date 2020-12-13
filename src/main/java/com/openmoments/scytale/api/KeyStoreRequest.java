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

    String getById(Long id, APIRequest apiRequest) throws InvalidKeystoreException, IOException, InterruptedException {
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

    String post(String name, APIRequest apiRequest) throws IOException, InterruptedException, InvalidKeystoreException {
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
}
