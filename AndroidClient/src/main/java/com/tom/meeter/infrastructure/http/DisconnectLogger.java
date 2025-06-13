package com.tom.meeter.infrastructure.http;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.tom.meeter.R;

import retrofit2.Call;
import retrofit2.Callback;

public abstract class DisconnectLogger<T> implements Callback<T> {

    private static final String TAG = DisconnectLogger.class.getCanonicalName();
    protected final Activity activity;

    public DisconnectLogger(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Toast.makeText(activity, R.string.server_is_unreachable, Toast.LENGTH_SHORT)
              .show();
        Log.d(TAG, "DisconnectLogger: " + activity.getComponentName() + ": "
              + activity.getResources().getString(R.string.server_is_unreachable));
    }
}
