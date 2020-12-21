package com.openmoments.scytale.api;

import com.openmoments.scytale.TestUtils;
import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.entities.PublicKey;
import com.openmoments.scytale.exception.ScytaleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

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
            when(apiRequest.get(eq("keystores/1/keys")))
                .thenReturn(TestUtils.setupHTTPResponse(200, "[{\"id\": 1, \"publicKey\": \"Test Updated\"}]"));

            List<PublicKey> getResponse = new PublicKeyRequest(apiRequest).getAll(keyStore);
            List<PublicKey> expectedList = new ArrayList<>();
            expectedList.add(new PublicKey(1L, "Test Updated"));

            assertEquals(expectedList, getResponse);
        }

        @Test
        @DisplayName("Should return empty list when no keys")
        void shouldReturnEmptyList() throws IOException, InterruptedException, ScytaleException, CertificateException {
            when(apiRequest.get(eq("keystores/1/keys")))
                .thenReturn(TestUtils.setupHTTPResponse(200, "[]"));

            List<PublicKey> getResponse = new PublicKeyRequest(apiRequest).getAll(keyStore);
            List<PublicKey> expectedList = new ArrayList<>();

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
            when(apiRequest.post(eq("keystores/1/keys"), any()))
                .thenReturn(TestUtils.setupHTTPResponse(200, "{\"id\": 1, \"publicKey\": \"Test Public Key\"}"));

            PublicKey publicKeyAdded = new PublicKeyRequest(apiRequest).add("Test Public Key", keyStore);
            PublicKey added = new PublicKey(1L, "Test Public Key");

            assertEquals(added, publicKeyAdded);
        }
    }

    @Nested
    @DisplayName("HTTP PUT")
    class PublicKeyUpdate {
        @Test
        @DisplayName("Should throw Scytale Exception on error")
        void shouldThrowOnError() throws IOException, InterruptedException, CertificateException {
            PublicKey newPublicKey = new PublicKey(1L, "Test");
            when(apiRequest.put(eq("keystores/1/keys/1"), any()))
                    .thenReturn(TestUtils.setupHTTPResponse(200, "Not JSON"));

            Exception scytaleException = assertThrows(ScytaleException.class,
                    () -> new PublicKeyRequest(apiRequest).update(newPublicKey, keyStore));

            assertEquals("API Returned invalid JSON", scytaleException.getMessage());
        }

        @Test
        @DisplayName("Should return PublicKey added")
        void shouldReturnPublicKeyUpdated() throws IOException, InterruptedException, ScytaleException, CertificateException {
            PublicKey updatedPublicKey = new PublicKey(1L, "Updated Test Public Key");
            when(apiRequest.put(eq("keystores/1/keys/1"), any()))
                    .thenReturn(TestUtils.setupHTTPResponse(200, "{\"id\": 1, \"publicKey\": \"Updated Test Public Key\"}"));

            PublicKey publicKeyAdded = new PublicKeyRequest(apiRequest).update(updatedPublicKey, keyStore);

            assertEquals(updatedPublicKey, publicKeyAdded);
        }
    }
}