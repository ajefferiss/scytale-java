package com.openmoments.scytale.api;

import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.exception.InvalidKeystoreException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class KeyStoreCreator {

    private static final Long DEFAULT_ID = -1L;
    private static final String DEFAULT_NAME = "";

    private Long id = DEFAULT_ID;
    private String name = DEFAULT_NAME;
    private KeyStoreRequest keyStoreRequest;

    /***
     * Set the {@link KeyStoreRequest KeyStoreRequest} to handle API queries
     * @param {@link keyStoreRequest KeyStoreRequest} API handler to use
     * @return {@link KeyStoreCreator KeyStoreCreator}
     */
    public KeyStoreCreator request(KeyStoreRequest keyStoreRequest) {
        this.keyStoreRequest = keyStoreRequest;
        return this;
    }

    /***
     * Sets the id of a KeyStore
     * @param id ID of the KeyStore returned previously by the API
     * @return {@link KeyStoreCreator KeyStoreCreator}
     */
    public KeyStoreCreator id(Long id) {
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
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the Keystore was not created correctly
     */
    public KeyStore create() throws InterruptedException, InvalidKeystoreException, IOException {
        return fromJSON(new JSONObject(keyStoreRequest.createKeyStore(this.name)));
    }

    /***
     * Return a specific keystore
     * @return {@link KeyStore KeyStore}
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the Keystore was not created correctly
     */
    public KeyStore byId() throws InterruptedException, InvalidKeystoreException, IOException {
        return fromJSON(new JSONObject(keyStoreRequest.getById(this.id)));
    }

    /***
     * Create a keystore instance by search for an exact name match
     * @return {@link KeyStore KeyStore}
     * @throws InterruptedException - If the API operation is interrupted
     * @throws InvalidKeystoreException - If the Keystore was not created correctly
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     */
    public KeyStore byName() throws InterruptedException, InvalidKeystoreException, IOException {
        JSONArray foundByName = new JSONArray(keyStoreRequest.searchByName(this.name));

        Optional<JSONObject> jsonObject = StreamSupport.stream(foundByName.spliterator(), false)
                                            .map(JSONObject.class::cast)
                                            .filter(o -> o.get(KeyStoreRequest.KEYSTORE_NAME_ATTR).equals(this.name))
                                            .findFirst();

        if (jsonObject.isEmpty()) {
            throw new InvalidKeystoreException("Keystore with name " + this.name + " does not exist");
        }

        return fromJSON(jsonObject.get());
    }

    /***
     * Create a keystore from a valid JSON object
     * @param json JSON to create KeyStore from
     * @return {@link KeyStore KeyStore}
     */
    public KeyStore fromJSON(JSONObject json) {
        this.id = json.getLong(KeyStoreRequest.KEYSTORE_ID_ATTR);
        this.name = json.getString(KeyStoreRequest.KEYSTORE_NAME_ATTR);
        return new KeyStore(this.id, this.name);
    }
}
