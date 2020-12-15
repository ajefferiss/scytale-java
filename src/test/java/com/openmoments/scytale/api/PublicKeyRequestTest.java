package com.openmoments.scytale.api;

import com.openmoments.scytale.TestUtils;
import com.openmoments.scytale.entities.KeyStore;
import com.openmoments.scytale.entities.PublicKey;
import com.openmoments.scytale.exception.ScytaleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("PublicKey Request Tests")
class PublicKeyRequestTest {

    private static final APIRequest apiRequest = mock(APIRequest.class);
    private final KeyStore keyStore = new KeyStore(1L, "Test");

    @Test
    @DisplayName("Should return JSON list")
    void shouldReturnKeysAsJSONListInString() throws IOException, InterruptedException, ScytaleException {
        when(apiRequest.get(eq("keystores/1/keys"), any()))
                .thenReturn(TestUtils.setupHTTPResponse(200, "[{\"id\": 1, \"publicKey\": \"Test Updated\"}]"));

        List<PublicKey> getResponse = new PublicKeyRequest(apiRequest).getAll(keyStore);
        List<PublicKey> expectedList = new ArrayList<>();
        expectedList.add(new PublicKey(1L, "Test Updated"));

        assertEquals(expectedList, getResponse);
    }
}