package com.example.tom.meeter;

import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

public class App extends Application {

    private static final String TAG = App.class.getCanonicalName();
    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        logMethod(TAG, this);
        component = buildComponent();
    }

    @Override
    public void onTerminate() {
        logMethod(TAG, this);
        super.onTerminate();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                //.appModule(new AppModule(this))
                .application(this)
                .build();
    }

    public AppComponent getComponent() {
        return component;
    }
}
