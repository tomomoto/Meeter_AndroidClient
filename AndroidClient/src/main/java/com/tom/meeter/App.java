package com.tom.meeter;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import com.tom.meeter.context.auth.AuthComponent;
import com.tom.meeter.context.auth.DaggerAuthComponent;

public class App extends Application {

    private static final String TAG = App.class.getCanonicalName();
    private AppComponent component;
    private AuthComponent authComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        logMethod(TAG, this);

        authComponent = buildAuthComponent();

        component = buildComponent();
    }

    @Override
    public void onTerminate() {
        logMethod(TAG, this);
        super.onTerminate();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
              .authComponent(authComponent)
              .application(this)
              .build();
    }

    protected AuthComponent buildAuthComponent() {
        return DaggerAuthComponent.builder()
              .application(this)
              .build();
    }

    public AppComponent getComponent() {
        return component;
    }

    public AuthComponent getAuthComponent() {
        return authComponent;
    }
}
