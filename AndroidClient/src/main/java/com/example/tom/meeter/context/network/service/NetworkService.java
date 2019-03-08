package com.example.tom.meeter.context.network.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.tom.meeter.context.network.domain.IncomeEvents;
import com.example.tom.meeter.context.network.domain.SearchForEvents;
import com.example.tom.meeter.context.network.domain.LoginAttempt;
import com.example.tom.meeter.context.network.domain.SuccessfulLogin;
import com.example.tom.meeter.context.network.domain.FailureLogin;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import static com.example.tom.meeter.infrastructure.common.Constants.APP_PROPERTIES;
import static com.example.tom.meeter.infrastructure.common.Constants.SERVER_IP_PROPERTY;
import static com.example.tom.meeter.infrastructure.common.Constants.SERVER_PORT_PROPERTY;

public class NetworkService extends Service {

    private static final String NETWORK_SERVICE_TAG = NetworkService.class.getCanonicalName();

    private static final String SUCCESSFUL_LOGIN_EVENT = "RightLoginEvent";
    private static final String UNSUCCESSFUL_LOGIN_EVENT = "WrongLoginEvent";
    private static final String FOUND_EVENTS = "FoundEvents";

    private static void successfulLoginEventHandler(Object... args) {
        JSONObject ev = (JSONObject) args[0];
        SuccessfulLogin payload = null;
        try {
            payload = new SuccessfulLogin(
                    ev.getInt("user_id"),
                    ev.getString("name"),
                    ev.getString("surname"),
                    ev.getString("sex"),
                    ev.getString("info"),
                    ev.getString("birthday")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(NETWORK_SERVICE_TAG, "successfulLoginEventHandler, " + payload.toString());
        EventBus.getDefault().post(payload);
    }

    private static void failureLoginEventHandler(Object... args) {
        Log.d(NETWORK_SERVICE_TAG, "failureLoginEventHandler From service");
        EventBus.getDefault().post(new FailureLogin());
    }

    private static void foundEventsEventHandler(Object... args) {
        JSONArray events = (JSONArray) args[0];
        Log.d(NETWORK_SERVICE_TAG, "foundEventsEventHandler events: " + events);
        EventBus.getDefault().post(new IncomeEvents(events));
    }

    public class Binder extends android.os.Binder {
        public NetworkService getService() {
            return NetworkService.this;
        }
    }

    private String serverIp;
    private int serverPort;
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
            initServerPath();
            initSocketHandlers();
        } catch (IOException | URISyntaxException e) {
            Log.e(NETWORK_SERVICE_TAG, e.getMessage(), e);
        }
        return START_STICKY;
    }

    private void initServerPath() throws IOException {
        Properties p = new Properties();
        p.load(getBaseContext().getAssets().open(APP_PROPERTIES));
        serverIp = p.getProperty(SERVER_IP_PROPERTY);
        serverPort = Integer.valueOf(p.getProperty(SERVER_PORT_PROPERTY));
    }

    private void initSocketHandlers() throws URISyntaxException {
        if (!started) {
            socketClient = IO.socket("http://" + serverIp + ":" + serverPort);
            socketClient.on(SUCCESSFUL_LOGIN_EVENT, NetworkService::successfulLoginEventHandler);
            socketClient.on(UNSUCCESSFUL_LOGIN_EVENT, NetworkService::failureLoginEventHandler);
            socketClient.on(FOUND_EVENTS, NetworkService::foundEventsEventHandler);
            socketClient.connect();
            EventBus.getDefault().register(this);
            Log.d(NETWORK_SERVICE_TAG, "Service is going to start... Socket connected from service");
            started = true;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        socketClient.disconnect();
        socketClient.off(SUCCESSFUL_LOGIN_EVENT, NetworkService::successfulLoginEventHandler);
        socketClient.off(UNSUCCESSFUL_LOGIN_EVENT, NetworkService::failureLoginEventHandler);
        socketClient.off(FOUND_EVENTS, NetworkService::foundEventsEventHandler);
        Log.d(NETWORK_SERVICE_TAG, "Disconnected from service");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginAttempt event) {
        Log.d(NETWORK_SERVICE_TAG, event.toString());
        JSONObject payload = new JSONObject();
        try {
            payload.put("login", event.getLogin());
            payload.put("password", event.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socketClient.emit("login", payload);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchForEvents event) {
        Log.d(NETWORK_SERVICE_TAG, event.toString());
        try {
            socketClient.emit("FindEvents", event.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
