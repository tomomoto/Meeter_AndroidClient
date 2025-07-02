package com.tom.meeter.context.event;

import android.app.Application;

import com.tom.meeter.AppComponent;
import com.tom.meeter.context.event.activity.EventDispatcherActivity;
import com.tom.meeter.context.event.activity.EventLocationMapActivity;
import com.tom.meeter.context.event.activity.EventOnMapActivity;
import com.tom.meeter.context.event.activity.ProfileEventActivity;
import com.tom.meeter.context.event.activity.PublishEventActivity;
import com.tom.meeter.context.event.activity.UserEventActivity;

import dagger.BindsInstance;
import dagger.Component;

@EventScope
@Component(
      modules = {EventModule.class},
      dependencies = {AppComponent.class}
)
public interface EventComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        Builder appComponent(AppComponent appComponent);

        EventComponent build();
    }

    void inject(EventDispatcherActivity eventDispatcherActivity);

    void inject(ProfileEventActivity profileEventActivity);

    void inject(UserEventActivity userEventActivity);

    void inject(EventOnMapActivity eventOnMapActivity);

    void inject(EventLocationMapActivity eventLocationMapActivity);

    void inject(PublishEventActivity pea);

}
