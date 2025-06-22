package com.tom.meeter.infrastructure.http;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tom.meeter.R;

import java.net.ConnectException;
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
        if (supportedErrorMapping(t)) {
            Toast.makeText(ctx, R.string.server_is_unreachable, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "DisconnectLogger for [" + ctx.getClass().getCanonicalName()
                  + "]: " + ctx.getResources().getString(R.string.server_is_unreachable)
                  + " Error: " + t.getClass().getCanonicalName() + " - " + t.getMessage());
        }
    }

    protected boolean supportedErrorMapping(Throwable t) {
        if (t instanceof SocketTimeoutException
              || t instanceof ConnectException) {
            return true;
        }
        return false;
    }
}
