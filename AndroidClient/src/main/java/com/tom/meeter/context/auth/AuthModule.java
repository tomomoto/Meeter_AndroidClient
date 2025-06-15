package com.tom.meeter.context.auth;

import static com.tom.meeter.infrastructure.common.GlobalConstants.getServerPath;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tom.meeter.AppModule;
import com.tom.meeter.context.auth.service.AuthService;
import com.tom.meeter.context.auth.service.TokenService;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AuthModule {

    private static final String TAG = AppModule.class.getCanonicalName();

    public AuthModule() {
        Log.d(TAG, "Configuring AuthModule...");
    }

    @AuthModuleScope
    @NonNull
    @Provides
    public TokenService provideTokenService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(TokenService.class);
    }

    @AuthModuleScope
    @NonNull
    @Provides
    public AuthService provideAuthService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(AuthService.class);
    }
}
