package com.tom.meeter.context.event;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tom.meeter.context.event.service.EventService;

import java.util.TimeZone;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class EventModule {

    private static final String TAG = EventModule.class.getCanonicalName();

    public EventModule() {
        logMethod(TAG, this);
    }

    @EventScope
    @NonNull
    @Provides
    public EventService provideEventService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create(
                    JsonMapper.builder()
                          .addModule(new JavaTimeModule())
                          //.addModule(new Jdk8Module().configureReadAbsentAsNull(false))
                          .addModule(new Jdk8Module())
                          .serializationInclusion(JsonInclude.Include.NON_NULL)
                          .build()
                          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                          .setTimeZone(TimeZone.getDefault())))
              .build()
              .create(EventService.class);
    }
}