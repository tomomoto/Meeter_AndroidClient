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

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tom.meeter.R;
import com.tom.meeter.context.event.activity.EventActivity;
import com.tom.meeter.context.network.dto.EventDTO;

public class NotificationHelper {

    private static final String EVENTS_NOTIFY = "events_notify";
    private static final String ALL_EVENTS_GROUP = "com.tom.meeter.all_events_group";
    private static final int SUMMARY_ID = 9999;

    public static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "New events";
            String description = "Information about newly created events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(EVENTS_NOTIFY, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendNotificationEventCreated(Context ctx, EventDTO event) {
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
        mgr.notify(event.getId().hashCode(), getNotification(ctx, event));
        mgr.notify(SUMMARY_ID, getSummaryNotification(ctx));
    }

    private static Notification getNotification(Context ctx, EventDTO event) {
        return new NotificationCompat.Builder(ctx, EVENTS_NOTIFY)
              .setSmallIcon(R.drawable.ic_meeter_lr)
              .setContentTitle(ctx.getString(R.string.notification_new_event))
              //.setContentTitle(event.getCreatorId() + " published new event!")
              .setContentText(event.getName())
              .setStyle(getBigStyle(event.getDescription()))
              .setContentIntent(createPendingIntent(ctx, event))
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

    private static PendingIntent createPendingIntent(Context ctx, EventDTO event) {
        Intent intent = EventActivity.createEventActivityIntent(ctx, event.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(
              ctx, event.getId().hashCode(), intent,
              PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
