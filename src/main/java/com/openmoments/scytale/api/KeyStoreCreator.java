package com.openmoments.scytale.api;

import com.openmoments.scytale.exception.InvalidKeystoreException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;

public class KeyStoreCreator {

    protected static final String KEYSTORE_ID_ATTR = "id";
    protected static final String KEYSTORE_NAME_ATTR = "name";
    protected static final String KEYSTORE_URI = "keystores";

    private Integer id;
    private String name;
    private APIRequest apiRequest;

    public KeyStoreCreator() {}

    /***
     * Sets the implementation of the {@link APIRequest APIRequest} interface
     * @param apiRequest Implementation of the {@link APIRequest APIRequest}
     * @return {@link KeyStoreCreator KeyStoreCreator}
     */
    public KeyStoreCreator apiRequest(APIRequest apiRequest) {
        this.apiRequest = apiRequest;
        return this;
    }

    /***
     * Sets the id of a KeyStore
     * @param id ID of the KeyStore returned previously by the API
     * @return {@link KeyStoreCreator KeyStoreCreator}
     */
    public KeyStoreCreator id(Integer id) {
        this.id = id;
        return this;
    }

    /***
     * Sets the required name for the KeyStore
     * @param name Name of the keystore
     * @return {@link KeyStoreCreator KeyStoreCreator}
     */
    public KeyStoreCreator name(String name) {
        this.name = name;
        return this;
    }

    /***
     * Creates a new Keystore instance for the specified user.
     * @return {@link KeyStore KeyStore}
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws IllegalArgumentException - If the id passed is empty
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the Keystore was not created correctly
     */
    public KeyStore create() throws IllegalArgumentException, InvalidKeystoreException, IOException, InterruptedException {
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

        JSONObject createdKeystore = new JSONObject(createResponse.body());

        this.id = createdKeystore.getInt(KEYSTORE_ID_ATTR);
        this.name = createdKeystore.getString(KEYSTORE_NAME_ATTR);

        return new KeyStore(this.id, this.name);
    }

    /***
     * Return a specific keystore
     * @return {@link KeyStore KeySTore}
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws IllegalArgumentException - If the id passed is empty
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the Keystore was not created correctly
     */
    public KeyStore get() throws IllegalArgumentException, InvalidKeystoreException, IOException, InterruptedException {
        if (Optional.ofNullable(id).orElse(0) == 0) {
            throw new IllegalArgumentException("Keystore ID cannot be empty");
        }
        if (apiRequest == null) {
            throw  new IllegalArgumentException("API Request interface is required");
        }

        String getURL = KEYSTORE_URI + "/" + id;

        HttpResponse<String> createResponse = apiRequest.get(getURL,null);
        if (createResponse.statusCode() != 200) {
            throw new InvalidKeystoreException("API response failed with " + createResponse.body());
        }

        JSONObject retrievedKeyStore = new JSONObject(createResponse.body());

        this.id = retrievedKeyStore.getInt(KEYSTORE_ID_ATTR);
        this.name = retrievedKeyStore.getString(KEYSTORE_NAME_ATTR);

        return new KeyStore(this.id, this.name);
    }
}
