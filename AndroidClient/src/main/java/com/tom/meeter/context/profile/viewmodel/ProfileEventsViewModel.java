package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.profile.event.domain.Event;
import com.tom.meeter.context.profile.user.service.UserService;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEventsViewModel extends ViewModel {

    private static final String TAG = ProfileEventsViewModel.class.getCanonicalName();

    private MutableLiveData<List<Event>> profileEventsLiveData = new MutableLiveData<>();

    private final UserService userService;

    @Inject
    public ProfileEventsViewModel(UserService userService) {
        logMethod(TAG, this);
        this.userService = userService;
    }

    public void getProfileEvents(String token, Runnable onAuthFail) {
        userService.getProfileEvents(Constants.getAuthHeader(token)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.code() == HttpCodes.OK && response.body() != null) {
                    profileEventsLiveData.setValue(response.body());
                    return;
                }
                if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                    onAuthFail.run();
                    return;
                }
                Log.d(TAG, "/profile: " + response.code() + ":" + response.body());
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<List<Event>> getProfileEventsLiveData() {
        return profileEventsLiveData;
    }
}