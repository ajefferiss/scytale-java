package com.openmoments.scytale.api;

import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.exception.InvalidKeystoreException;
import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

public class KeyStoreRequest extends ScytaleRequest {
    private static final Logger LOG = Logger.getLogger(KeyStoreRequest.class.getName());
    protected static final String KEYSTORE_ID_ATTR = "id";
    protected static final String KEYSTORE_NAME_ATTR = "name";
    protected static final String KEYSTORE_URI = "keystores";

    /***
     * Constructor requiring an APIRequest
     * @param apiRequest - Implementation of APIRequest interface
     * @throws IllegalArgumentException - if the APIRequest is invalid
     */
    public KeyStoreRequest(APIRequest apiRequest) {
        this(apiRequest, null);
    }

    public KeyStoreRequest(APIRequest apiRequest, APIRequestCallback callback) {
        super(apiRequest, callback);
    }

    /***
     * Find a keystore based on the ID
     * @param id - Id of the keystore to retrieve
     * @return {@link KeyStore KeyStore} returned by the API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     * @throws CertificateException - Certificate authentication failed
     */
    public Optional<KeyStore> getById(Long id) throws InterruptedException, ScytaleException, IOException, CertificateException {
        validateID(id);

        String bodyResponse = this.get(KEYSTORE_URI + "/" + id);

        if (bodyResponse.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(fromJson(bodyResponse));
    }

    /***
     * Create KeyStore on the remote API
     * @param name - Name of the keystore to create
     * @return {@link KeyStore KeyStore} created over the API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     * @throws CertificateException - Certificate authentication failed
     */
    public Optional<KeyStore> createKeyStore(String name) throws InterruptedException, ScytaleException, IOException, CertificateException {
        validateName(name);

        JSONObject createJson = new JSONObject().put(KEYSTORE_NAME_ATTR, name);

        String postBody = this.post(KEYSTORE_URI, createJson);
        if (postBody.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(fromJson(postBody));
    }

    /***
     * Updates a given keystore
     * @param updated  - New replacement keystore
     * @return {@link String String} body of response from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     * @throws CertificateException - Certificate authentication failed
     */
    public Optional<KeyStore> updateKeyStore(KeyStore updated) throws InterruptedException, ScytaleException, IOException, CertificateException {
        validateID(updated.getId());
        validateName(updated.getName());

        JSONObject updateJson = new JSONObject()
                .put(KEYSTORE_ID_ATTR, updated.getId())
                .put(KEYSTORE_NAME_ATTR, updated.getName());

        String updateURI = KEYSTORE_URI + "/" + updated.getId();

        String updateBody = this.put(updateURI, updateJson);
        if (updateBody.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(fromJson(updateBody));
    }

    /***
     * Retrieve a keystore based upon the name
     * @param name - Name of keystore item
     * @return {@link KeyStore KeyStore}
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     * @throws InvalidKeystoreException - Could not find the the specified keystore by name
     * @throws CertificateException - Certificate authentication failed
     */
    public Optional<KeyStore> searchByName(String name) throws IOException, InterruptedException, ScytaleException, InvalidKeystoreException, CertificateException {
        String searchURL = KEYSTORE_URI + "/search?name=" + name;

        try {
            String searchBody = this.get(searchURL);
            if (searchBody.isEmpty()) {
                return Optional.empty();
            }

            JSONArray foundByName = new JSONArray(searchBody);
            Optional<JSONObject> jsonObject = StreamSupport.stream(foundByName.spliterator(), false)
                    .map(JSONObject.class::cast)
                    .filter(o -> o.get(KeyStoreRequest.KEYSTORE_NAME_ATTR).equals(name))
                    .findFirst();

            if (jsonObject.isEmpty()) {
                throw new InvalidKeystoreException("Keystore with name " + name + " does not exist");
            }

            return Optional.of(new KeyStore(jsonObject.get().getLong(KEYSTORE_ID_ATTR),
                                            jsonObject.get().getString(KEYSTORE_NAME_ATTR)));
        } catch (JSONException jsonException) {
            LOG.log(Level.SEVERE, RETURNED_INVALID_JSON, jsonException);
            throw new ScytaleException(RETURNED_INVALID_JSON);
        }
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

    private KeyStore fromJson(String jsonString) throws ScytaleException {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return new KeyStore(jsonObject.getLong(KEYSTORE_ID_ATTR), jsonObject.getString(KEYSTORE_NAME_ATTR));
        } catch (JSONException jsonException) {
            LOG.log(Level.SEVERE, RETURNED_INVALID_JSON, jsonException);
            throw new ScytaleException(RETURNED_INVALID_JSON);
        }
    }
}
