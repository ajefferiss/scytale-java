package com.openmoments.scytale.api;

import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface APIRequest {
    HttpResponse<String> post(String uri, JSONObject json, APIRequestCallback callbacks) throws IOException, InterruptedException;
}
