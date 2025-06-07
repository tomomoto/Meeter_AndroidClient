package com.tom.meeter.infrastructure.http;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    private static final String TAG = HttpClient.class.getCanonicalName();
    private static final MediaType JSON = MediaType.get("application/json");

    private static final Callback CALLBACK_LOGGER = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.d(TAG, "IOException: ", e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            Log.d(TAG, "usersResp: " + response.body().string());
        }
    };

    private final OkHttpClient client = new OkHttpClient();

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public Call get(String url, Callback callback) {
        Log.d(TAG, "Making GET: '" + url + "'...");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public void get(String url) {
        get(url, CALLBACK_LOGGER);
    }

    //new HttpClient().get(initServerPath(getBaseContext()) + "/users");
}
