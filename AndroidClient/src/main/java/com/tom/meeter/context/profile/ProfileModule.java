package com.tom.meeter.context.profile;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;
import static com.tom.meeter.infrastructure.common.RetrofitBuilder.createBuilder;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.tom.meeter.context.profile.repository.event.database.EventDao;
import com.tom.meeter.context.profile.repository.event.database.EventDatabase;
import com.tom.meeter.context.profile.repository.user.database.UserDao;
import com.tom.meeter.context.profile.repository.user.database.UserDatabase;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.service.SettingsService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class ProfileModule {

    @ProfileScope
    @NonNull
    @Provides
    public SettingsService provideSettingsService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create())
              .build()
              .create(SettingsService.class);
    }

    @ProfileScope
    @NonNull
    @Provides
    public ProfileService provideProfileService(Application app) {
        return createBuilder(app).create(ProfileService.class);
    }


    @ProfileScope
    @NonNull
    @Provides
    public EventDatabase provideEventDb(Application app) {
        return Room.databaseBuilder(app, EventDatabase.class, "event.db")
              .fallbackToDestructiveMigration()
              .build();
    }

    @ProfileScope
    @NonNull
    @Provides
    public EventDao provideEventDao(EventDatabase eventDatabase) {
        return eventDatabase.eventDao();
    }

    @ProfileScope
    @NonNull
    @Provides
    public UserDatabase provideUserDb(Application app) {
        return Room.databaseBuilder(app, UserDatabase.class, "user.db")
              .fallbackToDestructiveMigration()
              .build();
    }

    @ProfileScope
    @NonNull
    @Provides
    public UserDao provideUserDao(UserDatabase userDatabase) {
        return userDatabase.userDao();
    }

    @ProfileScope
    @NonNull
    @Provides
    public Executor provideExecutor() {
        return new ThreadPoolExecutor(4, 8, 1000, TimeUnit.SECONDS,
              new ArrayBlockingQueue<>(15, false));
    }
}
