package com.example.tom.meeter;

import android.app.Application;

import com.example.tom.meeter.context.auth.infrastructure.AccountAuthenticator;
import com.example.tom.meeter.context.auth.login.activity.LoginActivity;
import com.example.tom.meeter.context.auth.registration.activity.RegistrationActivity;
import com.example.tom.meeter.context.profile.activity.ProfileActivity;
import com.example.tom.meeter.context.profile.fragment.ProfileFragment;
import com.example.tom.meeter.context.profile.fragment.UserEventsFragment;
import com.example.tom.meeter.infrastructure.injection.viewmodel.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {AppModule.class, ViewModelModule.class})
@Singleton
public interface AppComponent {

    @Component.Builder
    interface Builder {

        /*@BindsInstance
        Builder appModule(AppModule appModule);*/

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(ProfileActivity profileActivity);

    void inject(ProfileFragment profileFragment);

    void inject(UserEventsFragment userEventsFragment);

    void inject(LoginActivity loginActivity);

    void inject(RegistrationActivity registrationActivity);

    void inject(AccountAuthenticator accountAuthenticator);
}