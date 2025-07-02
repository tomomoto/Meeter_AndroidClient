package com.tom.meeter;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.RetrofitBuilder.createBuilder;

import android.app.Application;

import androidx.annotation.NonNull;

import com.tom.meeter.infrastructure.common.RetrofitBuilder;
import com.tom.meeter.infrastructure.components.UserLoader;
import com.tom.meeter.infrastructure.http.HttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private static final String TAG = AppModule.class.getCanonicalName();

    public AppModule() {
        logMethod(TAG, this);
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
        return createBuilder(app, RetrofitBuilder.jtm).create(UserLoader.class);
    }
}
