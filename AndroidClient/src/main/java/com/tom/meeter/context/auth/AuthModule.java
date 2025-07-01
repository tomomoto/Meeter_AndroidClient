package com.tom.meeter.context.auth;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import androidx.annotation.NonNull;

import com.tom.meeter.AppModule;
import com.tom.meeter.context.auth.service.AuthService;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
//import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AuthModule {

    private static final String TAG = AppModule.class.getCanonicalName();

    public AuthModule() {
        logMethod(TAG, this);
    }

    @AuthScope
    @NonNull
    @Provides
    public AuthService provideAuthService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create())
              .build()
              .create(AuthService.class);
    }
}
