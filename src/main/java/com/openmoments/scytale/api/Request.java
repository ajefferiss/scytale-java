package com.openmoments.scytale.api;

import com.openmoments.scytale.config.PropertiesLoader;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * Implementation of the {@link APIRequest APIRequest} interface using native {@link HttpClient HttpClient}.
 */
public class Request implements APIRequest {
    private static final Logger LOG = Logger.getLogger(Request.class.getName());

    private static final String AUTHENTICATION_KEY_HEADER = "X-API-Key";
    private static final String API_AUTH_TYPE = "api.auth.type";
    private static final String API_AUTH_KEY = "api.auth.key";
    private static final String API_AUTH_CERT = "api.auth.cert";
    private static final String API_URI = "api.url";
    private static final String API_AUTH_TRUSTSTORE = "api.auth.truststore";
    private static final String API_AUTH_TRUSTSTORE_PASSWORD = "api.auth.truststore-password";

    private HttpClient client;
    private HttpRequest.Builder apiRequestBuilder;
    private final Properties properties;

    public Request() throws IOException {
        properties = new PropertiesLoader().getProperties();
    }

    public Request(String propertiesPath) throws IOException {
        properties = new PropertiesLoader().file(propertiesPath).getProperties();
    }

    /***
     * Perform a HTTP GET
     * @param uri {@link String String} URI to perform GET against
     * @return A String {@link HttpResponse HttpResponse}
     * @throws IOException - If an I/O error occurs when sending or receiving
     * @throws InterruptedException - If the operation is interrupted
     * @throws CertificateException - If certificate authentication used but is invalid
     */
    @Override
    public HttpResponse<String> get(String uri) throws IOException, InterruptedException, CertificateException {
        createRequest(uri);
        apiRequestBuilder.GET();

        return client.send(apiRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /***
     * Perform a async HTTP GET
     * @param uri {@link String String} URI to perform GET against
     * @return A String {@link HttpResponse HttpResponse}
     * @throws CertificateException - If certificate authentication used but is invalid
     */
    @Override
    public CompletableFuture<HttpResponse<String>> getAsync(String uri) throws CertificateException {
        createRequest(uri);
        apiRequestBuilder.GET();

        return client.sendAsync(apiRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /***
     * Perform a HTTP POST
     * @param uri {@link String String} URI to perform POST against
     * @return A String {@link HttpResponse HttpResponse}
     * @throws IOException - If an I/O error occurs when sending or receiving
     * @throws InterruptedException - If the operation is interrupted
     * @throws CertificateException - If certificate authentication used but is invalid
     */
    @Override
    public HttpResponse<String> post(String uri, JSONObject json) throws IOException, InterruptedException, CertificateException {
        createRequest(uri);
        apiRequestBuilder.POST(HttpRequest.BodyPublishers.ofString(json.toString()));

        return client.send(apiRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /***
     * Perform a async HTTP POST
     * @param uri {@link String String} URI to perform POST against
     * @return A String {@link HttpResponse HttpResponse}
     * @throws CertificateException - If certificate authentication used but is invalid
     */
    @Override
    public CompletableFuture<HttpResponse<String>> postAsync(String uri, JSONObject json) throws CertificateException {
        createRequest(uri);
        apiRequestBuilder.POST(HttpRequest.BodyPublishers.ofString(json.toString()));

        return client.sendAsync(apiRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /***
     * Perform a HTTP PUT
     * @param uri {@link String String} URI to perform PUT against
     * @return A String {@link HttpResponse HttpResponse}
     * @throws IOException - If an I/O error occurs when sending or receiving
     * @throws InterruptedException - If the operation is interrupted
     * @throws CertificateException - If certificate authentication used but is invalid
     */
    @Override
    public HttpResponse<String> put(String uri, JSONObject json) throws IOException, InterruptedException, CertificateException {
        createRequest(uri);
        apiRequestBuilder.PUT(HttpRequest.BodyPublishers.ofString(json.toString()));

        return client.send(apiRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /***
     * Perform a async HTTP PUT
     * @param uri {@link String String} URI to perform PUT against
     * @return A String {@link HttpResponse HttpResponse}
     * @throws CertificateException - If certificate authentication used but is invalid
     */
    @Override
    public CompletableFuture<HttpResponse<String>> putAsync(String uri, JSONObject json) throws CertificateException {
        createRequest(uri);
        apiRequestBuilder.PUT(HttpRequest.BodyPublishers.ofString(json.toString()));

        return client.sendAsync(apiRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /***
     * Construct the {@link HttpRequest HttpRequest} and {@link HttpClient HttpClient} instances required to
     * perform operations
     * @param uri {@link String String} endpoint URI to perform HTTP operation against
     * @throws CertificateException - If certificate authentication used but is invalid
     */
    private void createRequest(String uri) throws CertificateException {
        String authType = properties.getProperty(API_AUTH_TYPE);

        apiRequestBuilder = HttpRequest.newBuilder().uri(URI.create(getAPIURL(uri)));
        apiRequestBuilder.header("Content-Type", "application/json");

        if (authType == null) {
            LOG.log(Level.FINE, "Authentication not configured");
            return;
        }

        if (authType.equalsIgnoreCase("key")) {
            apiRequestBuilder.header(AUTHENTICATION_KEY_HEADER, properties.get(API_AUTH_KEY).toString());
            client = HttpClient.newBuilder().build();
        } else if (authType.equalsIgnoreCase("cert")) {
            try {
                SSLContext sslContext = getSslContext();
                SSLParameters sslParam = new SSLParameters();
                sslParam.setNeedClientAuth(true);

                client = HttpClient.newBuilder().sslContext(sslContext).sslParameters(sslParam).build();
            } catch (NoSuchAlgorithmException | KeyStoreException | IOException | UnrecoverableKeyException | KeyManagementException e) {
                LOG.log(Level.SEVERE, "Failed to configure mutual authentication for API Requests", e);
                throw new CertificateException("Certificate used for mutual authentication is invalid");
            }
        }
    }

    /***
     * Setup a SSL Context for use for mutual authentication
     * @return {@link SSLContext SSLContext} to use for mutual authentication
     * @throws CertificateException - If certificates in the keystore could not be loaded
     * @throws NoSuchAlgorithmException - If the algorithm used for the keystore cannot be found
     * @throws KeyStoreException -  If no Provider supports a PKCS12 or JKS
     * @throws IOException - If there is an I/O or format problem with the keystore data, if a password is required but not given, or if the given password was incorrect.
     * @throws UnrecoverableKeyException - If the key cannot be recovered (e.g. the given password is wrong).
     * @throws KeyManagementException - If the SSL context initialisation fails
     */
    private SSLContext getSslContext() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        KeyStore clientKeyStore = KeyStore.getInstance("pkcs12");
        String p12Password = properties.getProperty(API_AUTH_KEY, "");
        InputStream clientP12 = getClass().getClassLoader().getResourceAsStream(properties.getProperty(API_AUTH_CERT));
        TrustManager[] managers = null;

        String trustManager = properties.getProperty(API_AUTH_TRUSTSTORE);
        if (trustManager != null && !trustManager.isEmpty()) {
            InputStream trustManagerStream = getClass().getClassLoader().getResourceAsStream(trustManager);
            KeyStore trustStore = KeyStore.getInstance("jks");
            String trustStorePassword = properties.getProperty(API_AUTH_TRUSTSTORE_PASSWORD, "");
            trustStore.load(trustManagerStream, trustStorePassword.toCharArray());

            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(trustStore);
            managers = factory.getTrustManagers();
        }

        clientKeyStore.load(clientP12, p12Password.toCharArray());

        KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance("SunX509");
        keyMgrFactory.init(clientKeyStore, p12Password.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyMgrFactory.getKeyManagers(), managers, null);
        return sslContext;
    }

    /***
     * Construct a full URL for a given endpoint
     * @param uri {@link String String} containing endpoint to request
     * @return {@link String String} URL to request
     */
    private String getAPIURL(String uri) {
        return properties.getProperty(API_URI)
                .replaceAll("/+$", "") +
                "/" +
                uri.replaceAll("^/+", "");
    }
}
