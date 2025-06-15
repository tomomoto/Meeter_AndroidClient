package com.tom.meeter.context.token;

import android.app.Application;

import com.tom.meeter.context.launcher.Launcher;
import com.tom.meeter.context.token.service.TokenService;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {TokenModule.class})
public interface TokenComponent {

    TokenService providesTokenService();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        TokenComponent build();
    }


    void inject(Launcher launcher);
}