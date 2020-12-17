package com.openmoments.scytale.api;

import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface APIRequest {
    HttpResponse<String> get(String uri, APIRequestCallback callbacks) throws IOException, InterruptedException;
    HttpResponse<String> post(String uri, JSONObject json, APIRequestCallback callbacks) throws IOException, InterruptedException;
    HttpResponse<String> put(String uri, JSONObject json, APIRequestCallback callbacks) throws IOException, InterruptedException;
}
