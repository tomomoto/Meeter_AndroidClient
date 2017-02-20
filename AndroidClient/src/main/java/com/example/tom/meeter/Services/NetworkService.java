package com.example.tom.meeter.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.tom.meeter.NetworkEvents.EventsIncomeEvent;
import com.example.tom.meeter.NetworkEvents.FindNewEventsEvent;
import com.example.tom.meeter.NetworkEvents.LoggingEvent;
import com.example.tom.meeter.NetworkEvents.RightLoginEvent;
import com.example.tom.meeter.NetworkEvents.WrongLoginEvent;
import com.github.nkzawa.emitter.Emitter;
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

    private boolean started = false;
    private static final String TAG = "NetworkService";
    private Socket mSocketClient;

    public NetworkService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started) {
            EventBus.getDefault().register(this);
            try {
                mSocketClient = IO.socket("http://192.168.1.6:3000");
            } catch (URISyntaxException e) {
                Log.d(TAG, e.getMessage());
            }
            mSocketClient.on("RightLoginEvent", onRightLogin);
            mSocketClient.on("WrongLoginEvent", onWrongLogin);
            mSocketClient.on("FoundEvents", onFoundEvents);
            mSocketClient.connect();
            Log.d(TAG, "Service is going to start... Socket connected from service");
            started = true;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mSocketClient.disconnect();
        mSocketClient.off("RightLoginEvent", onRightLogin);
        mSocketClient.off("WrongLoginEvent", onWrongLogin);
        mSocketClient.off("FoundEvents", onFoundEvents);
        Log.d(TAG,"Disconnected from service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Emitter.Listener onWrongLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            EventBus.getDefault().post(new WrongLoginEvent());
            Log.d(TAG,"onWrongLogin From service");
        }
    };

    private Emitter.Listener onRightLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
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
    };

    private Emitter.Listener onFoundEvents = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Object arg = args[0];
            JSONArray jsonObject = (JSONArray) arg;
            EventBus.getDefault().post(new EventsIncomeEvent(jsonObject));
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoggingEvent event) {
        Log.d(TAG,"LoggingEvent EventCatcher");
        JSONObject jsonLoginData = new JSONObject();
        try {
            jsonLoginData.put("login", event.getUserLogin());
            jsonLoginData.put("password", event.getUserPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocketClient.emit("login", jsonLoginData);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FindNewEventsEvent event) {
        Log.d(TAG,"FindNewEventsEvent EventCatcher");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", event.getLatitude());
            jsonObject.put("longitude", event.getLongitude());
            jsonObject.put("area", event.getArea());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocketClient.emit("FindEvents", jsonObject);
    }
}
