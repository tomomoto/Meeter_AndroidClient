package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.factory.ProfileEventsViewModelAssistedFactory;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileEventsViewModel extends ViewModel {

    private static final String TAG = ProfileEventsViewModel.class.getCanonicalName();

    private final String auth;
    private final Context ctx;
    private final Runnable onNotAuthenticated;

    private final MutableLiveData<List<EventDTO>> events = new MutableLiveData<>();
    private final ProfileService service;

    @AssistedInject
    public ProfileEventsViewModel(
          ProfileService service,
          @Assisted String auth,
          @Assisted Context ctx,
          @Assisted Runnable onNotAuthenticated) {
        logMethod(TAG, this);
        this.service = service;
        this.auth = auth;
        this.ctx = ctx.getApplicationContext();
        this.onNotAuthenticated = onNotAuthenticated;
        init();
    }

    public static ViewModelProvider.Factory factory(
          ProfileEventsViewModelAssistedFactory assistedFactory,
          String auth, Context ctx, Runnable onNotAuthenticated) {
        return new ViewModelProvider.Factory() {
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) assistedFactory.create(auth, ctx, onNotAuthenticated);
            }
        };
    }

    public void init() {
        service.getProfileEvents(auth).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(
                        Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                      super.onResponse(call, response);
                      if (response.code() != HttpCodes.OK || response.body() == null) {
                          return;
                      }
                      events.setValue(response.body());
                      return;
                  }
              }
        );
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<List<EventDTO>> getEvents() {
        return events;
    }
}
