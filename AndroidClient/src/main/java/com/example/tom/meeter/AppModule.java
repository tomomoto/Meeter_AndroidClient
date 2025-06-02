package com.example.tom.meeter;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.tom.meeter.context.event.database.EventDao;
import com.example.tom.meeter.context.event.database.EventDatabase;
import com.example.tom.meeter.context.event.service.EventService;
import com.example.tom.meeter.context.user.database.UserDao;
import com.example.tom.meeter.context.user.database.UserDatabase;
import com.example.tom.meeter.context.user.service.UserService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    private static final String TAG = AppModule.class.getCanonicalName();
    private static final String IP = "192.168.127.59";
    private static final int PORT = 8084;
    private static final String SERVER_URL = "http://" + IP + ":" + PORT + "/";


    public AppModule() {
        Log.d(TAG, "Configuring AppModule... Server URL is [" + SERVER_URL + "]");
    }

    @Singleton
    @NonNull
    @Provides
    public UserService provideUserService() {
        return new Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserService.class);
    }

    @Singleton
    @NonNull
    @Provides
    public UserDatabase provideUserDb(Application app) {
        return Room.databaseBuilder(app, UserDatabase.class, "user.db")
            .fallbackToDestructiveMigration()
            .build();
    }

    @Singleton
    @NonNull
    @Provides
    public UserDao provideUserDao(UserDatabase userDatabase) {
        return userDatabase.userDao();
    }

    @Singleton
    @NonNull
    @Provides
    public Executor provideExecutor() {
        return new ThreadPoolExecutor(4, 8, 1000, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(15, false));
    }

    @Singleton
    @NonNull
    @Provides
    public EventService provideEventService() {
        return new Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventService.class);
    }

    @Singleton
    @NonNull
    @Provides
    public EventDatabase provideEventDb(Application app) {
        return Room.databaseBuilder(app, EventDatabase.class, "event.db")
            .fallbackToDestructiveMigration()
            .build();
    }

    @Singleton
    @NonNull
    @Provides
    public EventDao provideEventDao(EventDatabase eventDatabase) {
        return eventDatabase.eventDao();
    }
}
