package com.tom.meeter;

import static com.tom.meeter.context.notification.NotificationHelper.createNotificationChannel;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import com.tom.meeter.context.auth.AuthComponent;
import com.tom.meeter.context.auth.DaggerAuthComponent;
import com.tom.meeter.context.event.DaggerEventComponent;
import com.tom.meeter.context.event.EventComponent;
import com.tom.meeter.context.profile.DaggerProfileComponent;
import com.tom.meeter.context.profile.ProfileComponent;
import com.tom.meeter.context.user.DaggerUserComponent;
import com.tom.meeter.context.user.UserComponent;

public class App extends Application {

    private static final String TAG = App.class.getCanonicalName();
    private AppComponent component;
    private AuthComponent authComponent;
    private EventComponent eventComponent;
    private UserComponent userComponent;
    private ProfileComponent profileComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        logMethod(TAG, this);

        component = buildComponent();

        /* Independent */
        authComponent = buildAuthComponent();

        /* Dependent */
        eventComponent = buildEventComponent();
        profileComponent = buildProfileComponent();
        userComponent = buildUserComponent();


        createNotificationChannel(this);
    }

    @Override
    public void onTerminate() {
        logMethod(TAG, this);
        super.onTerminate();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
              .application(this)
              .build();
    }

    protected AuthComponent buildAuthComponent() {
        return DaggerAuthComponent.builder()
              .application(this)
              .appComponent(component)
              .build();
    }

    protected EventComponent buildEventComponent() {
        return DaggerEventComponent.builder()
              .application(this)
              .appComponent(component)
              .build();
    }

    protected UserComponent buildUserComponent() {
        return DaggerUserComponent.builder()
              .application(this)
              .appComponent(component)
              .build();
    }

    protected ProfileComponent buildProfileComponent() {
        return DaggerProfileComponent.builder()
              .application(this)
              .appComponent(component)
              .build();
    }

    public AppComponent getComponent() {
        return component;
    }

    public AuthComponent getAuthComponent() {
        return authComponent;
    }

    public EventComponent getEventComponent() {
        return eventComponent;
    }

    public UserComponent getUserComponent() {
        return userComponent;
    }

    public ProfileComponent getProfileComponent() {
        return profileComponent;
    }
}
