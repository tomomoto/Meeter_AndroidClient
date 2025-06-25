package com.tom.meeter.context.auth;

import android.app.Application;

import com.tom.meeter.AppComponent;
import com.tom.meeter.context.auth.activity.LoginActivity;
import com.tom.meeter.context.auth.activity.RegistrationActivity;
import com.tom.meeter.context.auth.infrastructure.AccountAuthenticator;

import dagger.BindsInstance;
import dagger.Component;

@AuthScope
@Component(modules = {AuthModule.class},
      dependencies = {AppComponent.class})
public interface AuthComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        Builder appComponent(AppComponent appComponent);

        AuthComponent build();
    }


    void inject(LoginActivity loginActivity);

    void inject(RegistrationActivity registrationActivity);

    void inject(AccountAuthenticator accountAuthenticator);
}
