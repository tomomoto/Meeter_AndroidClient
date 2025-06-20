package com.tom.meeter.context.event;

import android.app.Application;

import com.tom.meeter.context.event.activity.EventActivity;
import com.tom.meeter.context.event.activity.EventLocationMapActivity;
import com.tom.meeter.context.event.activity.EventOnMapActivity;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.event.viewmodel.EventViewModelModule;
import com.tom.meeter.context.image.ImageComponent;
import com.tom.meeter.context.token.TokenComponent;

import dagger.BindsInstance;
import dagger.Component;

@EventScope
@Component(
      modules = {
            EventModule.class,
            EventViewModelModule.class
      },
      dependencies = {
            TokenComponent.class,
            ImageComponent.class
      })
public interface EventComponent {

    EventService provideEventService();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        Builder tokenComponent(TokenComponent tokenComponent);

        Builder imageComponent(ImageComponent tokenComponent);

        EventComponent build();
    }

    void inject(EventActivity eventActivity);

    void inject(EventOnMapActivity eventOnMapActivity);

    void inject(EventLocationMapActivity eventLocationMapActivity);
}
