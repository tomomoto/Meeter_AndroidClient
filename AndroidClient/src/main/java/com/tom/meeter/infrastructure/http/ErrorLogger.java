package com.tom.meeter.infrastructure.http;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;

public abstract class ErrorLogger<T> extends DisconnectLogger<T> {

    private static final String TAG = ErrorLogger.class.getCanonicalName();

    public ErrorLogger(Context ctx) {
        super(ctx);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (super.supportedErrorMapping(t)) {
            super.onFailure(call, t);
        } else {
            String message = t.getMessage();
            Toast.makeText(ctx, "ErrorLogger: " + message, Toast.LENGTH_LONG)
                  .show();
            Log.e(TAG, "ErrorLogger for " + ctx.getClass().getSimpleName()
                  + ", error: " + message);
        }
    }
}
