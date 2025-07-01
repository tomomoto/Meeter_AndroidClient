package com.tom.meeter;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import androidx.annotation.NonNull;

import com.tom.meeter.context.token.service.TokenService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class TokenModule {

    private static final String TAG = TokenModule.class.getCanonicalName();

    public TokenModule() {
        logMethod(TAG, this);
    }

    @Singleton
    @NonNull
    @Provides
    public TokenService providesTokenService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create())
              .build()
              .create(TokenService.class);
    }

}
