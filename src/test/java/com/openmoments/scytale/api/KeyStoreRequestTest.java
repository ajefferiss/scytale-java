package com.openmoments.scytale.api;

import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
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
            arguments("Keystore name cannot be empty", new KeyStore(1L, ""), apiRequest)
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

    @Test
    @DisplayName("Get By Id should return ID in JSON wheShould return request body")
    void shouldReturnGetByIdBody() throws IOException, InterruptedException, ScytaleException {
        when(apiRequest.get(eq(KeyStoreRequest.KEYSTORE_URI + "/1"), any()))
                .thenReturn(setupResponse(200, "{\"id\": 1}"));

        JSONObject expectedJson = new JSONObject().put("id", 1);
        JSONObject actualBody = new JSONObject(new KeyStoreRequest(apiRequest).getById(1L));

        assertEquals(expectedJson.toString(), actualBody.toString());
    }

    @Test
    @DisplayName("Create should return keystore in response body on success")
    void shouldReturnCreatedBody() throws IOException, InterruptedException, ScytaleException {
        when(apiRequest.post(eq(KeyStoreRequest.KEYSTORE_URI), any(JSONObject.class), any()))
                .thenReturn(setupResponse(200, "{\"id\": 1, \"name\": \"Test\"}"));

        JSONObject expectedJson = new JSONObject().put("id", 1).put("name", "Test");
        JSONObject actualBody = new JSONObject(new KeyStoreRequest(apiRequest).createKeyStore("Test"));

        assertEquals(expectedJson.toString(), actualBody.toString());
    }

    @Test
    @DisplayName("Update should return updated keystore in response body on success")
    void shouldReturnUpdatedBody() throws IOException, InterruptedException, ScytaleException {
        when(apiRequest.put(eq(KeyStoreRequest.KEYSTORE_URI + "/1"), any(JSONObject.class), any()))
                .thenReturn(setupResponse(200, "{\"id\": 1, \"name\": \"Updated\"}"));

        KeyStore keyStore = new KeyStore(1L, "Updated");
        JSONObject expectedJson = new JSONObject().put("id", 1).put("name", "Updated");
        JSONObject actualBody = new JSONObject(new KeyStoreRequest(apiRequest).updateKeyStore(keyStore));

        assertEquals(expectedJson.toString(), actualBody.toString());
    }

    @Test
    @DisplayName("Should return response body on success")
    void shouldReturnListInSearchBody() throws IOException, InterruptedException, ScytaleException {
        when(apiRequest.get(eq(KeyStoreRequest.KEYSTORE_URI + "/search?name=Test"), any()))
                .thenReturn(setupResponse(200, "[{\"id\": 1, \"name\": \"Test Updated\"}]"));

        JSONObject expectedJson = new JSONObject().put("id", 1).put("name", "Test Updated");
        JSONArray actualBody = new JSONArray(new KeyStoreRequest(apiRequest).searchByName("Test"));
        assertEquals(expectedJson.toString(), actualBody.get(0).toString());
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