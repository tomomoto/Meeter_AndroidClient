package com.example.tom.meeter.context.network.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.tom.meeter.context.network.domain.CreateNewEventAttempt;
import com.example.tom.meeter.context.network.domain.FailureEventCreation;
import com.example.tom.meeter.context.network.domain.IncomeEvents;
import com.example.tom.meeter.context.network.domain.RegistrationAttempt;
import com.example.tom.meeter.context.network.domain.RegistrationFailed;
import com.example.tom.meeter.context.network.domain.RegistrationSuccess;
import com.example.tom.meeter.context.network.domain.SearchForEvents;
import com.example.tom.meeter.context.network.domain.LoginAttempt;
import com.example.tom.meeter.context.network.domain.SuccessfulEventCreation;
import com.example.tom.meeter.context.network.domain.SuccessfulLogin;
import com.example.tom.meeter.context.network.domain.FailureLogin;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.example.tom.meeter.infrastructure.common.Constants.initServerPath;

public class NetworkService extends Service {

  private static final String TAG = NetworkService.class.getCanonicalName();

  private static final String SUCCESSFUL_LOGIN_EVENT = "RightLoginEvent";
  private static final String UNSUCCESSFUL_LOGIN_EVENT = "WrongLoginEvent";
  private static final String SUCCESSFUL_REGISTRATION_EVENT = "SuccessfulRegistrationEvent";
  private static final String FAILED_REGISTRATION_EVENT = "FailedRegistrationEvent";
  private static final String SUCCESSFUL_EVENT_CREATION = "SuccessfulEventCreation";
  private static final String FAILURE_EVENT_CREATION = "FailureEventCreation";
  private static final String FOUND_EVENTS = "FoundEvents";

  private static void successfulLoginEventHandler(Object... args) {
    JSONObject ev = (JSONObject) args[0];
    Log.d(TAG, "successfulLoginEventHandler, " + ev.toString());
    SuccessfulLogin payload = null;
    try {
      payload = new SuccessfulLogin(ev.getString("id"));
    } catch (JSONException e) {
      Log.e(TAG, "successfulLoginEventHandler error" + e.getLocalizedMessage(), e);
    }
    EventBus.getDefault().post(payload);
  }

  private static void failureLoginEventHandler(Object... args) {
    Log.d(TAG, "failureLoginEventHandler From service");
    EventBus.getDefault().post(new FailureLogin());
  }

  private static void foundEventsEventHandler(Object... args) {
    JSONArray events = (JSONArray) args[0];
    Log.d(TAG, "foundEventsEventHandler events: " + events);
    EventBus.getDefault().post(new IncomeEvents(events));
  }

  private static void successRegistrationEventHandler(Object... args) {
    Log.d(TAG, "successRegistrationEventHandler From service");
    RegistrationSuccess payload = new RegistrationSuccess((String) args[0]);
    EventBus.getDefault().post(payload);
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
      socketClient = IO.socket(initServerPath(getBaseContext()));
      socketClient.on(SUCCESSFUL_LOGIN_EVENT, NetworkService::successfulLoginEventHandler);
      socketClient.on(UNSUCCESSFUL_LOGIN_EVENT, NetworkService::failureLoginEventHandler);
      socketClient.on(FOUND_EVENTS, NetworkService::foundEventsEventHandler);
      socketClient.on(SUCCESSFUL_REGISTRATION_EVENT, NetworkService::successRegistrationEventHandler);
      socketClient.on(FAILED_REGISTRATION_EVENT, NetworkService::failureRegistrationEventHandler);
      socketClient.on(SUCCESSFUL_EVENT_CREATION, NetworkService::successfulEventCreationHandler);
      socketClient.on(FAILURE_EVENT_CREATION, NetworkService::failureEventCreationHandler);
      socketClient.connect();
      EventBus.getDefault().register(this);
      Log.d(TAG, "Service is going to start... Socket connected from service");
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
    socketClient.off(SUCCESSFUL_REGISTRATION_EVENT, NetworkService::successRegistrationEventHandler);
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
    Log.d(TAG, event.toString());
    JSONObject payload = new JSONObject();
    try {
      payload.put("login", event.getLogin());
      payload.put("password", event.getPassword());
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage(), e);
    }
    socketClient.emit("login", payload);
  }

  @Subscribe
  public void onMessageEvent(SearchForEvents event) {
    Log.d(TAG, event.toString());
    try {
      socketClient.emit("FindEvents", event.toJson());
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  @Subscribe
  public void onMessageEvent(RegistrationAttempt event) {
    Log.d(TAG, event.toString());
    try {
      socketClient.emit("register", event.toJson());
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  @Subscribe
  public void onMessageEvent(CreateNewEventAttempt event) {
    Log.d(TAG, event.toString());
    try {
      socketClient.emit("createNewEvent", event.toJson());
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }
}
