package com.tom.meeter.infrastructure.http;

import android.app.Activity;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;

public class ActivityRecreatorOnAuthFailure<T> extends HttpErrorLogger<T> {

    private static final String TAG = ActivityRecreatorOnAuthFailure.class.getCanonicalName();
    private final Activity activity;

    public ActivityRecreatorOnAuthFailure(Activity activity) {
        super(activity.getApplicationContext());
        this.activity = activity;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        super.onResponse(call, response);
        if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
            Log.d(TAG, "Recreating: " + activity.getClass().getCanonicalName());
            activity.recreate();
        }
    }
}
