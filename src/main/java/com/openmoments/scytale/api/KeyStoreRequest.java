package com.openmoments.scytale.api;

import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

public class KeyStoreRequest extends ScytaleRequest {

    protected static final String KEYSTORE_ID_ATTR = "id";
    protected static final String KEYSTORE_NAME_ATTR = "name";
    protected static final String KEYSTORE_URI = "keystores";

    /***
     * Constructor requiring an APIRequest
     * @param apiRequest - Implementation of APIRequest interface
     * @throws IllegalArgumentException - if the APIRequest is invalid
     */
    public KeyStoreRequest(APIRequest apiRequest) {
        super(apiRequest);
    }

    /***
     * Find a keystore based on the ID
     * @param id - Id of the keystore to retrieve
     * @return {@link String String} body of response from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    public String getById(Long id) throws InterruptedException, ScytaleException, IOException {
        validateID(id);

        return this.get(KEYSTORE_URI + "/" + id);
    }

    /***
     * Create KeyStore on the remote API
     * @param name - Name of the keystore to create
     * @return {@link String String} body of response from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    public String createKeyStore(String name) throws InterruptedException, ScytaleException, IOException {
        validateName(name);

        JSONObject createJson = new JSONObject().put(KEYSTORE_NAME_ATTR, name);
        return this.post(KEYSTORE_URI,createJson);
    }

    /***
     * Updates a given keystore
     * @param updated  - New replacement keystore
     * @return {@link String String} body of response from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    public String updateKeyStore(KeyStore updated) throws InterruptedException, ScytaleException, IOException {
        validateID(updated.getId());
        validateName(updated.getName());

        JSONObject updateJson = new JSONObject()
                .put(KEYSTORE_ID_ATTR, updated.getId())
                .put(KEYSTORE_NAME_ATTR, updated.getName());

        String updateURI = KEYSTORE_URI + "/" + updated.getId();

        return this.put(updateURI, updateJson);
    }

    /***
     * Retrieve a keystore based upon the name
     * @param name - Name of keystore item
     * @return {@link KeyStore KeyStore}
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    public String searchByName(String name) throws IOException, InterruptedException, ScytaleException {
        String searchURL = KEYSTORE_URI + "/search?name=" + name;
        return this.get(searchURL);
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
}
