package com.tom.meeter;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tom.meeter.context.image.ImageService;
import com.tom.meeter.context.profile.event.database.EventDao;
import com.tom.meeter.context.profile.event.database.EventDatabase;
import com.tom.meeter.context.profile.event.service.EventService;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.settings.service.SettingsService;
import com.tom.meeter.context.profile.user.database.UserDao;
import com.tom.meeter.context.profile.user.database.UserDatabase;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.http.HttpClient;

import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    @AppScope
    @NonNull
    @Provides
    public ProfileService provideProfileService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create(
                    JsonMapper.builder()
                          .addModule(new JavaTimeModule())
                          .addModule(new Jdk8Module())
                          .serializationInclusion(JsonInclude.Include.NON_NULL)
                          .build()
                          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                          .setTimeZone(TimeZone.getDefault())))
              .build()
              .create(ProfileService.class);
    }

    @AppScope
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
              //.addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(UserService.class);
    }

    @AppScope
    @NonNull
    @Provides
    public UserDatabase provideUserDb(Application app) {
        return Room.databaseBuilder(app, UserDatabase.class, "user.db")
              .fallbackToDestructiveMigration()
              .build();
    }

    @AppScope
    @NonNull
    @Provides
    public UserDao provideUserDao(UserDatabase userDatabase) {
        return userDatabase.userDao();
    }

    @AppScope
    @NonNull
    @Provides
    public Executor provideExecutor() {
        return new ThreadPoolExecutor(4, 8, 1000, TimeUnit.SECONDS,
              new ArrayBlockingQueue<>(15, false));
    }

    @AppScope
    @NonNull
    @Provides
    public EventService provideEventService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create())
              //.addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(EventService.class);
    }

    @AppScope
    @NonNull
    @Provides
    public EventDatabase provideEventDb(Application app) {
        return Room.databaseBuilder(app, EventDatabase.class, "event.db")
              .fallbackToDestructiveMigration()
              .build();
    }

    @AppScope
    @NonNull
    @Provides
    public EventDao provideEventDao(EventDatabase eventDatabase) {
        return eventDatabase.eventDao();
    }

    @AppScope
    @NonNull
    @Provides
    public SettingsService provideSettingsService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create())
              //.addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(SettingsService.class);
    }

    @AppScope
    @NonNull
    @Provides
    public ImageService provideImageService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .build()
              .create(ImageService.class);
    }

    @AppScope
    @NonNull
    @Provides
    public HttpClient provideHttpClient(Application app) {
        return new HttpClient(getServerPath(app));
    }
}
