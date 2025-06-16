package com.tom.meeter.context.event;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import androidx.annotation.NonNull;

import com.tom.meeter.context.event.service.EventService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class EventModule {

    private static final String TAG = EventModule.class.getCanonicalName();

    public EventModule() {
        logMethod(TAG, this);
    }

    @Singleton
    @NonNull
    @Provides
    public EventService provideEventService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(EventService.class);
    }
}