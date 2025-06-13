package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.profile.user.domain.User;
import com.tom.meeter.context.profile.user.service.UserService;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.http.HttpCodes;

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

    public void getProfile(String token, Runnable onAuthFail) {
        userService.getProfile(Constants.getAuthHeader(token)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == HttpCodes.OK && response.body() != null) {
                    userLiveData.setValue(response.body());
                    return;
                }
                //TODO token invalidation
                if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                    onAuthFail.run();
                    return;
                }
                Log.d(TAG, "/profile: " + response.code() + ":" + response.body());
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

