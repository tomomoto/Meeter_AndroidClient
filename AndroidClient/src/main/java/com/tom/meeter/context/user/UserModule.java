package com.tom.meeter.context.user;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tom.meeter.context.user.service.UserService;

import java.util.TimeZone;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create(
                    JsonMapper.builder()
                          .addModule(new JavaTimeModule())
                          .build()
                          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                          .setTimeZone(TimeZone.getDefault())))
              .build()
              .create(UserService.class);
    }
}
