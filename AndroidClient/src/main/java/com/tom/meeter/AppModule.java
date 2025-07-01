package com.tom.meeter;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tom.meeter.infrastructure.http.HttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
}
