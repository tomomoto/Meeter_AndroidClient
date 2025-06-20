package com.tom.meeter.infrastructure.http;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import android.util.Log;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpClient {

    private final String serverUrl;

    public HttpClient(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    private static final String TAG = HttpClient.class.getCanonicalName();
    private static final MediaType JSON = MediaType.get("application/json");

    private final OkHttpClient client = new OkHttpClient();

    public Call post(String json) {
        return post(json, serverUrl);
    }

    public Call post(String url, String json) {
        return client.newCall(
              new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(json, JSON))
                    .build());
    }

    public Call get(String url) {
        Log.d(TAG, "Making GET: '" + url + "'...");
        return client.newCall(
              new Request.Builder()
                    .url(url)
                    .get()
                    .build());
    }

    public Call patch(String json, String url, String authHeader) {
        String fullPath = serverUrl + url;
        Log.d(TAG, "Making PATCH: '" + fullPath + "' ...");
        return client.newCall(
              new Request.Builder()
                    .url(fullPath)
                    .header(AUTH_HEADER, authHeader)
                    .patch(RequestBody.create(json, JSON))
                    .build());
    }

    //new HttpClient().get(initServerPath(getBaseContext()) + "/users");
}
