package com.tom.meeter;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tom.meeter.infrastructure.components.UserLoader;
import com.tom.meeter.infrastructure.http.HttpClient;

import java.util.TimeZone;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class AppModule {

    private static final String TAG = AppModule.class.getCanonicalName();

    public AppModule() {
        Log.d(TAG, "Configuring AppModule...");
    }

    @Singleton
    @NonNull
    @Provides
    public HttpClient provideHttpClient(Application app) {
        return new HttpClient(getServerPath(app));
    }

    @Singleton
    @NonNull
    @Provides
    public UserLoader provideUserLoader(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create(
                    JsonMapper.builder()
                          .addModule(new JavaTimeModule())
                          .build()
                          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                          .setTimeZone(TimeZone.getDefault())))
              .build()
              .create(UserLoader.class);
    }
}
