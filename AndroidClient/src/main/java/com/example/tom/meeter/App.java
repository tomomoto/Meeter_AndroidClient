package com.example.tom.meeter;

import android.app.Application;

public class App extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = buildComponent();
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
