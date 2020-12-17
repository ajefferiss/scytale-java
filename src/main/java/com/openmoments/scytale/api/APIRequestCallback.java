package com.openmoments.scytale.api;

import java.net.http.HttpResponse;

public interface APIRequestCallback {
    void onSuccess(HttpResponse<String> response);
    void onError(HttpResponse<String> error);
}
