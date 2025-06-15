package com.tom.meeter.context.network.service;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.peekToken;
import static com.tom.meeter.infrastructure.common.GlobalConstants.AUTH_HEADER;
import static com.tom.meeter.infrastructure.common.GlobalConstants.getSocketIOPath;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static io.socket.client.Socket.EVENT_CONNECT;
import static io.socket.client.Socket.EVENT_CONNECT_ERROR;
import static io.socket.client.Socket.EVENT_DISCONNECT;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.tom.meeter.context.network.domain.CreateNewEventAttempt;
import com.tom.meeter.context.network.domain.SearchForEvents;
import com.tom.meeter.infrastructure.common.GlobalConstants;
import com.tom.meeter.infrastructure.common.JsonHelper;
import com.tom.meeter.infrastructure.eventbus.events.FailureEventCreation;
import com.tom.meeter.infrastructure.eventbus.events.IncomeEvents;
import com.tom.meeter.infrastructure.eventbus.events.SuccessfulEventCreation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.EngineIOException;

public class SocketIOService extends Service {

    private static final String TAG = SocketIOService.class.getCanonicalName();

    private static final String GREETINGS_CHANNEL = "greetings";
    private static final String EVENTS_CREATE_CHANNEL = "events:create";
    private static final String EVENTS_SEARCH_CHANNEL = "events:search";

    private static final String CODE_KEY = "code";
    private static final String ID_KEY = "id";
    private static final int CREATED_CODE = 201;
    private static final int BAD_REQUEST = 400;
    private static final String UNAUTHORIZED = "401";
    private AccountManager accountManager;

    public class ServiceBinder extends Binder {
        public SocketIOService getService() {
            return SocketIOService.this;
        }
    }

    private boolean initialized = false;
    private Socket socketClient;
    private ServiceBinder serviceBinder;

    private String lastKnownAuthToken;

    public SocketIOService() {
        logMethod(TAG, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (accountManager == null) {
            accountManager = AccountManager.get(getApplicationContext());
        }
        //AuthHelper.setToken(accountManager, Launcher.EXPIRED);
        Log.d(TAG, "SocketIOService onBind()" + " intent: " + intent);
        try {
            lastKnownAuthToken = peekToken(accountManager);
            initializeSocketClient(false, lastKnownAuthToken);
        } catch (IOException | URISyntaxException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return serviceBinder;
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
        serviceBinder = new ServiceBinder();
    }

/*    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SocketIOService onStartCommand(). already started? " + initialized
              + " intent: " + intent + " flags: " + flags
              + " readFlags: " + readFlags(flags) + " startId: " + startId);
        try {
            initializeSocketClient(false, "");
        } catch (IOException | URISyntaxException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return START_STICKY;
    }*/

    @Override
    public boolean onUnbind(Intent intent) {
        boolean ret = super.onUnbind(intent);
        logMethod(TAG, this);
        return ret;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        logMethod(TAG, this);
    }

    private void initializeSocketClient(
          boolean forceInit, String authToken) throws URISyntaxException, IOException {
        if (initialized && !forceInit) {
            Log.d(TAG, "SocketIOService is not going to initialize, since it is already initialized.");
            return;
        }
        String uri = getSocketIOPath(getBaseContext());
        Log.d(TAG, "Configuring SocketIOClient for server: " + uri);
        socketClient = IO.socket(uri, setupOptions(authToken));

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

                          if (cause instanceof SocketTimeoutException socketTimeoutException) {
                              Log.i(TAG, "SocketIOService received SocketTimeoutException. Server is unavailable.");
                              return;
                          }
                          if (UNAUTHORIZED.equals(ioException.getMessage())) {
                              Log.i(TAG, "SocketIOService received authorization error. " +
                                    "It is not possible to connect to the server with provided authorization. " +
                                    "Server is going to disconnect and not going to receive any " +
                                    "messages until recreateServer() is called.");
                              disconnect();
                              return;
                          }
                      }
                  }
              });

        socketClient.on(GREETINGS_CHANNEL, SocketIOService::greetingsHandler);
        socketClient.on(EVENTS_SEARCH_CHANNEL, SocketIOService::eventsSearchHandler);
        socketClient.on(EVENTS_CREATE_CHANNEL, SocketIOService::eventsCreateHandler);
        socketClient.connect();
        EventBus.getDefault().register(this);
        Log.d(TAG, "SocketIOClient is going to start... connected? {"
              + socketClient.connected() + "}, isActive? {" + socketClient.isActive() + "}.");
        socketClient.emit(GREETINGS_CHANNEL, "Client greetings.");
        initialized = true;
    }

    @Override
    public void onDestroy() {
        logMethod(TAG, this);
        disconnect();
        super.onDestroy();
    }

    public void recreateServer() {
        disconnect();
        lastKnownAuthToken = peekToken(accountManager);
        try {
            initializeSocketClient(initialized, lastKnownAuthToken);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void disconnect() {
        logMethod(TAG, this);
        EventBus.getDefault().unregister(this);
        socketClient.disconnect();
        socketClient.off(GREETINGS_CHANNEL, SocketIOService::greetingsHandler);
        socketClient.off(EVENTS_SEARCH_CHANNEL, SocketIOService::eventsSearchHandler);
        socketClient.off(EVENTS_CREATE_CHANNEL, SocketIOService::eventsCreateHandler);
        initialized = false;
    }

    @Subscribe
    public void onMessageEvent(SearchForEvents event) {
        Log.d(TAG, "onMessageEvent:SearchForEvents: " + event.toString());
        try {
            socketClient.emit(EVENTS_SEARCH_CHANNEL, event.toJson());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Subscribe
    public void onMessageEvent(CreateNewEventAttempt event) {
        Log.d(TAG, "onMessageEvent:CreateNewEventAttempt: " + event.toString());
        try {
            socketClient.emit(EVENTS_CREATE_CHANNEL, event.toJson());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private static String readFlags(int flags) {
        if ((flags & START_FLAG_REDELIVERY) == START_FLAG_REDELIVERY)
            return "START_FLAG_REDELIVERY";
        if ((flags & START_FLAG_RETRY) == START_FLAG_RETRY)
            return "START_FLAG_RETRY";
        if (flags == 0) {
            return "zero";
        }
        throw new RuntimeException("flag???" + flags);
    }

    private static IO.Options setupOptions(String authToken) {
        IO.Options result = new IO.Options();
        result.extraHeaders = setupAuthenticationHeader(authToken);
        return result;
    }

    private static Map<String, List<String>> setupAuthenticationHeader(String authToken) {
        Map<String, List<String>> result = new HashMap<>();
        result.put(AUTH_HEADER, Collections.singletonList(GlobalConstants.getAuthHeader(authToken)));
        return result;
    }

    private static void greetingsHandler(Object... args) {
        Log.d(TAG, "SocketIO server welcomes the client." + Arrays.toString(args));
    }

    private static void eventsSearchHandler(Object... args) {
        JSONArray response = getSimpleResponse(JSONArray.class, args);
        Log.d(TAG, EVENTS_SEARCH_CHANNEL + " : " + response);
        EventBus.getDefault().post(new IncomeEvents(response));
    }

    private static <T> T getSimpleResponse(Class<T> aClass, Object[] args) {
        if (!validateSingleMessageResponse(aClass, args)) {
            throw new RuntimeException("Incorrect response for " + aClass + " with response " + Arrays.toString(args));
        }
        return (T) args[0];
    }

    private static boolean validateSingleMessageResponse(Class<?> aClass, Object... args) {
        if (args.length != 1) {
            return false;
        }
        if (!aClass.isInstance(args[0])) {
            return false;
        }
        return true;
    }

    private static void eventsCreateHandler(Object... args) {
        JSONObject response = getSimpleResponse(JSONObject.class, args);
        int code = JsonHelper.getInt(response, CODE_KEY);
        Log.d(TAG, EVENTS_CREATE_CHANNEL + " : " + response);
        switch (code) {
            case CREATED_CODE:
                Log.d(TAG, "Event successfully created. " + response);
                EventBus.getDefault()
                      .post(new SuccessfulEventCreation(JsonHelper.getString(response, ID_KEY)));
                break;
            case BAD_REQUEST:
                Log.d(TAG, "Failed event creation. " + response);
                EventBus.getDefault().post(new FailureEventCreation());
                break;
            default:
                Log.d(TAG, "Unrecognized code from " + EVENTS_CREATE_CHANNEL + " [" + code + "]");
                break;
        }
    }
}

