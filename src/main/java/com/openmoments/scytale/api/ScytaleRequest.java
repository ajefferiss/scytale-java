package com.openmoments.scytale.api;

import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.cert.CertificateException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScytaleRequest {
    private static final Logger LOG = Logger.getLogger(ScytaleRequest.class.getName());
    private final APIRequest apiRequest;
    private final APIRequestCallback apiRequestCallback;
    protected static final String FAILED_WITH = "API response failed with ";
    protected static final String RETURNED_INVALID_JSON = "API Returned invalid JSON";

    /***
     *
     * @param apiRequest
     */
    public ScytaleRequest(APIRequest apiRequest) {
        this(apiRequest, null);
    }

    public ScytaleRequest(APIRequest apiRequest, APIRequestCallback apiRequestCallback) {
        if (apiRequest == null) {
            throw new IllegalArgumentException("API Request interface is required");
        }
        this.apiRequest = apiRequest;
        this.apiRequestCallback = apiRequestCallback;
    }

    /***
     * Perform a HTTP GET. When run with a class callback method will return an empty string.
     * @param getURL {@link String String} URL to perform GET against
     * @return {@link String String} JSON object returned from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    protected String get(String getURL) throws IOException, InterruptedException, ScytaleException, CertificateException {

        if (apiRequestCallback == null) {
            HttpResponse<String> getResponse = apiRequest.get(getURL);
            if (getResponse.statusCode() != 200) {
                throw new ScytaleException(FAILED_WITH + getResponse.body());
            }

            return getResponse.body();
        }

        CompletableFuture<HttpResponse<String>> responseFuture = apiRequest.getAsync(getURL);
        responseFuture.whenComplete((response, error) -> {
           if (response != null) {
               if (response.statusCode() != 200) {
                   apiRequestCallback.onError(response);
               } else {
                   apiRequestCallback.onSuccess(response);
               }
           }
           if (error != null) {
               LOG.log(Level.SEVERE, "GET Request failed", error);
           }
        });

        return "";
    }

    /***
     * Perform a HTTP POST. When run with a class callback method will return an empty string.
     * @param postURL {@link String String} URL to perform POST against
     * @param postBody {@link JSONObject JSONObject} to send
     * @return {@link String String} JSON object returned from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    protected String post(String postURL, JSONObject postBody) throws IOException, InterruptedException, ScytaleException, CertificateException {

        if (apiRequestCallback == null) {
            HttpResponse<String> postResponse = apiRequest.post(postURL, postBody);
            if (postResponse.statusCode() != 200) {
                throw new ScytaleException(FAILED_WITH + postResponse.body());
            }

            return postResponse.body();
        }

        CompletableFuture<HttpResponse<String>> responseFuture = apiRequest.postAsync(postURL, postBody);
        responseFuture.whenComplete((response, error) -> {
            if (response != null) {
                if (response.statusCode() != 200) {
                    apiRequestCallback.onError(response);
                } else {
                    apiRequestCallback.onSuccess(response);
                }
            }
            if (error != null) {
                LOG.log(Level.SEVERE, "POST Request failed", error);
            }
        });

        return "";
    }

    /***
     * Perform a HTTP PUT. When run with a class callback method will return an empty string.
     * @param putURL {@link String String} URL to perform PUT against
     * @param putBody {@link JSONObject JSONObject} to send
     * @return {@link String String} JSON object returned from API
     * @throws IOException - If an I/O error occurs when sending or receiving API requests
     * @throws InterruptedException - If the API operation is interrupted
     * @throws ScytaleException - If the API did not return a valid Keystore
     */
    protected String put(String putURL, JSONObject putBody) throws IOException, InterruptedException, ScytaleException, CertificateException {

        if (apiRequestCallback == null) {
            HttpResponse<String> putResponse = apiRequest.put(putURL, putBody);
            if (putResponse.statusCode() != 200) {
                throw new ScytaleException(FAILED_WITH + putResponse.body());
            }

            return putResponse.body();
        }

        CompletableFuture<HttpResponse<String>> responseFuture = apiRequest.putAsync(putURL, putBody);
        responseFuture.whenComplete((response, error) -> {
            if (response != null) {
                if (response.statusCode() != 200) {
                    apiRequestCallback.onError(response);
                } else {
                    apiRequestCallback.onSuccess(response);
                }
            }
            if (error != null) {
                LOG.log(Level.SEVERE, "POST Request failed", error);
            }
        });

        return "";
    }
}