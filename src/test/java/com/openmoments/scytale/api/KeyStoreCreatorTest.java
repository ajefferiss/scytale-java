package com.openmoments.scytale.api;

import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.exception.InvalidKeystoreException;
import com.openmoments.scytale.exception.ScytaleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Keystore Creator Tests")
class KeyStoreCreatorTest {

    private KeyStoreRequest request = mock(KeyStoreRequest.class);
    private static final String KEYSTORE_JSON = "{\"id\": 1, \"name\": \"Test\"}";

    @Test
    @DisplayName("Creation returns a keystore")
    void shouldReturnKeystoreOnCreate() throws InterruptedException, ScytaleException, IOException {
        when(request.createKeyStore(eq("Test"))).thenReturn(KEYSTORE_JSON);

        KeyStore keyStore = new KeyStoreCreator().request(request).name("Test").create();
        assertNotNull(keyStore);
        assertEquals(1L, keyStore.getId());
        assertEquals("Test", keyStore.getName());
    }

    @Test
    @DisplayName("Returns a Keystore when found by ID")
    void shouldReturnKeystoreById() throws InterruptedException, ScytaleException, IOException {
        when(request.getById(eq(1L))).thenReturn(KEYSTORE_JSON);

        KeyStore keyStore = new KeyStoreCreator().request(request).id(1L).byId();
        assertNotNull(keyStore);
        assertEquals(1L, keyStore.getId());
        assertEquals("Test", keyStore.getName());
    }

    @Test
    @DisplayName("Returns a Keystore when found by exact name")
    void shouldReturnKeystoreByName() throws InterruptedException, InvalidKeystoreException, IOException, ScytaleException {
        String jsonArray = "[" + KEYSTORE_JSON + ", {\"id\": 2, \"name\": \"Test JSON\"}]";
        when(request.searchByName(eq("Test"))).thenReturn(jsonArray);

        KeyStore keyStore = new KeyStoreCreator().request(request).name("Test").byName();
        assertNotNull(keyStore);
        assertEquals(1L, keyStore.getId());
        assertEquals("Test", keyStore.getName());
    }

    @Test
    @DisplayName("Should throw when exact name not found")
    void shouldThrowWhenKeystoreNotFoundByName() throws InterruptedException, ScytaleException, IOException {
        String jsonArray = "[" + KEYSTORE_JSON + ", {\"id\": 2, \"name\": \"Test JSON\"}]";
        when(request.searchByName(eq("Test "))).thenReturn(jsonArray);

        Exception invalidKeyStoreException = assertThrows(InvalidKeystoreException.class,
                () -> new KeyStoreCreator().request(request).name("Test ").byName());

        String expectedMessage = "Keystore with name Test  does not exist";
        assertEquals(expectedMessage, invalidKeyStoreException.getMessage());
    }
}