package com.tom.meeter;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import com.tom.meeter.context.auth.AuthComponent;
import com.tom.meeter.context.auth.DaggerAuthComponent;
import com.tom.meeter.context.event.DaggerEventComponent;
import com.tom.meeter.context.event.EventComponent;
import com.tom.meeter.context.token.DaggerTokenComponent;
import com.tom.meeter.context.token.TokenComponent;

public class App extends Application {

    private static final String TAG = App.class.getCanonicalName();
    private AppComponent component;
    private AuthComponent authComponent;
    private TokenComponent tokenComponent;
    private EventComponent eventComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        logMethod(TAG, this);

        tokenComponent = buildTokenComponent();
        authComponent = buildAuthComponent();
        eventComponent = buildEventComponent();

        component = buildComponent();
    }

    @Override
    public void onTerminate() {
        logMethod(TAG, this);
        super.onTerminate();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
              .tokenComponent(tokenComponent)
              .authComponent(authComponent)
              .eventComponent(eventComponent)
              .application(this)
              .build();
    }

    protected AuthComponent buildAuthComponent() {
        return DaggerAuthComponent.builder()
              .application(this)
              .build();
    }

    protected TokenComponent buildTokenComponent() {
        return DaggerTokenComponent.builder()
              .application(this)
              .build();
    }

    protected EventComponent buildEventComponent() {
        return DaggerEventComponent.builder()
              .application(this)
              .build();
    }

    public AppComponent getComponent() {
        return component;
    }

    public AuthComponent getAuthComponent() {
        return authComponent;
    }

    public TokenComponent getTokenComponent() {
        return tokenComponent;
    }

    public EventComponent getEventComponent() {
        return eventComponent;
    }
}
