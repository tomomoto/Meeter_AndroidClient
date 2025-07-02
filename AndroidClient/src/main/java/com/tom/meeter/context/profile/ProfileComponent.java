package com.tom.meeter.context.profile;

import android.app.Application;

import com.tom.meeter.AppComponent;
import com.tom.meeter.context.profile.component.StatusesFilterDialog;
import com.tom.meeter.context.profile.component.activity.ProfileActivity;
import com.tom.meeter.context.profile.component.activity.SettingsActivity;
import com.tom.meeter.context.profile.component.activity.SubscribersActivity;
import com.tom.meeter.context.profile.component.activity.SubscriptionsActivity;
import com.tom.meeter.context.profile.component.fragment.ActiveEventsFragment;
import com.tom.meeter.context.profile.component.fragment.CreateEventFragment;
import com.tom.meeter.context.profile.component.fragment.GoogleMapsFragment;
import com.tom.meeter.context.profile.component.fragment.ProfileEventsFragment;
import com.tom.meeter.context.profile.component.fragment.ProfileFragment;

import dagger.BindsInstance;
import dagger.Component;

@ProfileScope
@Component(
      modules = {ProfileModule.class},
      dependencies = {AppComponent.class}
)
public interface ProfileComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        Builder appComponent(AppComponent appComponent);

        ProfileComponent build();
    }

    void inject(ProfileActivity profileActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(ProfileFragment profileFragment);

    void inject(GoogleMapsFragment googleMapsFragment);

    void inject(ActiveEventsFragment activeEventsFragment);

    void inject(ProfileEventsFragment profileEventsFragment);

    void inject(SubscribersActivity subscribersActivity);

    void inject(SubscriptionsActivity subscriptionsActivity);

    void inject(CreateEventFragment createEventFragment);

    void inject(StatusesFilterDialog statusesFilterDialog);

}
