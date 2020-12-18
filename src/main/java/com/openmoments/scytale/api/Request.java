package com.openmoments.scytale.api;

import com.openmoments.scytale.config.PropertiesLoader;
import com.openmoments.scytale.encryption.RandomStringGenerator;
import org.json.JSONObject;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
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
            final byte[] publicData = getCertData();
            final byte[] privateData = getKeyData();

            try {
                SSLContext sslCtx = getSslContext(publicData, privateData);

                SSLParameters sslParam = new SSLParameters();
                sslParam.setNeedClientAuth(true);

                client = HttpClient.newBuilder().sslContext(sslCtx).sslParameters(sslParam).build();
            } catch (CertificateException | NoSuchAlgorithmException | InvalidKeySpecException | KeyStoreException | IOException | UnrecoverableKeyException | KeyManagementException e) {
                LOG.log(Level.SEVERE, "Failed to configure mutual authentication for API Requests", e);
                throw new CertificateException("Certificate used for mutual authentication is invalid");
            }
        }
    }

    private SSLContext getSslContext(byte[] publicData, byte[] privateData) throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        final Collection<? extends Certificate> chain = certificateFactory.generateCertificates(
            new ByteArrayInputStream(publicData)
        );
        final Key key = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateData));
        KeyStore clientKeyStore = KeyStore.getInstance("jks");
        final char[] pwdChars = new RandomStringGenerator().buildString().toCharArray();
        clientKeyStore.load(null, null);
        clientKeyStore.setKeyEntry("Scytale", key, pwdChars, chain.toArray(new Certificate[0]));

        KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance("SunX509");
        keyMgrFactory.init(clientKeyStore, pwdChars);

        SSLContext sslCtx = SSLContext.getInstance("TLSv1.2");
        sslCtx.init(keyMgrFactory.getKeyManagers(), null, null);
        return sslCtx;
    }

    private String getAPIURL(String uri) {
        return properties.getProperty(API_URI)
                .replaceAll("/+$", "") +
                "/" +
                uri.replaceAll("^/+", "");
    }

    private byte[] getCertData() {
        return decodeCertificateData(properties.getProperty(API_AUTH_CERT));
    }

    private byte[] getKeyData() {
        return decodeCertificateData(properties.getProperty(API_AUTH_KEY));
    }

    private byte[] decodeCertificateData(String data) {
        try {
            return Base64.getDecoder().decode(data);
        } catch (IllegalArgumentException illegalArgumentException) {
            LOG.log(Level.FINE, "Failed to decode data as base64", illegalArgumentException);
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(data)) {
                if (inputStream != null) {
                    return inputStream.readAllBytes();
                }
            } catch (IOException ioException) {
                LOG.log(Level.FINE, "Failed to decode data as resource path", ioException);
            }
        }

        throw new IllegalArgumentException("Authentication required but is not valid");
    }
}
