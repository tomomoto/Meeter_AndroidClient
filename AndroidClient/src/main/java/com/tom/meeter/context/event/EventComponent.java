package com.tom.meeter.context.event;

import android.app.Application;

import com.tom.meeter.context.event.service.EventService;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {EventModule.class})
public interface EventComponent {

    EventService provideEventService();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        EventComponent build();
    }
}
