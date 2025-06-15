package com.tom.meeter.context.auth;

import android.app.Application;

import com.tom.meeter.context.auth.activity.LoginActivity;
import com.tom.meeter.context.auth.activity.RegistrationActivity;
import com.tom.meeter.context.auth.infrastructure.AccountAuthenticator;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AuthModule.class})
public interface AuthComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AuthComponent build();
    }


    void inject(LoginActivity loginActivity);

    void inject(RegistrationActivity registrationActivity);

    void inject(AccountAuthenticator accountAuthenticator);
}
