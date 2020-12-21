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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Request implements APIRequest {
    private static final Logger LOG = Logger.getLogger(Request.class.getName());

    private static final String AUTHENTICATION_KEY_HEADER = "X-API-Key";
    private static final String API_AUTH_TYPE = "api.auth.type";
    private static final String API_AUTH_KEY = "api.auth.key";
    private static final String API_AUTH_CERT = "api.auth.cert";
    private static final String API_URI = "api.url";

    private HttpClient client;
    private final Properties properties;
    private HttpRequest.Builder apiRequestBuilder;

    public Request() throws IOException {
        properties = new PropertiesLoader().getProperties();
    }

    @Override
    public HttpResponse<String> get(String uri, APIRequestCallback callbacks) throws IOException, InterruptedException, CertificateException {
        createRequest(uri);
        apiRequestBuilder.GET();

        return client.send(apiRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> post(String uri, JSONObject json, APIRequestCallback callbacks) throws IOException, InterruptedException, CertificateException {
        createRequest(uri);
        apiRequestBuilder.POST(HttpRequest.BodyPublishers.ofString(json.toString()));

        return client.send(apiRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> put(String uri, JSONObject json, APIRequestCallback callbacks) throws IOException, InterruptedException, CertificateException {
        createRequest(uri);
        apiRequestBuilder.PUT(HttpRequest.BodyPublishers.ofString(json.toString()));

        return client.send(apiRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

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
            } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | UnrecoverableKeyException | KeyManagementException e) {
                LOG.log(Level.SEVERE, "Failed to configure mutual authentication for API Requests", e);
                throw new CertificateException("Certificate used for mutual authentication is invalid");
            }
        }
    }

    private SSLContext getSslContext() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        KeyStore clientKeyStore = KeyStore.getInstance("pkcs12");
        String p12Password = properties.getProperty(API_AUTH_KEY, "");
        InputStream clientP12 = getClass().getClassLoader().getResourceAsStream(properties.getProperty(API_AUTH_CERT));
        TrustManager[] managers = null;

        String trustManager = properties.getProperty("api.auth.truststore");
        if (trustManager != null && !trustManager.isEmpty()) {
            InputStream trustManagerStream = getClass().getClassLoader().getResourceAsStream(trustManager);
            KeyStore trustStore = KeyStore.getInstance("jks");
            String trustStorePassword = properties.getProperty("api.auth.truststore-password", "");
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

    private String getAPIURL(String uri) {
        return properties.getProperty(API_URI)
                .replaceAll("/+$", "") +
                "/" +
                uri.replaceAll("^/+", "");
    }
}
