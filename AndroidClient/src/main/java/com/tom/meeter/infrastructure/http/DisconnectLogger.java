package com.tom.meeter.infrastructure.http;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tom.meeter.R;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;

public abstract class DisconnectLogger<T> implements Callback<T> {

    private static final String TAG = DisconnectLogger.class.getCanonicalName();

    private final Context ctx;

    public DisconnectLogger(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof SocketTimeoutException ste) {
            Toast.makeText(ctx, R.string.server_is_unreachable, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "DisconnectLogger for " + ctx.getClass().getSimpleName()
                  + " : " + ctx.getResources().getString(R.string.server_is_unreachable)
                  + ", error: " + t.getMessage());
        } else {
            Toast.makeText(ctx, "ERROR", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "DisconnectLogger for " + ctx.getClass().getSimpleName()
                  + ", error: " + t.getMessage());
        }
    }
}
