package com.tom.meeter.context.notification;

import static com.tom.meeter.infrastructure.common.CommonHelper.getSmallAppLogo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tom.meeter.R;
import com.tom.meeter.context.event.activity.EventDispatcherActivity;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.network.utils.SocketIOEventCode;
import com.tom.meeter.context.user.activity.UserActivity;

import java.util.function.Supplier;

public class NotificationHelper {

    private static final String EVENTS_NOTIFY = "events_notify";
    private static final String ALL_EVENTS_GROUP = "com.tom.meeter.all_events_group";
    private static final int SUMMARY_ID = 9999;

    public static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                  EVENTS_NOTIFY,
                  ctx.getString(R.string.new_events_channel),
                  NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(
                  ctx.getString(R.string.information_about_newly_created_events));

            NotificationManager notificationManager = ctx.getSystemService(
                  NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendEventNotification(
          Context ctx, UserDTO user,
          SocketIOEventCode eventCode, EventDTO event) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
              != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat mgr = NotificationManagerCompat.from(ctx);
        mgr.notify(event.getId().hashCode(), getNotification(ctx, user, event, eventCode));
        notifySummary(ctx, mgr);
    }

    public static void sendEventDeletedNotification(
          Context ctx, UserDTO user, String eventId) {
        // As nothing to notify...

/*        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
              != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat mgr = NotificationManagerCompat.from(ctx);
        mgr.notify(
              eventId.hashCode(),
              getEventNotification(
                    ctx, contentTitle, contentText, bigStyleText, eventId));
        notifySummary(ctx, mgr);*/
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static void notifySummary(Context ctx, NotificationManagerCompat mgr) {
        mgr.notify(SUMMARY_ID, getSummaryNotification(ctx));
    }

    public static void sendNotificationNewSubscriber(Context ctx, UserDTO user) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat mgr = NotificationManagerCompat.from(ctx);
        mgr.notify(user.getId().hashCode(), getNotificationNewSubscriber(ctx, user));
        notifySummary(ctx, mgr);
    }

    private static Notification getNotification(
          Context ctx, UserDTO user, EventDTO event, SocketIOEventCode eventCode) {
        Supplier<String> contentTitleS = null;
        Supplier<String> contentTextS = null;
        Supplier<String> bigStyleTextS = event::getDescription;
        if (eventCode == SocketIOEventCode.CREATED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.notification_new_event);
            contentTextS = () -> user.getName() + " " + user.getSurname() + " " + ctx.getString(R.string.created_event);
        } else if (eventCode == SocketIOEventCode.UPDATED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_updated);
            contentTextS = () -> getContentText(ctx, user, R.string.is_updated);
        } else if (eventCode == SocketIOEventCode.PUBLISHED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_published);
            contentTextS = () -> getContentText(ctx, user, R.string.is_published);
        } else if (eventCode == SocketIOEventCode.UNPUBLISHED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_unpublished);
            contentTextS = () -> getContentText(ctx, user, R.string.is_unpublished);
        } else if (eventCode == SocketIOEventCode.SCHEDULED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_scheduled);
            contentTextS = () -> getContentText(ctx, user, R.string.is_scheduled);
        } else if (eventCode == SocketIOEventCode.STARTED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_started);
            contentTextS = () -> getContentText(ctx, user, R.string.event_is_started);
        } else if (eventCode == SocketIOEventCode.PAUSED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_paused);
            contentTextS = () -> getContentText(ctx, user, R.string.event_is_paused);
        } else if (eventCode == SocketIOEventCode.RESUMED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_resumed);
            contentTextS = () -> getContentText(ctx, user, R.string.event_is_resumed);
        } else if (eventCode == SocketIOEventCode.FINISHED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_finished);
            contentTextS = () -> getContentText(ctx, user, R.string.event_is_finished);
        } else if (eventCode == SocketIOEventCode.CANCELLED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_cancelled);
            contentTextS = () -> getContentText(ctx, user, R.string.event_is_cancelled);
        } else if (eventCode == SocketIOEventCode.ARCHIVED) {
            contentTitleS = () -> getContentTitle(ctx, event.getName(), R.string.event_archived);
            contentTextS = () -> getContentText(ctx, user, R.string.event_is_archived);
        }

        if (contentTitleS == null) {
            throw new IllegalStateException("Unrecognized event notification:" + eventCode);
        }
        return getEventNotification(ctx, contentTitleS, contentTextS, bigStyleTextS, event.getId());
    }

    @NonNull
    private static String getContentText(Context ctx, UserDTO user, int newStatusResId) {
        return ctx.getString(R.string.created_by_user) + " " + user.getName() + " " + user.getSurname()
              + " " + ctx.getString(newStatusResId);
    }

    @NonNull
    private static String getContentTitle(Context ctx, String eventName, int resId) {
        return ctx.getString(resId) + ": " + eventName + " !";
    }

    private static Notification getEventNotification(
          Context ctx, String title, String text,
          String bigStyleText, String eventId) {
        return new NotificationCompat.Builder(ctx, EVENTS_NOTIFY)
              .setSmallIcon(getSmallAppLogo())
              .setContentTitle(title)
              .setContentText(text)
              .setStyle(getBigStyle(bigStyleText))
              .setContentIntent(createEventPendingIntent(ctx, eventId))
              .setGroup(ALL_EVENTS_GROUP)
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              .setAutoCancel(true)
              .build();
    }

    private static Notification getEventNotification(
          Context ctx, Supplier<String> titleS, Supplier<String> textS,
          Supplier<String> bigStyleTextS, String eventId) {
        return getEventNotification(
              ctx, titleS.get(), textS.get(),
              bigStyleTextS.get(), eventId);
    }

    private static Notification getNotificationNewSubscriber(
          Context ctx, UserDTO user) {
        return new NotificationCompat.Builder(ctx, EVENTS_NOTIFY)
              .setSmallIcon(getSmallAppLogo())
              .setContentTitle(ctx.getString(R.string.new_subscriber))
              .setContentText(
                    user.getName() + " " + user.getSurname() + " "
                          + ctx.getString(R.string.subscribed))
              .setStyle(getBigStyle(
                    user.getName() + " "
                          + user.getSurname() + ". "
                          + user.getInfo()))
              .setContentIntent(createUserPendingIntent(ctx, user))
              .setGroup(ALL_EVENTS_GROUP)
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              .setAutoCancel(true)
              .build();
    }

    private static Notification getSummaryNotification(Context ctx) {
        return new NotificationCompat.Builder(ctx, EVENTS_NOTIFY)
              .setSmallIcon(getSmallAppLogo())
              .setGroup(ALL_EVENTS_GROUP)
              .setGroupSummary(true)
              .build();
    }

    private static NotificationCompat.BigTextStyle getBigStyle(String descr) {
        return new NotificationCompat.BigTextStyle().bigText(descr);
    }

    private static PendingIntent createEventPendingIntent(
          Context ctx, String eventId) {
        Intent intent = EventDispatcherActivity.createEventActivityIntent(
              ctx, eventId);
        intent.setFlags(
              Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(
              ctx, eventId.hashCode(), intent,
              PendingIntent.FLAG_IMMUTABLE
                    | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent createUserPendingIntent(Context ctx, UserDTO user) {
        Intent intent = UserActivity.createUserActivityIntent(ctx, user.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(
              ctx, user.getId().hashCode(), intent,
              PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
