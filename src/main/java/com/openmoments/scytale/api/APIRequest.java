package com.openmoments.scytale.api;

import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.cert.CertificateException;
import java.util.concurrent.CompletableFuture;

public interface APIRequest {
    HttpResponse<String> get(String uri) throws IOException, InterruptedException, CertificateException;
    HttpResponse<String> post(String uri, JSONObject json) throws IOException, InterruptedException, CertificateException;
    HttpResponse<String> put(String uri, JSONObject json) throws IOException, InterruptedException, CertificateException;

    CompletableFuture<HttpResponse<String>> getAsync(String uri) throws IOException, InterruptedException, CertificateException;
    CompletableFuture<HttpResponse<String>> postAsync(String uri, JSONObject json) throws IOException, InterruptedException, CertificateException;
    CompletableFuture<HttpResponse<String>> putAsync(String uri, JSONObject json) throws IOException, InterruptedException, CertificateException;
}
