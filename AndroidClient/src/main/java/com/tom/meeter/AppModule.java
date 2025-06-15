package com.tom.meeter;

import static com.tom.meeter.infrastructure.common.GlobalConstants.getServerPath;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.tom.meeter.context.profile.event.database.EventDao;
import com.tom.meeter.context.profile.event.database.EventDatabase;
import com.tom.meeter.context.profile.event.service.EventService;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.settings.service.SettingsService;
import com.tom.meeter.context.profile.user.database.UserDao;
import com.tom.meeter.context.profile.user.database.UserDatabase;
import com.tom.meeter.context.user.service.UserService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
              .addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(ProfileService.class);
    }

    @AppScope
    @NonNull
    @Provides
    public UserService provideUserService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(GsonConverterFactory.create())
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
              .addConverterFactory(GsonConverterFactory.create())
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
              .addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(SettingsService.class);
    }
}
