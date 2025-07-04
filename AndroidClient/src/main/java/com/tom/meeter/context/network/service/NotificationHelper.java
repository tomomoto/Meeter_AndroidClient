package com.tom.meeter.context.network.service;

import static com.tom.meeter.infrastructure.common.CommonHelper.getAppLogo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.tom.meeter.R;
import com.tom.meeter.context.launcher.Launcher;

public class NotificationHelper {

    private NotificationHelper() {
    }

    private static final String CHANNEL_ID = "socket_channel";

    static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                  CHANNEL_ID,
                  ctx.getString(R.string.network_channel),
                  NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = ctx.getSystemService(NotificationManager.class);
            if (manager == null) {
                return;
            }
            manager.createNotificationChannel(channel);
        }
    }

    static Notification buildForegroundNotification(Context ctx) {

        Intent notificationIntent = new Intent(ctx, Launcher.class);
        notificationIntent.setFlags(
              Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
              ctx,
              0,
              notificationIntent,
              PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(ctx, CHANNEL_ID)
              .setContentTitle(ctx.getString(R.string.app_name))
              .setContentText(ctx.getString(R.string.press_to_open_the_application))
              .setContentIntent(pendingIntent)
              .setSmallIcon(getAppLogo())
              .setOngoing(true)
              .build();
    }
}
