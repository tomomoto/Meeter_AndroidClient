package com.tom.meeter;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import com.tom.meeter.context.auth.AuthComponent;
import com.tom.meeter.context.auth.DaggerAuthComponent;
import com.tom.meeter.context.event.DaggerEventComponent;
import com.tom.meeter.context.event.EventComponent;
import com.tom.meeter.context.image.DaggerImageComponent;
import com.tom.meeter.context.image.ImageComponent;
import com.tom.meeter.context.token.DaggerTokenComponent;
import com.tom.meeter.context.token.TokenComponent;
import com.tom.meeter.context.user.DaggerUserComponent;
import com.tom.meeter.context.user.UserComponent;

public class App extends Application {

    private static final String TAG = App.class.getCanonicalName();
    private AppComponent component;
    private AuthComponent authComponent;
    private TokenComponent tokenComponent;
    private ImageComponent imageComponent;
    private EventComponent eventComponent;
    private UserComponent userComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        logMethod(TAG, this);

        /* Independent */
        tokenComponent = buildTokenComponent();
        authComponent = buildAuthComponent();
        imageComponent = buildImageComponent();

        /* Dependent */
        eventComponent = buildEventComponent();
        userComponent = buildUserComponent();

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
              .imageComponent(imageComponent)
              .authComponent(authComponent)
              .eventComponent(eventComponent)
              .userComponent(userComponent)
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

    protected ImageComponent buildImageComponent() {
        return DaggerImageComponent.builder()
              .application(this)
              .build();
    }

    protected EventComponent buildEventComponent() {
        return DaggerEventComponent.builder()
              .application(this)
              .tokenComponent(tokenComponent)
              .imageComponent(imageComponent)
              .build();
    }

    protected UserComponent buildUserComponent() {
        return DaggerUserComponent.builder()
              .application(this)
              .tokenComponent(tokenComponent)
              .imageComponent(imageComponent)
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

    public ImageComponent getImageComponent() {
        return imageComponent;
    }

    public UserComponent getUserComponent() {
        return userComponent;
    }
}
