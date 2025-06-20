package com.tom.meeter;

import android.app.Application;

import com.tom.meeter.context.auth.AuthComponent;
import com.tom.meeter.context.event.EventComponent;
import com.tom.meeter.context.event.activity.EventActivity;
import com.tom.meeter.context.event.activity.EventLocationMapActivity;
import com.tom.meeter.context.event.activity.EventOnMapActivity;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.context.profile.activity.SettingsActivity;
import com.tom.meeter.context.profile.fragment.ActiveEventsFragment;
import com.tom.meeter.context.profile.fragment.GoogleMapsFragment;
import com.tom.meeter.context.profile.fragment.ProfileFragment;
import com.tom.meeter.context.profile.fragment.UserEventsFragment;
import com.tom.meeter.context.token.TokenComponent;
import com.tom.meeter.context.user.activity.UserActivity;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(
      modules = {AppModule.class, ViewModelModule.class},
      dependencies = {TokenComponent.class, AuthComponent.class, EventComponent.class})
@AppScope
public interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        Builder authComponent(AuthComponent authComponent);
        Builder tokenComponent(TokenComponent tokenComponent);
        Builder eventComponent(EventComponent eventComponent);

        AppComponent build();
    }

    void inject(ProfileActivity profileActivity);

    void inject(ProfileFragment profileFragment);

    void inject(GoogleMapsFragment googleMapsFragment);

    void inject(SettingsActivity settingsActivity);

    void inject(UserActivity userActivity);

    void inject(EventActivity eventActivity);

    void inject(ActiveEventsFragment activeEventsFragment);

    void inject(UserEventsFragment userEventsFragment);

    void inject(EventOnMapActivity eventOnMapActivity);
    void inject(EventLocationMapActivity eventLocationMapActivity);
}