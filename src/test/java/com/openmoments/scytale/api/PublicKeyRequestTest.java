package com.openmoments.scytale.api;

import com.openmoments.scytale.TestUtils;
import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.entities.ScytalePublicKey;
import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("PublicKey Request Tests")
class PublicKeyRequestTest {

    private static final APIRequest apiRequest = mock(APIRequest.class);
    private final KeyStore keyStore = new KeyStore(1L, "Test");

    @Nested
    @DisplayName("HTTP GET")
    class PublicKeyGets {
        @Test
        @DisplayName("Should throw Scytale Exception on error")
        void shouldThrowOnError() throws IOException, InterruptedException, CertificateException {
            when(apiRequest.get(eq("keystores/1/keys")))
                .thenReturn(TestUtils.setupHTTPResponse(200, "Not JSON"));

            Exception scytaleException = assertThrows(ScytaleException.class,
                () -> new PublicKeyRequest(apiRequest).getAll(keyStore));

            assertEquals("API Returned invalid JSON", scytaleException.getMessage());
        }

        @Test
        @DisplayName("Should return PublicKey list")
        void shouldReturnKeysAsList() throws IOException, InterruptedException, ScytaleException, CertificateException {
            String testPublicKey = readTestFile("test_rsa_public.pem");
            JSONObject resultObject = new JSONObject().put("id", 1).put("publicKey", testPublicKey);
            JSONArray resultArray = new JSONArray().put(resultObject);

            when(apiRequest.get(eq("keystores/1/keys"))).thenReturn(TestUtils.setupHTTPResponse(200, resultArray.toString()));

            List<ScytalePublicKey> getResponse = new PublicKeyRequest(apiRequest).getAll(keyStore);
            List<ScytalePublicKey> expectedList = new ArrayList<>();
            expectedList.add(new ScytalePublicKey(1L, testPublicKey));

            assertEquals(expectedList, getResponse);
        }

        @Test
        @DisplayName("Should return empty list when no keys")
        void shouldReturnEmptyList() throws IOException, InterruptedException, ScytaleException, CertificateException {
            when(apiRequest.get(eq("keystores/1/keys")))
                .thenReturn(TestUtils.setupHTTPResponse(200, "[]"));

            List<ScytalePublicKey> getResponse = new PublicKeyRequest(apiRequest).getAll(keyStore);
            List<ScytalePublicKey> expectedList = new ArrayList<>();

            assertEquals(expectedList, getResponse);
        }
    }

    @Nested
    @DisplayName("HTTP POST")
    class PublicKeyAdd {
        @Test
        @DisplayName("Should throw Scytale Exception on error")
        void shouldThrowOnError() throws IOException, InterruptedException, CertificateException {
            when(apiRequest.post(eq("keystores/1/keys"), any()))
                .thenReturn(TestUtils.setupHTTPResponse(200, "Not JSON"));

            Exception scytaleException = assertThrows(ScytaleException.class,
                () -> new PublicKeyRequest(apiRequest).add("Test", keyStore));

            assertEquals("API Returned invalid JSON", scytaleException.getMessage());
        }

        @Test
        @DisplayName("Should return PublicKey added")
        void shouldReturnPublicKeyAdded() throws IOException, InterruptedException, ScytaleException, CertificateException {
            String testPublicKey = readTestFile("test_rsa_public.pem");
            JSONObject resultObject = new JSONObject().put("id", 1).put("publicKey", testPublicKey);

            when(apiRequest.post(eq("keystores/1/keys"), any())).thenReturn(TestUtils.setupHTTPResponse(200, resultObject.toString()));

            ScytalePublicKey scytalePublicKeyAdded = new PublicKeyRequest(apiRequest).add(testPublicKey, keyStore);
            ScytalePublicKey added = new ScytalePublicKey(1L, testPublicKey);

            assertEquals(added, scytalePublicKeyAdded);
        }
    }

    @Nested
    @DisplayName("HTTP PUT")
    class PublicKeyUpdate {
        @Test
        @DisplayName("Should throw Scytale Exception on error")
        void shouldThrowOnError() throws IOException, InterruptedException, CertificateException {
            ScytalePublicKey newScytalePublicKey = new ScytalePublicKey(1L, readTestFile("test_rsa_public.pem"));

            when(apiRequest.put(eq("keystores/1/keys/1"), any()))
                    .thenReturn(TestUtils.setupHTTPResponse(200, "Not JSON"));

            Exception scytaleException = assertThrows(ScytaleException.class,
                    () -> new PublicKeyRequest(apiRequest).update(newScytalePublicKey, keyStore));

            assertEquals("API Returned invalid JSON", scytaleException.getMessage());
        }

        @Test
        @DisplayName("Should return PublicKey added")
        void shouldReturnPublicKeyUpdated() throws IOException, InterruptedException, ScytaleException, CertificateException {
            String testPublicKey = readTestFile("test_rsa_public.pem");
            JSONObject resultObject = new JSONObject().put("id", 1).put("publicKey", testPublicKey);
            ScytalePublicKey updatedScytalePublicKey = new ScytalePublicKey(1L, testPublicKey);

            when(apiRequest.put(eq("keystores/1/keys/1"), any())).thenReturn(TestUtils.setupHTTPResponse(200, resultObject.toString()));

            ScytalePublicKey scytalePublicKeyAdded = new PublicKeyRequest(apiRequest).update(updatedScytalePublicKey, keyStore);

            assertEquals(updatedScytalePublicKey, scytalePublicKeyAdded);
        }
    }

    String readTestFile(String resourcePath) {
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource(resourcePath).toURI());
            return Files.lines(path).collect(Collectors.joining("\n"));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}