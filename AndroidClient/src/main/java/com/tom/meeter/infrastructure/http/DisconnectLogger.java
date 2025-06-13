package com.tom.meeter.infrastructure.http;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tom.meeter.R;

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
        Toast.makeText(ctx, R.string.server_is_unreachable, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "DisconnectLogger for " + ctx.getPackageName()
              + " : " + ctx.getResources().getString(R.string.server_is_unreachable)
              + ", error: " + t.getMessage());
    }
}
