package com.example.tom.meeter.context.network.service;

import static com.example.tom.meeter.infrastructure.common.Constants.initSocketIOPath;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.tom.meeter.context.network.domain.CreateNewEventAttempt;
import com.example.tom.meeter.context.network.domain.FailureEventCreation;
import com.example.tom.meeter.context.network.domain.FailureLogin;
import com.example.tom.meeter.context.network.domain.IncomeEvents;
import com.example.tom.meeter.context.network.domain.LoginAttempt;
import com.example.tom.meeter.context.network.domain.RegistrationAttempt;
import com.example.tom.meeter.context.network.domain.RegistrationFailed;
import com.example.tom.meeter.context.network.domain.RegistrationSuccess;
import com.example.tom.meeter.context.network.domain.SearchForEvents;
import com.example.tom.meeter.context.network.domain.SuccessfulEventCreation;
import com.example.tom.meeter.context.network.domain.SuccessfulLogin;
import com.example.tom.meeter.infrastructure.common.JsonHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;

public class NetworkService extends Service {

    private static final String TAG = NetworkService.class.getCanonicalName();

    private static final String GREETINGS_CHANNEL = "greetings";
    private static final String USER_LOGIN_CHANNEL = "user:login";
    private static final String EVENTS_SEARCH_CHANNEL = "events:search";

    private static final String SUCCESSFUL_REGISTRATION_EVENT = "SuccessfulRegistrationEvent";
    private static final String FAILED_REGISTRATION_EVENT = "FailedRegistrationEvent";

    private static final String SUCCESSFUL_EVENT_CREATION = "SuccessfulEventCreation";
    private static final String FAILURE_EVENT_CREATION = "FailureEventCreation";
    private static final String CODE_KEY = "code";
    private static final String ID_KEY = "id";
    private static final String MESSAGE_KEY = "message";

    private static void greetingsHandler(Object... args) {
        Log.d(TAG, "SocketIO server welcomes the client." + Arrays.toString(args));
    }

    private static void userLoginHandler(Object... args) {
        JSONObject response = getSimpleResponse(JSONObject.class, args);
        int code = JsonHelper.getInt(response, CODE_KEY);
        Log.d(TAG, USER_LOGIN_CHANNEL + " : " + response);
        switch (code) {
            case 200:
                Log.d(TAG, "Successful login. " + response);
                EventBus.getDefault()
                        .post(new SuccessfulLogin(JsonHelper.getString(response, ID_KEY)));
                break;
            case 400:
            case 401:
                Log.d(TAG, "Failed login. " + response);
                EventBus.getDefault()
                        .post(new FailureLogin(JsonHelper.getString(response, MESSAGE_KEY)));
                break;
            default:
                Log.d(TAG, "Unrecognized code from " + USER_LOGIN_CHANNEL + " [" + code + "]");
                break;
        }
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

    private static void userRegisterHandler(Object... args) {
        String userId = getSimpleResponse(String.class, args);
        Log.d(TAG, "successRegistrationEventHandler with " + userId);
        EventBus.getDefault().post(new RegistrationSuccess(userId));
    }

    private static void failureRegistrationEventHandler(Object... args) {
        Log.d(TAG, "failureRegistrationEventHandler From service");
        EventBus.getDefault().post(new RegistrationFailed());
    }

    private static void successfulEventCreationHandler(Object... args) {
        Log.d(TAG, "successfulEventCreationHandler From service");
        SuccessfulEventCreation payload = new SuccessfulEventCreation((String) args[0]);
        EventBus.getDefault().post(payload);
    }

    private static void failureEventCreationHandler(Object... args) {
        Log.d(TAG, "failureEventCreationHandler From service");
        EventBus.getDefault().post(new FailureEventCreation());
    }

    public class Binder extends android.os.Binder {
        public NetworkService getService() {
            return NetworkService.this;
        }
    }

    private boolean started = false;
    private Socket socketClient;
    private Binder binder;

    public NetworkService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            initSocketHandlers();
        } catch (IOException | URISyntaxException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return START_STICKY;
    }

    private void initSocketHandlers() throws URISyntaxException, IOException {
        if (!started) {
            String uri = initSocketIOPath(getBaseContext());
            Log.d(TAG, "Configuring SocketIO client for server: " + uri);
            socketClient = IO.socket(uri);
            socketClient.on(GREETINGS_CHANNEL, NetworkService::greetingsHandler);
            socketClient.on(USER_LOGIN_CHANNEL, NetworkService::userLoginHandler);
            socketClient.on(EVENTS_SEARCH_CHANNEL, NetworkService::eventsSearchHandler);

            socketClient.on(SUCCESSFUL_REGISTRATION_EVENT, NetworkService::userRegisterHandler);

            socketClient.on(FAILED_REGISTRATION_EVENT, NetworkService::failureRegistrationEventHandler);
            socketClient.on(SUCCESSFUL_EVENT_CREATION, NetworkService::successfulEventCreationHandler);
            socketClient.on(FAILURE_EVENT_CREATION, NetworkService::failureEventCreationHandler);
            socketClient.connect();
            EventBus.getDefault().register(this);
            Log.d(TAG, "SocketIO client is going to start...");
            Log.d(TAG, "SocketIO client: connected ?{"
                    + socketClient.connected() + "}. isActive? ?{" + socketClient.isActive() + "}.");
            socketClient.emit(GREETINGS_CHANNEL, "Client greetings.");
            started = true;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        socketClient.disconnect();
        socketClient.off(GREETINGS_CHANNEL, NetworkService::greetingsHandler);
        socketClient.off(USER_LOGIN_CHANNEL, NetworkService::userLoginHandler);
        socketClient.off(EVENTS_SEARCH_CHANNEL, NetworkService::eventsSearchHandler);

        socketClient.off(SUCCESSFUL_REGISTRATION_EVENT, NetworkService::userRegisterHandler);
        socketClient.off(FAILED_REGISTRATION_EVENT, NetworkService::failureRegistrationEventHandler);
        socketClient.off(SUCCESSFUL_EVENT_CREATION, NetworkService::successfulEventCreationHandler);
        socketClient.off(FAILURE_EVENT_CREATION, NetworkService::failureEventCreationHandler);
        Log.d(TAG, "Disconnected from service");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Subscribe
    public void onMessageEvent(LoginAttempt event) {
        Log.d(TAG, "onMessageEvent:LoginAttempt: " + event.toString());
        try {
            socketClient.emit(USER_LOGIN_CHANNEL, event.toJson());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
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
    public void onMessageEvent(RegistrationAttempt event) {
        Log.d(TAG, "onMessageEvent:RegistrationAttempt:" + event.toString());
        try {
            socketClient.emit("register", event.toJson());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Subscribe
    public void onMessageEvent(CreateNewEventAttempt event) {
        Log.d(TAG, "onMessageEvent:CreateNewEventAttempt:" + event.toString());
        try {
            socketClient.emit("createNewEvent", event.toJson());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
