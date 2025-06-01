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
  private static final String LOGIN_CHANNEL = "login";

  private static final String SUCCESSFUL_REGISTRATION_EVENT = "SuccessfulRegistrationEvent";
  private static final String FAILED_REGISTRATION_EVENT = "FailedRegistrationEvent";
  private static final String SUCCESSFUL_EVENT_CREATION = "SuccessfulEventCreation";
  private static final String FAILURE_EVENT_CREATION = "FailureEventCreation";
  private static final String FOUND_EVENTS = "FoundEvents";

  private static void greetingsHandler(Object... args) {
    Log.d(TAG, "SocketIO server welcomes the client." + Arrays.toString(args));
  }

  private static void loginEventHandler(Object... args) {
    JSONObject response = (JSONObject) args[0];
    int code;
    try {
      code = response.getInt("code");
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    switch (code) {
      case 200:
        Log.d(TAG, "Successful login. " + response);
        SuccessfulLogin payload;
        try {
          payload = new SuccessfulLogin(response.getString("id"));
        } catch (JSONException e) {
          Log.e(TAG, "successfulLoginEventHandler error" + e.getLocalizedMessage(), e);
          throw new RuntimeException(e);
        }
        EventBus.getDefault().post(payload);
        break;
      case 400:
      case 401:
        Log.d(TAG, "Failed login. " + response);
          String message;
          try {
              message = response.getString("message");
          } catch (JSONException e) {
              throw new RuntimeException(e);
          }
          EventBus.getDefault().post(new FailureLogin(message));
        break;
      default:
        Log.d(TAG, "Unrecognized code from Login attempt: {}" + code);
        break;
    }
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
      String uri = initSocketIOPath(getBaseContext());
      Log.d(TAG, "Configuring SocketIO client for server: " + uri);
      socketClient = IO.socket(uri);
      socketClient.on(GREETINGS_CHANNEL, NetworkService::greetingsHandler);
      socketClient.on(LOGIN_CHANNEL, NetworkService::loginEventHandler);

      socketClient.on(FOUND_EVENTS, NetworkService::foundEventsEventHandler);
      socketClient.on(SUCCESSFUL_REGISTRATION_EVENT, NetworkService::successRegistrationEventHandler);
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
    socketClient.off(LOGIN_CHANNEL, NetworkService::loginEventHandler);

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
    Log.d(TAG, "onMessageEvent:LoginAttempt: "+ event.toString());
    JSONObject payload = null;
    try {
      payload = event.toJson();
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage(), e);
    }
    socketClient.emit(LOGIN_CHANNEL, payload);
  }

  @Subscribe
  public void onMessageEvent(SearchForEvents event) {
    Log.d(TAG, "onMessageEvent:SearchForEvents: "+ event.toString());
    try {
      socketClient.emit("FindEvents", event.toJson());
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  @Subscribe
  public void onMessageEvent(RegistrationAttempt event) {
    Log.d(TAG, "onMessageEvent:RegistrationAttempt:"+ event.toString());
    try {
      socketClient.emit("register", event.toJson());
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  @Subscribe
  public void onMessageEvent(CreateNewEventAttempt event) {
    Log.d(TAG, "onMessageEvent:CreateNewEventAttempt:"+ event.toString());
    try {
      socketClient.emit("createNewEvent", event.toJson());
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }
}
