package com.tom.meeter.context.network.service;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.peekToken;
import static com.tom.meeter.context.network.service.EventHandlers.eventsNotificationsChannel;
import static com.tom.meeter.context.network.service.EventHandlers.newSubscriberNotificationsChannel;
import static com.tom.meeter.context.network.service.NotificationHelper.buildForegroundNotification;
import static com.tom.meeter.context.network.service.NotificationHelper.createNotificationChannel;
import static com.tom.meeter.context.network.utils.Utils.readFlags;
import static com.tom.meeter.context.network.utils.Utils.setupOptions;
import static com.tom.meeter.infrastructure.common.Globals.getSocketIOPath;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static io.socket.client.Socket.EVENT_CONNECT;
import static io.socket.client.Socket.EVENT_CONNECT_ERROR;
import static io.socket.client.Socket.EVENT_DISCONNECT;

import android.accounts.AccountManager;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tom.meeter.context.network.domain.SearchForEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.EngineIOException;

public class SocketIOService extends Service {

    private static final String TAG = SocketIOService.class.getCanonicalName();

    public static final String STOP_CMD = "STOP";

    static final String GREETINGS_CHANNEL = "greetings";
    static final String EVENTS_SEARCH_CHANNEL = "events:search";
    static final String EVENTS_NOTIFICATIONS_CHANNEL = "events:notifications";
    static final String NEW_SUBSCRIBER_CHANNEL = "user:subscription:new";

    private static final String UNAUTHORIZED = "401";

    private AccountManager accountManager;
    private Socket socketClient;

    private String lastKnownAuthToken;
    private boolean initialized = false;

    public SocketIOService() {
        logMethod(TAG, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        logMethod(TAG, this, "intent: ", intent);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logMethod(TAG, this,
              "already started? " + initialized,
              "intent: " + intent, "flags: " + flags,
              "readFlags: " + readFlags(flags), " startId: " + startId);

        if (intent != null && STOP_CMD.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        lastKnownAuthToken = peekToken(accountManager);
        initializeSocketClient(false, lastKnownAuthToken);

        Notification notification = buildForegroundNotification(this);
        createNotificationChannel(this);
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        logMethod(TAG, this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logMethod(TAG, this);
        if (accountManager == null) {
            accountManager = AccountManager.get(getApplicationContext());
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        logMethod(TAG, this);
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        logMethod(TAG, this);
        super.onTaskRemoved(rootIntent);
    }

    private void initializeSocketClient(boolean forceInit, String authToken) {
        if (initialized && !forceInit) {
            Log.d(TAG, "SocketIOService is not going to initialize, " +
                  "since it is already initialized.");
            return;
        }
        String uri = getSocketIOPath(getApplicationContext());
        Log.d(TAG, "Configuring SocketIOClient for server: " + uri);
        socketClient = IO.socket(URI.create(uri), setupOptions(authToken));

        socketClient.on(EVENT_CONNECT,
              args -> {
                  Log.d(TAG, "SocketIOClient successfully connected to the server." + Arrays.toString(args));
              });
        socketClient.on(EVENT_DISCONNECT,
              args -> {
                  Log.d(TAG, "SocketIOClient disconnected from the server." + Arrays.toString(args));
              });
        socketClient.on(EVENT_CONNECT_ERROR,
              args -> {
                  Log.d(TAG, "SocketIOClient received connection error." + Arrays.toString(args));
                  Object arg = args[0];
                  if (arg instanceof EngineIOException engineIOException) {
                      Throwable cause = engineIOException.getCause();

                      if (cause instanceof IOException ioException) {

                          if (cause instanceof SocketTimeoutException ste) {
                              Log.i(TAG, "SocketIOService received SocketTimeoutException. Server is unavailable.");
                              return;
                          }
                          if (UNAUTHORIZED.equals(ioException.getMessage())) {
                              Log.i(TAG, "SocketIOService received an authorization error. " +
                                    "It is not possible to connect to the server with provided authorization. " +
                                    "Server is going to disconnect and not going to receive any " +
                                    "messages until recreateServer() is called.");
                              disconnect();
                              return;
                          }
                      }
                  }
              });

        socketClient.on(GREETINGS_CHANNEL, EventHandlers::greetingsHandler);
        socketClient.on(EVENTS_SEARCH_CHANNEL, EventHandlers::eventsSearchHandler);
        socketClient.on(EVENTS_NOTIFICATIONS_CHANNEL,
              args -> eventsNotificationsChannel(SocketIOService.this, args));
        socketClient.on(NEW_SUBSCRIBER_CHANNEL,
              args -> newSubscriberNotificationsChannel(SocketIOService.this, args));
        socketClient.connect();
        EventBus.getDefault().register(this);
        Log.d(TAG, "SocketIOClient is going to start. " +
              "connected? {" + socketClient.connected() + "}, " +
              "isActive? {" + socketClient.isActive() + "}.");
        socketClient.emit(GREETINGS_CHANNEL, "Client greetings.");
        initialized = true;
    }

    @Subscribe
    public void onMessageEvent(SearchForEvents event) {
        Log.d(TAG, "onMessageEvent: [" + EVENTS_SEARCH_CHANNEL + "] : " + event);
        socketClient.emit(EVENTS_SEARCH_CHANNEL, event.toJson());
    }

    @Override
    public void onDestroy() {
        logMethod(TAG, this);
        disconnect();
        super.onDestroy();
    }

    private void recreateServer() {
        disconnect();
        lastKnownAuthToken = peekToken(accountManager);
        initializeSocketClient(initialized, lastKnownAuthToken);
    }

    private void disconnect() {
        logMethod(TAG, this);
        EventBus.getDefault().unregister(this);
        socketClient.disconnect();
        socketClient.off();
        initialized = false;
    }
}
