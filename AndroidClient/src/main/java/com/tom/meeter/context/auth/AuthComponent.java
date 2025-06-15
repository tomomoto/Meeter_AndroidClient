package com.tom.meeter.context.auth;

import android.app.Application;

import com.tom.meeter.context.auth.activity.LoginActivity;
import com.tom.meeter.context.auth.activity.RegistrationActivity;
import com.tom.meeter.context.auth.infrastructure.AccountAuthenticator;
import com.tom.meeter.context.auth.service.TokenService;
import com.tom.meeter.context.launcher.Launcher;

import dagger.BindsInstance;
import dagger.Component;

@AuthModuleScope

@Component(modules = {AuthModule.class})
public interface AuthComponent {
    TokenService providesTokenService();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AuthComponent build();
    }


    void inject(LoginActivity loginActivity);

    void inject(Launcher launcher);

    void inject(RegistrationActivity registrationActivity);

    void inject(AccountAuthenticator accountAuthenticator);
}
