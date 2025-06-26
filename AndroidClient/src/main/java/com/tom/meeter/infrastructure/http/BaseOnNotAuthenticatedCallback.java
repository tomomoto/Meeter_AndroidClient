package com.tom.meeter.infrastructure.http;

import android.content.Context;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;

public class BaseOnNotAuthenticatedCallback<T> extends HttpErrorLogger<T> {

    private static final String TAG = BaseOnNotAuthenticatedCallback.class.getCanonicalName();

    private final Runnable onNotAuthenticated;

    public BaseOnNotAuthenticatedCallback(Context ctx, Runnable onNotAuthenticated) {
        super(ctx);
        this.onNotAuthenticated = onNotAuthenticated;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> resp) {
        super.onResponse(call, resp);
        if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
            Log.d(TAG, "On auth fail for: " + ctx.getClass().getCanonicalName());
            onNotAuthenticated.run();
        }
    }
}
