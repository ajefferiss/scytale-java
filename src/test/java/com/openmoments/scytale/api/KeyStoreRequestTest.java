package com.openmoments.scytale.api;

import com.openmoments.scytale.TestUtils;
import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.exception.InvalidKeystoreException;
import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Keystore Request Tests")
class KeyStoreRequestTest {

    private static final APIRequest apiRequest = mock(APIRequest.class);
    private static final String KEYSTORE_JSON = "{\"id\": 1, \"name\": \"Test\"}";

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
    @DisplayName("Returns a Keystore when found by ID")
    void shouldReturnKeystoreById() throws IOException, InterruptedException, ScytaleException, CertificateException {
        when(apiRequest.get(eq(KeyStoreRequest.KEYSTORE_URI + "/1")))
                .thenReturn(TestUtils.setupHTTPResponse(200, KEYSTORE_JSON));

        KeyStore keyStore = new KeyStoreRequest(apiRequest).getById(1L).get();
        assertNotNull(keyStore);
        assertEquals(1L, keyStore.getId());
        assertEquals("Test", keyStore.getName());
    }

    @Test
    @DisplayName("Throws when invalid JSON returned")
    void shouldThrowWhenInvalidJsonReturned() throws IOException, InterruptedException, CertificateException {
        when(apiRequest.get(eq(KeyStoreRequest.KEYSTORE_URI + "/1")))
                .thenReturn(TestUtils.setupHTTPResponse(200,  "{\"id\": 1, \"ks name\": \"Test\"}"));

        Exception scytaleException = assertThrows(ScytaleException.class,
                () -> new KeyStoreRequest(apiRequest).getById(1L));

        assertEquals("API Returned invalid JSON", scytaleException.getMessage());
    }

    @Test
    @DisplayName("Creation returns a keystore")
    void shouldReturnKeystoreOnCreate() throws IOException, InterruptedException, ScytaleException, CertificateException {
        when(apiRequest.post(eq(KeyStoreRequest.KEYSTORE_URI), any(JSONObject.class)))
                .thenReturn(TestUtils.setupHTTPResponse(200, KEYSTORE_JSON));

        KeyStore keyStore = new KeyStoreRequest(apiRequest).createKeyStore("Test").get();
        assertNotNull(keyStore);
        assertEquals(1L, keyStore.getId());
        assertEquals("Test", keyStore.getName());
    }

    @Test
    @DisplayName("Update should return KeyStore")
    void shouldReturnUpdatedKeyStore() throws IOException, InterruptedException, ScytaleException, CertificateException {
        when(apiRequest.put(eq(KeyStoreRequest.KEYSTORE_URI + "/1"), any(JSONObject.class)))
                .thenReturn(TestUtils.setupHTTPResponse(200, "{\"id\": 1, \"name\": \"Updated KeyStore\"}"));

        KeyStore updatedKeyStore = new KeyStore(1L, "Updated KeyStore");
        KeyStore keyStore = new KeyStoreRequest(apiRequest).updateKeyStore(updatedKeyStore).get();
        assertNotNull(keyStore);
        assertEquals(1L, keyStore.getId());
        assertEquals("Updated KeyStore", keyStore.getName());
    }

    @Test
    @DisplayName("Returns a Keystore when found by exact name")
    void shouldReturnKeystoreByName() throws IOException, InterruptedException, InvalidKeystoreException, ScytaleException, CertificateException {
        String jsonArray = "[" + KEYSTORE_JSON + ", {\"id\": 2, \"name\": \"Test JSON\"}]";
        when(apiRequest.get(KeyStoreRequest.KEYSTORE_URI + "/search?name=Test"))
                .thenReturn(TestUtils.setupHTTPResponse(200, jsonArray));

        KeyStore keyStore = new KeyStoreRequest(apiRequest).searchByName("Test").get();
        assertNotNull(keyStore);
        assertEquals(1L, keyStore.getId());
        assertEquals("Test", keyStore.getName());
    }

    @Test
    @DisplayName("Throws when search by name returns invalid")
    void shouldThrowWhenSearchByNameInvalid() throws IOException, InterruptedException, InvalidKeystoreException, ScytaleException, CertificateException {
        when(apiRequest.get(KeyStoreRequest.KEYSTORE_URI + "/search?name=Test"))
                .thenReturn(TestUtils.setupHTTPResponse(200, "Not JSON"));

        Exception scytaleException = assertThrows(ScytaleException.class,
                () -> new KeyStoreRequest(apiRequest).searchByName("Test"));

        String expectedMessage = "API Returned invalid JSON";
        assertEquals(expectedMessage, scytaleException.getMessage());
    }

    @Test
    @DisplayName("Should throw when exact name not found")
    void shouldThrowWhenKeystoreNotFoundByName() throws IOException, InterruptedException, CertificateException {
        String jsonArray = "[" + KEYSTORE_JSON + ", {\"id\": 2, \"name\": \"Test JSON\"}]";
        when(apiRequest.get(KeyStoreRequest.KEYSTORE_URI + "/search?name=Test "))
                .thenReturn(TestUtils.setupHTTPResponse(200, jsonArray));

        Exception invalidKeyStoreException = assertThrows(InvalidKeystoreException.class,
                () -> new KeyStoreRequest(apiRequest).searchByName("Test "));

        String expectedMessage = "Keystore with name Test  does not exist";
        assertEquals(expectedMessage, invalidKeyStoreException.getMessage());
    }
}