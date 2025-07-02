package com.tom.meeter.context.event.activity;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ScheduleEventActivity extends AppCompatActivity {

    public static void dispatchToScheduleEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createScheduleEventActivityIntent(ctx, eventId));
    }

    public static Intent createScheduleEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, ScheduleEventActivity.class)
              .putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
    }
}
