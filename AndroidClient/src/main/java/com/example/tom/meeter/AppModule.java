package com.example.tom.meeter;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;

import com.example.tom.meeter.context.event.domain.EventDao;
import com.example.tom.meeter.context.event.domain.EventDatabase;
import com.example.tom.meeter.context.event.domain.EventService;
import com.example.tom.meeter.context.user.domain.UserDao;
import com.example.tom.meeter.context.user.domain.UserDatabase;
import com.example.tom.meeter.context.user.domain.UserService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module/*(includes = {ViewModelModule.class})*/
public class AppModule {

    private static String BASE_URL = "http://10.137.57.156:3000/";

/*    private Context appContext;

    public AppModule(Context appContext) {
        this.appContext = appContext;
    }

    @Provides
    @Singleton
    public Context providesContext() {
        return appContext;
    }*/

    @Singleton
    @NonNull
    @Provides
    public UserService provideUserService() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                //.addCallAdapterFactory(LiveDataCallAdapterFactory())
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
                .baseUrl(BASE_URL)
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