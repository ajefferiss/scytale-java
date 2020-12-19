package com.openmoments.scytale.api;

import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.cert.CertificateException;

public class ScytaleRequest {
    private APIRequest apiRequest;

    /***
     *
     * @param apiRequest
     */
    public ScytaleRequest(APIRequest apiRequest) {
        if (apiRequest == null) {
            throw new IllegalArgumentException("API Request interface is required");
        }
        this.apiRequest = apiRequest;
    }

    /***
     * Perform a HTTP GET
     * @param getURL {@link String String} URL to perform GET against
     * @return {@link String String} JSON object returned from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    protected String get(String getURL) throws IOException, InterruptedException, ScytaleException, CertificateException {
        HttpResponse<String> getResponse = apiRequest.get(getURL,null);
        if (getResponse.statusCode() != 200) {
            throw new ScytaleException("API response failed with " + getResponse.body());
        }

        return getResponse.body();
    }

    /***
     * Perform a HTTP POST
     * @param postURL {@link String String} URL to perform POST against
     * @param postBody {@link JSONObject JSONObject} to send
     * @return {@link String String} JSON object returned from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    protected String post(String postURL, JSONObject postBody) throws IOException, InterruptedException, ScytaleException, CertificateException {
        HttpResponse<String> postResponse = apiRequest.post(postURL, postBody, null);

        if (postResponse.statusCode() != 200) {
            throw new ScytaleException("API response failed with " + postResponse.body());
        }

        return postResponse.body();
    }

    /***
     * Perform a HTTP PUT
     * @param putURL {@link String String} URL to perform PUT against
     * @param putBody {@link JSONObject JSONObject} to send
     * @return {@link String String} JSON object returned from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    protected String put(String putURL, JSONObject putBody) throws IOException, InterruptedException, ScytaleException, CertificateException {
        HttpResponse<String> putResponse = apiRequest.put(putURL, putBody, null);

        if (putResponse.statusCode() != 200) {
            throw new ScytaleException("API response failed with " + putResponse.body());
        }

        return putResponse.body();
    }
}