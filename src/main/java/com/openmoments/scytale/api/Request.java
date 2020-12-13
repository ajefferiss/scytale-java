package com.openmoments.scytale.api;

import com.openmoments.scytale.config.PropertiesLoader;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class Request implements APIRequest {
    private static final String AUTHENTICATION_KEY_HEADER = "X-API-Key";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Properties properties;
    private HttpRequest.Builder request;

    public Request() throws IOException {
        properties = new PropertiesLoader().getProperties();
    }

    @Override
    public HttpResponse<String> get(String uri, JSONObject json, APIRequestCallback callbacks) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public HttpResponse<String> post(String uri, JSONObject json, APIRequestCallback callbacks) throws IOException, InterruptedException {
        String authType = properties.getProperty("api.type");

        StringBuilder apiURL = new StringBuilder();
        apiURL.append(properties.getProperty("api.url").replaceAll("/+$", ""));
        apiURL.append("/");
        apiURL.append(uri.replaceAll("^/+", ""));

        request = HttpRequest.newBuilder().uri(URI.create(apiURL.toString()));
        request.header("Content-Type", "application/json");

        if (authType != null && authType.equalsIgnoreCase("key")) {
            request.header(AUTHENTICATION_KEY_HEADER, properties.get("api.authentication").toString());
        }

        request.POST(HttpRequest.BodyPublishers.ofString(json.toString()));

        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

}
