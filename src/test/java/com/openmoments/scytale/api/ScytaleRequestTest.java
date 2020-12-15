package com.openmoments.scytale.api;

import com.openmoments.scytale.exception.ScytaleException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScytaleRequestTest {

    private APIRequest apiRequest = mock(APIRequest.class);
    private ScytaleRequest scytaleRequest;

    @Test
    void shouldThrowWhenAPIRequestInvalid() {
        Exception illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> new ScytaleRequest(null));

        assertEquals("API Request interface is required", illegalArgumentException.getMessage());
    }

    @BeforeEach
    void setup() {
        scytaleRequest = new ScytaleRequest(apiRequest);
    }

    @Nested
    @DisplayName("HTTP GET Requests")
    class ScytaleGet {
        @Test
        @DisplayName("Non-success response throws")
        void shouldThrowWhenHTTPNot200() throws IOException, InterruptedException {
            when(apiRequest.get(any(), any())).thenReturn(setupResponse(403, "Forbidden 403"));

            Exception scytaleException = assertThrows(ScytaleException.class, () -> new ScytaleRequest(apiRequest).get("/"));
            String expectedMessage = "API response failed with Forbidden 403";
            assertEquals(expectedMessage, scytaleException.getMessage());
        }

        @Test
        @DisplayName("Should return request body")
        void shouldReturnRequestBody() throws IOException, InterruptedException, ScytaleException {
            when(apiRequest.get(any(), any())).thenReturn(setupResponse(200, "{'id': 1, 'name': 'Test'}"));

            String expected = "{'id': 1, 'name': 'Test'}";
            assertEquals(expected, new ScytaleRequest(apiRequest).get("/"));
        }
    }

    @Nested
    @DisplayName("HTTP POST Requests")
    class ScytalePost {
        private JSONObject postJson = new JSONObject().put("id", 1);

        @Test
        @DisplayName("Non-success response throws")
        void shouldThrowWhenHTTPNot200() throws IOException, InterruptedException {
            when(apiRequest.post(any(), any(), any())).thenReturn(setupResponse(403, "Forbidden 403"));

            Exception scytaleException = assertThrows(ScytaleException.class, () -> new ScytaleRequest(apiRequest).post("/", postJson));
            String expectedMessage = "API response failed with Forbidden 403";
            assertEquals(expectedMessage, scytaleException.getMessage());
        }
    }

    @Nested
    @DisplayName("HTTP PUT Requests")
    class ScytalePut {
        private JSONObject putJson = new JSONObject().put("id", 1);

        @Test
        @DisplayName("Non-success response throws")
        void shouldThrowWhenHTTPNot200() throws IOException, InterruptedException {
            when(apiRequest.put(any(), any(), any())).thenReturn(setupResponse(403, "Forbidden 403"));

            Exception scytaleException = assertThrows(ScytaleException.class, () -> new ScytaleRequest(apiRequest).put("/", putJson));
            String expectedMessage = "API response failed with Forbidden 403";
            assertEquals(expectedMessage, scytaleException.getMessage());
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