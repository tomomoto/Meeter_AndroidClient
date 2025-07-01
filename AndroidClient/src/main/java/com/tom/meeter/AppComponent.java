package com.tom.meeter;

import android.app.Application;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.image.activity.BaseUploadActivity;
import com.tom.meeter.context.launcher.Launcher;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.context.profile.activity.SettingsActivity;
import com.tom.meeter.context.profile.activity.SubscribersActivity;
import com.tom.meeter.context.profile.activity.SubscriptionsActivity;
import com.tom.meeter.context.profile.fragment.ActiveEventsFragment;
import com.tom.meeter.context.profile.fragment.CreateEventFragment;
import com.tom.meeter.context.profile.fragment.GoogleMapsFragment;
import com.tom.meeter.context.profile.fragment.ProfileEventsFragment;
import com.tom.meeter.context.profile.fragment.ProfileFragment;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.service.UserService;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Component(
      modules = {
            AppModule.class,
            ProfileModule.class,
            TokenModule.class,
            ImageModule.class,
            UserModule.class
      })
@Singleton
public interface AppComponent {

    TokenService provideTokenService();

    ImageDownloader provideImageDownloader();

    UserService provideUserService();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(Launcher launcher);

    void inject(ProfileActivity profileActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(ProfileFragment profileFragment);

    void inject(GoogleMapsFragment googleMapsFragment);

    void inject(ActiveEventsFragment activeEventsFragment);

    void inject(ProfileEventsFragment profileEventsFragment);

    void inject(SubscribersActivity subscribersActivity);

    void inject(SubscriptionsActivity subscriptionsActivity);

    void inject(BaseUploadActivity baseUploadActivity);

    void inject(CreateEventFragment createEventFragment);

}
