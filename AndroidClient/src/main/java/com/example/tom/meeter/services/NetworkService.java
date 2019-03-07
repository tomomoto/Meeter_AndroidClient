package com.example.tom.meeter.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.tom.meeter.network.EventsIncomeEvent;
import com.example.tom.meeter.network.FindNewEventsEvent;
import com.example.tom.meeter.network.LoggingEvent;
import com.example.tom.meeter.network.RightLoginEvent;
import com.example.tom.meeter.network.WrongLoginEvent;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class NetworkService extends Service {

    private static final String NETWORK_SERVICE_TAG = NetworkService.class.getCanonicalName();
    private static String URI = "http://10.137.57.156:3000";
    //private static String URI = "http://192.168.1.6:3000";

    private static void foundEventsEventHandler(Object... args) {
        Object arg = args[0];
        JSONArray jsonObject = (JSONArray) arg;
        EventBus.getDefault().post(new EventsIncomeEvent(jsonObject));
    }

    private static void failureLoginEventHandler(Object... args) {
        EventBus.getDefault().post(new WrongLoginEvent());
        Log.d(NETWORK_SERVICE_TAG, "failureLoginEventHandler From service");
    }

    private static void successfulLoginEventHandler(Object... args) {
        Object arg = args[0];
        JSONObject jsonObject = (JSONObject) arg;
        RightLoginEvent rightLoginEvent = null;
        try {
            rightLoginEvent = new RightLoginEvent(
                    jsonObject.getInt("user_id"),
                    jsonObject.getString("name"),
                    jsonObject.getString("surname"),
                    //jsonObject.getString("age"),
                    jsonObject.getString("sex"),
                    jsonObject.getString("info"),
                    jsonObject.getString("birthday")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(rightLoginEvent);
    }

    private boolean started = false;
    private Socket socketClient;

    public NetworkService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started) {
            EventBus.getDefault().register(this);
            try {
                socketClient = IO.socket(URI);
            } catch (URISyntaxException e) {
                Log.d(NETWORK_SERVICE_TAG, e.getMessage());
            }
            socketClient.on("RightLoginEvent", NetworkService::successfulLoginEventHandler);
            socketClient.on("WrongLoginEvent", NetworkService::failureLoginEventHandler);
            socketClient.on("FoundEvents", NetworkService::foundEventsEventHandler);
            socketClient.connect();
            Log.d(NETWORK_SERVICE_TAG, "Service is going to start... Socket connected from service");
            started = true;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        socketClient.disconnect();
        socketClient.off("RightLoginEvent", NetworkService::successfulLoginEventHandler);
        socketClient.off("WrongLoginEvent", NetworkService::failureLoginEventHandler);
        socketClient.off("FoundEvents", NetworkService::foundEventsEventHandler);
        Log.d(NETWORK_SERVICE_TAG, "Disconnected from service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoggingEvent event) {
        Log.d(NETWORK_SERVICE_TAG, "LoggingEvent EventCatcher");
        JSONObject jsonLoginData = new JSONObject();
        try {
            jsonLoginData.put("login", event.getUserLogin());
            jsonLoginData.put("password", event.getUserPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socketClient.emit("login", jsonLoginData);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FindNewEventsEvent event) {
        Log.d(NETWORK_SERVICE_TAG, "FindNewEventsEvent EventCatcher");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", event.getLatitude());
            jsonObject.put("longitude", event.getLongitude());
            jsonObject.put("area", event.getArea());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socketClient.emit("FindEvents", jsonObject);
    }
}
