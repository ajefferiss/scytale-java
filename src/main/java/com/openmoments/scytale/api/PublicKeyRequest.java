package com.openmoments.scytale.api;

import com.openmoments.scytale.encryption.CertificateEncoder;
import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.entities.ScytalePublicKey;
import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PublicKeyRequest extends ScytaleRequest {
    private static final Logger LOG = Logger.getLogger(PublicKeyRequest.class.getName());
    private static final String KEYS_URI_FORMAT = KeyStoreRequest.KEYSTORE_URI + "/%d/keys";
    private static final String ID_ATTR = "id";
    private static final String PUBLIC_KEY_ATTR = "publicKey";

    /***
     * Constructor requiring an APIRequest
     * @param apiRequest - Implementation of APIRequest interface
     * @throws IllegalArgumentException - if the APIRequest is invalid
     */
    public PublicKeyRequest(APIRequest apiRequest) {
        this(apiRequest, null);
    }

    public PublicKeyRequest(APIRequest apiRequest, APIRequestCallback apiRequestCallback) {
        super(apiRequest, apiRequestCallback);
    }

    /***
     * Retrieve all keys associated with a keystore
     * @param keyStore {@link KeyStore KeyStore} to retriever keys for
     * @return {@link List List} of {@link ScytalePublicKey PublicKey} items
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid list of public keys
     * @throws CertificateException - Certificate authentication failed
     */
    public List<ScytalePublicKey> getAll(KeyStore keyStore) throws IOException, InterruptedException, ScytaleException, CertificateException {
        String getURL = String.format(KEYS_URI_FORMAT, keyStore.getId());

        try {
            JSONArray apiResult = new JSONArray(this.get(getURL));
            List<JSONObject> jsonObjects = StreamSupport.stream(apiResult.spliterator(), false)
                    .map(JSONObject.class::cast)
                    .collect(Collectors.toList());

            return jsonObjects.stream()
                    .map(o -> new ScytalePublicKey(o.getLong(ID_ATTR), o.getString(PUBLIC_KEY_ATTR)))
                    .collect(Collectors.toList());
        } catch (JSONException jsonException) {
            LOG.log(Level.SEVERE, RETURNED_INVALID_JSON, jsonException);
            throw new ScytaleException(RETURNED_INVALID_JSON);
        }
    }

    /***
     * Add a {@link ScytalePublicKey PublicKey} to a {@link KeyStore KeyStore}
     * @param publicKey {@link ScytalePublicKey PublicKey} to add
     * @param keyStore {@link KeyStore KeyStore} to add public to
     * @return {@link ScytalePublicKey PublicKey} returned public key from keystore
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     * @throws CertificateException - Certificate authentication failed
     */
    public ScytalePublicKey add(String publicKey, KeyStore keyStore) throws IOException, InterruptedException, ScytaleException, CertificateException {
        String addUrl = String.format(KEYS_URI_FORMAT, keyStore.getId());
        JSONObject addKeyJson = new JSONObject().put(PUBLIC_KEY_ATTR, publicKey);

        try {
            JSONObject createdJSON = new JSONObject(this.post(addUrl, addKeyJson));
            return new ScytalePublicKey(createdJSON.getLong(ID_ATTR), createdJSON.getString(PUBLIC_KEY_ATTR));
        } catch (JSONException jsonException) {
            LOG.log(Level.SEVERE, RETURNED_INVALID_JSON, jsonException);
            throw new ScytaleException(RETURNED_INVALID_JSON);
        }
    }

    /***
     * Update a {@link ScytalePublicKey PublicKey}
     * @param updatedKey {@link ScytalePublicKey PublicKey}
     * @param keyStore {@link KeyStore KeyStore} associated with {@link ScytalePublicKey PublicKey} to update
     * @return {@link ScytalePublicKey PublicKey} copy of the updated public key returned from the API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     * @throws CertificateException - Certificate authentication failed
     */
    public ScytalePublicKey update(ScytalePublicKey updatedKey, KeyStore keyStore) throws IOException, InterruptedException, ScytaleException, CertificateException {
        String updateUrl = String.format(KEYS_URI_FORMAT, keyStore.getId()) + "/" + updatedKey.getId();
        String publicKey = new CertificateEncoder().base64EncodePublicKey(updatedKey.getPublicKey());
        JSONObject updateJson = new JSONObject().put(ID_ATTR, updatedKey.getId()).put(PUBLIC_KEY_ATTR, publicKey);

        try {
            JSONObject updatedJSON = new JSONObject(this.put(updateUrl, updateJson));
            return new ScytalePublicKey(updatedJSON.getLong(ID_ATTR), updatedJSON.getString(PUBLIC_KEY_ATTR));
        } catch (JSONException jsonException) {
            LOG.log(Level.SEVERE, RETURNED_INVALID_JSON, jsonException);
            throw new ScytaleException(RETURNED_INVALID_JSON);
        }
    }
}
