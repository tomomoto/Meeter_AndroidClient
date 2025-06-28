package com.tom.meeter.context.notification;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tom.meeter.R;
import com.tom.meeter.context.event.activity.EventDispatcherActivity;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.user.activity.UserActivity;

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

    public static void sendNotificationEventCreated(
          Context ctx, UserDTO user, EventDTO event) {
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
        mgr.notify(event.getId().hashCode(), getNotificationEventCreated(ctx, user, event));
        notifySummary(ctx, mgr);
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

    private static Notification getNotificationEventCreated(
          Context ctx, UserDTO user, EventDTO event) {
        return new NotificationCompat.Builder(ctx, EVENTS_NOTIFY)
              .setSmallIcon(R.drawable.ic_meeter_lr)
              .setContentTitle(
                    ctx.getString(R.string.notification_new_event) + ": " + event.getName() + " !")
              .setContentText(
                    user.getName() + " " + user.getSurname() + " "
                          + ctx.getString(R.string.created_event))
              .setStyle(getBigStyle(event.getDescription()))
              .setContentIntent(createEventPendingIntent(ctx, event))
              .setGroup(ALL_EVENTS_GROUP)
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              .setAutoCancel(true)
              .build();
    }

    private static Notification getNotificationNewSubscriber(
          Context ctx, UserDTO user) {
        return new NotificationCompat.Builder(ctx, EVENTS_NOTIFY)
              .setSmallIcon(R.drawable.ic_meeter_lr)
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
              .setSmallIcon(R.drawable.ic_meeter_lr)
              .setGroup(ALL_EVENTS_GROUP)
              .setGroupSummary(true)
              .build();
    }

    private static NotificationCompat.BigTextStyle getBigStyle(String descr) {
        return new NotificationCompat.BigTextStyle().bigText(descr);
    }

    private static PendingIntent createEventPendingIntent(Context ctx, EventDTO event) {
        Intent intent = EventDispatcherActivity.createEventActivityIntent(ctx, event.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(
              ctx, event.getId().hashCode(), intent,
              PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent createUserPendingIntent(Context ctx, UserDTO user) {
        Intent intent = UserActivity.createUserActivityIntent(ctx, user.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(
              ctx, user.getId().hashCode(), intent,
              PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
