package com.tom.meeter;

import android.app.Application;

import com.tom.meeter.context.auth.AuthComponent;
import com.tom.meeter.context.event.EventComponent;
import com.tom.meeter.context.image.ImageComponent;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.context.profile.activity.SettingsActivity;
import com.tom.meeter.context.profile.activity.SubscribersActivity;
import com.tom.meeter.context.profile.activity.SubscriptionsActivity;
import com.tom.meeter.context.profile.fragment.ActiveEventsFragment;
import com.tom.meeter.context.profile.fragment.GoogleMapsFragment;
import com.tom.meeter.context.profile.fragment.ProfileEventsFragment;
import com.tom.meeter.context.profile.fragment.ProfileFragment;
import com.tom.meeter.context.token.TokenComponent;
import com.tom.meeter.context.user.UserComponent;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(
      modules = {
            AppModule.class,
            ViewModelModule.class
      },
      dependencies = {
            TokenComponent.class,
            AuthComponent.class,
            ImageComponent.class,

            EventComponent.class,
            UserComponent.class
      })
@AppScope
public interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        Builder authComponent(AuthComponent authComponent);

        Builder tokenComponent(TokenComponent tokenComponent);

        Builder eventComponent(EventComponent eventComponent);

        Builder imageComponent(ImageComponent imageComponent);

        Builder userComponent(UserComponent userComponent);

        AppComponent build();
    }

    void inject(ProfileActivity profileActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(ProfileFragment profileFragment);

    void inject(GoogleMapsFragment googleMapsFragment);

    void inject(ActiveEventsFragment activeEventsFragment);

    void inject(ProfileEventsFragment profileEventsFragment);

    void inject(SubscribersActivity subscribersActivity);

    void inject(SubscriptionsActivity subscriptionsActivity);

}
