package com.openmoments.scytale.api;

import com.openmoments.scytale.entities.KeyStore;
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

    /***
     * Constructor requiring an APIRequest
     * @param apiRequest - Implementation of APIRequest interface
     */
    public KeyStoreRequest(APIRequest apiRequest) {
        this.apiRequest = apiRequest;
    }

    /***
     * Find a keystore based on the ID
     * @param id - Id of the keystore to retrieve
     * @return {@link String String} body of response from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the API did not return a valid Keystore
     */
    public String getById(Long id) throws InvalidKeystoreException, IOException, InterruptedException {
        validateID(id);
        validAPIImplementation();

        String getURL = KEYSTORE_URI + "/" + id;

        HttpResponse<String> getResponse = apiRequest.get(getURL,null);
        if (getResponse.statusCode() != 200) {
            throw new InvalidKeystoreException("API response failed with " + getResponse.body());
        }

        return getResponse.body();
    }

    /***
     * Create KeyStore on the remote API
     * @param name - Name of the keystore to create
     * @return {@link String String} body of response from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the API did not return a valid Keystore
     */
    public String createKeyStore(String name) throws IOException, InterruptedException, InvalidKeystoreException {
        validateName(name);
        validAPIImplementation();

        JSONObject createJson = new JSONObject().put(KEYSTORE_NAME_ATTR, name);
        HttpResponse<String> createResponse = apiRequest.post(KEYSTORE_URI, createJson, null);

        if (createResponse.statusCode() != 200) {
            throw new InvalidKeystoreException("API response failed with " + createResponse.body());
        }

        return createResponse.body();
    }

    /***
     * Updates a given keystore
     * @param updated - New replacement keystore
     * @return {@link String String} body of response from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the API did not return a valid Keystore
     */
    public String updateKeyStore(KeyStore updated) throws InvalidKeystoreException, IOException, InterruptedException {
        validateID(updated.getId());
        validateName(updated.getName());
        validAPIImplementation();

        JSONObject updateJson = new JSONObject()
                .put(KEYSTORE_ID_ATTR, updated.getId())
                .put(KEYSTORE_NAME_ATTR, updated.getName());

        String updateURI = KEYSTORE_URI + "/" + updated.getId();
        HttpResponse<String> updateResponse = apiRequest.put(updateURI, updateJson, null);

        if (updateResponse.statusCode() != 200) {
            throw new InvalidKeystoreException("API response failed with " + updateResponse.body());
        }

        return updateResponse.body();
    }

    /***
     * Retrieve a keystore based upon the name
     * @param name - Name of keystore item
     * @return {@link KeyStore KeyStore}
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the API did not return a valid Keystore
     */
    public String searchByName(String name) throws IOException, InterruptedException, InvalidKeystoreException {
        String searchURL = KEYSTORE_URI + "/search?name=" + name;
        HttpResponse<String> searchResponse = apiRequest.get(searchURL, null);

        if (searchResponse.statusCode() != 200) {
            throw new InvalidKeystoreException("API Response failed with " + searchResponse.body());
        }

        return searchResponse.body();
    }

    private void validateID(Long id) {
        if (Optional.ofNullable(id).orElse(0L) <= 0L) {
            throw new IllegalArgumentException("Keystore ID must be positive integer");
        }
    }

    private void validateName(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Keystore name cannot be empty");
        }
    }

    private void validAPIImplementation() {
        if (apiRequest == null) {
            throw  new IllegalArgumentException("API Request interface is required");
        }
    }
}
