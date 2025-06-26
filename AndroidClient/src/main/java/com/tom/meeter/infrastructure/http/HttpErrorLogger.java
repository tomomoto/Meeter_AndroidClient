package com.tom.meeter.infrastructure.http;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class HttpErrorLogger<T> extends ErrorLogger<T> {

    private static final String TAG = HttpErrorLogger.class.getCanonicalName();

    public HttpErrorLogger(Context ctx) {
        super(ctx);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            return;
        }
        String errorMessage;
        try (ResponseBody r = response.errorBody()) {
            errorMessage = r.string();
        } catch (IOException e) {
            errorMessage = "Unable to extract error body.";
        }
        int code = response.code();
        showMessage(ctx, code + "/" + errorMessage);
        Log.e(TAG, "HTTP request failed for [" + ctx.getClass().getCanonicalName()
              + "] with http code [" + code + "] and body " + errorMessage);
    }
}
