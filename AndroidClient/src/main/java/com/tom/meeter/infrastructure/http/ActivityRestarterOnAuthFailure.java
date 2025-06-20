package com.tom.meeter.infrastructure.http;

import androidx.fragment.app.Fragment;

import com.tom.meeter.infrastructure.common.InfrastructureHelper;

import retrofit2.Call;
import retrofit2.Response;

public class ActivityRestarterOnAuthFailure<T> extends ErrorLogger<T> {
    private static final String TAG = ActivityRestarterOnAuthFailure.class.getCanonicalName();
    private final Fragment fragment;

    public ActivityRestarterOnAuthFailure(Fragment fragment) {
        super(fragment.getContext());
        this.fragment = fragment;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
            InfrastructureHelper.restartActivityFromFragment(fragment);
            // TODO when restore from persisted state will be done, if necessary
            //InfrastructureHelper.recreateActivityFromFragment(this);
        }
    }
}
