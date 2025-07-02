package com.tom.meeter.context.event;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import androidx.annotation.NonNull;

import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.infrastructure.common.RetrofitBuilder;

import dagger.Module;
import dagger.Provides;

@Module
public class EventModule {

    private static final String TAG = EventModule.class.getCanonicalName();

    public EventModule() {
        logMethod(TAG, this);
    }

    @EventScope
    @NonNull
    @Provides
    public EventService provideEventService(Application app) {
        return RetrofitBuilder.createBuilder(app).create(EventService.class);
    }
}
