package com.tom.meeter;

import android.app.Application;

import com.tom.meeter.context.auth.AuthComponent;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.context.profile.activity.SettingsActivity;
import com.tom.meeter.context.profile.fragment.ProfileFragment;
import com.tom.meeter.context.profile.fragment.UserEventsFragment;
import com.tom.meeter.context.token.TokenComponent;
import com.tom.meeter.context.user.activity.UserActivity;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(
      modules = {AppModule.class, ViewModelModule.class},
      dependencies = {TokenComponent.class, AuthComponent.class})
@AppScope
public interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        Builder authComponent(AuthComponent authComponent);
        Builder tokenComponent(TokenComponent tokenComponent);

        AppComponent build();
    }

    void inject(ProfileActivity profileActivity);

    void inject(ProfileFragment profileFragment);

    void inject(UserEventsFragment userEventsFragment);

    void inject(SettingsActivity settingsActivity);

    void inject(UserActivity userActivity);
}