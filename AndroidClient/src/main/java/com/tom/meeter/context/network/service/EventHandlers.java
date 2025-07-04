package com.tom.meeter.context.network.service;

import static com.tom.meeter.context.network.service.SocketIOService.EVENTS_NOTIFICATIONS_CHANNEL;
import static com.tom.meeter.context.network.service.SocketIOService.EVENTS_SEARCH_CHANNEL;
import static com.tom.meeter.context.network.service.SocketIOService.NEW_SUBSCRIBER_CHANNEL;
import static com.tom.meeter.context.network.utils.SocketIOCodes.NEW_SUBSCRIBER_CODE;
import static com.tom.meeter.context.network.utils.Utils.getSimpleResponse;
import static com.tom.meeter.context.notification.NotificationHelper.sendEventDeletedNotification;
import static com.tom.meeter.context.notification.NotificationHelper.sendEventNotification;
import static com.tom.meeter.context.notification.NotificationHelper.sendNotificationNewSubscriber;

import android.content.Context;
import android.util.Log;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.network.exception.IncorrectResponseType;
import com.tom.meeter.context.network.utils.SocketIOEventCode;
import com.tom.meeter.infrastructure.eventbus.events.IncomeEvents;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class EventHandlers {

    private static final String TAG = EventHandlers.class.getCanonicalName();

    private static final String CODE_KEY = "code";
    private static final String MESSAGE_KEY = "message";
    private static final String USER_KEY = "user";
    private static final String EVENT_KEY = "event";
    private static final String EVENT_ID_KEY = "eventId";

    private EventHandlers() {
    }

    static void eventsNotificationsChannel(Context ctx, Object... args) {
        JSONObject response = getSimpleResponse(JSONObject.class, args);
        Log.d(TAG, EVENTS_NOTIFICATIONS_CHANNEL + " : " + response);
        try {
            int code = response.getInt(CODE_KEY);
            SocketIOEventCode eventNotifyCode = SocketIOEventCode.fromCode(code);
            if (eventNotifyCode == null) {
                Log.d(TAG, "Unrecognized event code: " + code);
                return;
            }
            JSONObject msg = response.getJSONObject(MESSAGE_KEY);
            UserDTO user = new UserDTO(msg.getJSONObject(USER_KEY));

            if (eventNotifyCode == SocketIOEventCode.DELETED) {
                sendEventDeletedNotification(ctx, user, msg.getString(EVENT_ID_KEY));
                return;
            }
            sendEventNotification(
                  ctx, user, eventNotifyCode,
                  new EventDTO(msg.getJSONObject(EVENT_KEY)));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    static void newSubscriberNotificationsChannel(Context ctx, Object... args) {
        JSONObject response = getSimpleResponse(JSONObject.class, args);
        Log.d(TAG, NEW_SUBSCRIBER_CHANNEL + " : " + response);
        try {
            if (response.getInt(CODE_KEY) == NEW_SUBSCRIBER_CODE) {
                sendNotificationNewSubscriber(
                      ctx,
                      new UserDTO(response.getJSONObject(MESSAGE_KEY)));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    static void greetingsHandler(Object... args) {
        Log.d(TAG, "SocketIO server welcomes the client. " + Arrays.toString(args));
    }

    static void eventsSearchHandler(Object... args) {
        try {
            JSONArray response = getSimpleResponse(JSONArray.class, args);
            Log.d(TAG, EVENTS_SEARCH_CHANNEL + " : " + response);
            EventBus.getDefault().post(IncomeEvents.fromJsonArray(response));
        } catch (IncorrectResponseType e) {
            JSONObject response = getSimpleResponse(JSONObject.class, args);
            Log.e(TAG, EVENTS_SEARCH_CHANNEL + " : " + response);
        }
    }
}
