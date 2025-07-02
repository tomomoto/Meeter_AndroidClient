package com.tom.meeter.context.user;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.RetrofitBuilder.createBuilder;

import android.app.Application;

import androidx.annotation.NonNull;

import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.common.RetrofitBuilder;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {

    private static final String TAG = UserModule.class.getCanonicalName();

    public UserModule() {
        logMethod(TAG, this);
    }

    @UserScope
    @NonNull
    @Provides
    public UserService provideUserService(Application app) {
        return createBuilder(app, RetrofitBuilder.jtm)
              .create(UserService.class);
    }
}
