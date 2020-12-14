package com.openmoments.scytale.api;

import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.exception.InvalidKeystoreException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Keystore Request Tests")
class KeyStoreRequestTest {

    private static final APIRequest apiRequest = mock(APIRequest.class);

    static Stream<Arguments> keyStoresWithExceptionText() {
        return Stream.of(
            arguments("Keystore ID must be positive integer", new KeyStore(0L, ""), apiRequest),
            arguments("Keystore ID must be positive integer", new KeyStore(-1L, ""), apiRequest),
            arguments("Keystore name cannot be empty", new KeyStore(1L, ""), apiRequest),
            arguments("API Request interface is required", new KeyStore(1L, "Test"), null)
        );
    }

    @ParameterizedTest
    @MethodSource("keyStoresWithExceptionText")
    @DisplayName("Validation")
    void shouldThrowWhenKeyStoreInvalid(String expectedMessage, KeyStore tempKeyStore, APIRequest request) {
        KeyStoreRequest keyStoreRequest = new KeyStoreRequest(request);

        Exception illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> keyStoreRequest.updateKeyStore(tempKeyStore));

        assertEquals(expectedMessage, illegalArgumentException.getMessage());
    }

    @Nested
    @DisplayName("Get By ID")
    class GetByID {
        static final String keystoreURI = KeyStoreRequest.KEYSTORE_URI + "/1";
        @Test
        @DisplayName("Non-success response throws")
        void shouldThrowWhenHTTPNot200() throws IOException, InterruptedException {
            when(apiRequest.get(eq(keystoreURI), any()))
                    .thenReturn(setupResponse(403, "Forbidden 403"));

            Exception invalidKeystoreException = assertThrows(InvalidKeystoreException.class,
                    () -> new KeyStoreRequest(apiRequest).getById(1L));
            String expectedMessage = "API response failed with Forbidden 403";
            assertEquals(expectedMessage, invalidKeystoreException.getMessage());
        }

        @Test
        @DisplayName("Should return request body")
        void shouldReturnGetByIdBody() throws IOException, InterruptedException, InvalidKeystoreException {
            when(apiRequest.get(eq(keystoreURI), any()))
                    .thenReturn(setupResponse(200, "{\"id\": 1}"));

            JSONObject expectedJson = new JSONObject().put("id", 1);
            JSONObject actualBody = new JSONObject(new KeyStoreRequest(apiRequest).getById(1L));

            assertEquals(expectedJson.toString(), actualBody.toString());
        }
    }

    @Nested
    @DisplayName("Create Keystore")
    class KeystoreCreation {
        @Test
        @DisplayName("Non-success response throws")
        void shouldThrowWhenHTTPNot200() throws IOException, InterruptedException {
            when(apiRequest.post(eq(KeyStoreRequest.KEYSTORE_URI), any(JSONObject.class), any()))
                    .thenReturn(setupResponse(403, "Forbidden 403"));

            Exception invalidKeyStoreException = assertThrows(InvalidKeystoreException.class,
                    () -> new KeyStoreRequest(apiRequest).createKeyStore("test"));
            String expectedMessage = "API response failed with Forbidden 403";
            assertEquals(expectedMessage, invalidKeyStoreException.getMessage());
        }

        @Test
        @DisplayName("Should return response body on success")
        void shouldReturnCreateBody() throws IOException, InterruptedException, InvalidKeystoreException {
            when(apiRequest.post(eq(KeyStoreRequest.KEYSTORE_URI), any(JSONObject.class), any()))
                    .thenReturn(setupResponse(200, "{\"id\": 1, \"name\": \"Test\"}"));

            JSONObject expectedJson = new JSONObject().put("id", 1).put("name", "Test");
            JSONObject actualBody = new JSONObject(new KeyStoreRequest(apiRequest).createKeyStore("Test"));

            assertEquals(expectedJson.toString(), actualBody.toString());
        }
    }

    @Nested
    @DisplayName("Update Keystore")
    class KeystoreUpdates {
        static final String updateURI = KeyStoreRequest.KEYSTORE_URI + "/1";

        @Test
        @DisplayName("Non-success response throws")
        void shouldThrowWhenHTTPNot200() throws IOException, InterruptedException {
            when(apiRequest.put(eq(updateURI), any(JSONObject.class), any()))
                    .thenReturn(setupResponse(403, "Forbidden 403"));

            KeyStore keyStore = new KeyStore(1L, "Updated");
            Exception invalidKeyStoreException = assertThrows(InvalidKeystoreException.class,
                    () -> new KeyStoreRequest(apiRequest).updateKeyStore(keyStore));
            String expectedMessage = "API response failed with Forbidden 403";
            assertEquals(expectedMessage, invalidKeyStoreException.getMessage());
        }

        @Test
        @DisplayName("Should return response body on success")
        void shouldReturnUpdatedBody() throws IOException, InterruptedException, InvalidKeystoreException {
            when(apiRequest.put(eq(updateURI), any(JSONObject.class), any()))
                    .thenReturn(setupResponse(200, "{\"id\": 1, \"name\": \"Updated\"}"));

            KeyStore keyStore = new KeyStore(1L, "Updated");
            JSONObject expectedJson = new JSONObject().put("id", 1).put("name", "Updated");
            JSONObject actualBody = new JSONObject(new KeyStoreRequest(apiRequest).updateKeyStore(keyStore));

            assertEquals(expectedJson.toString(), actualBody.toString());
        }
    }

    @Nested
    @DisplayName("Search KeyStore")
    class KeystoreSearch {
        static final String searchURI = KeyStoreRequest.KEYSTORE_URI + "/search?name=Test";

        @Test
        @DisplayName("Non-success response throws")
        void shouldThrowWhenHTTPNot200() throws IOException, InterruptedException {
            when(apiRequest.get(eq(searchURI), any()))
                    .thenReturn(setupResponse(403, "Forbidden 403"));

            Exception invalidKeyStoreException = assertThrows(InvalidKeystoreException.class,
                    () -> new KeyStoreRequest(apiRequest).searchByName("Test"));
            String expectedMessage = "API Response failed with Forbidden 403";
            assertEquals(expectedMessage, invalidKeyStoreException.getMessage());
        }

        @Test
        @DisplayName("Should return response body on success")
        void shouldReturnUpdatedBody() throws IOException, InterruptedException, InvalidKeystoreException {
            when(apiRequest.get(eq(searchURI), any()))
                    .thenReturn(setupResponse(200, "[{\"id\": 1, \"name\": \"Test Updated\"}]"));

            JSONObject expectedJson = new JSONObject().put("id", 1).put("name", "Test Updated");
            JSONArray actualBody = new JSONArray(new KeyStoreRequest(apiRequest).searchByName("Test"));
            assertEquals(expectedJson.toString(), actualBody.get(0).toString());
        }
    }

    HttpResponse<String> setupResponse(int responseCode, String responseBody) {
        return new HttpResponse<>() {
            @Override
            public int statusCode() {
                return responseCode;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return responseBody;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };
    }
}