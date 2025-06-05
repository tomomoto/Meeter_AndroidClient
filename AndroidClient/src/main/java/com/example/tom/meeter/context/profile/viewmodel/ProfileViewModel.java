package com.example.tom.meeter.context.profile.viewmodel;

import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.tom.meeter.context.user.domain.User;
import com.example.tom.meeter.context.user.service.UserService;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private static final String TAG = ProfileViewModel.class.getCanonicalName();

    private MutableLiveData<User> userLiveData = new MutableLiveData<>();

    private final UserService userService;

    @Inject
    public ProfileViewModel(UserService userService) {
        logMethod(TAG, this);
        this.userService = userService;
    }

    public void getProfile(String auth) {
        userService.getProfile(auth).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() != null) {
                    userLiveData.setValue(response.body());
                } else {
                    Log.d(TAG, "Response is null.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }
}

