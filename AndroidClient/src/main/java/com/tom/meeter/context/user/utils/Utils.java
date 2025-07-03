package com.tom.meeter.context.user.utils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Utils {

    private static final String TAG = Utils.class.getCanonicalName();

    public static final String USER_ID_KEY = "user_id";

    private Utils() {
    }

    public static boolean isIncorrect(Activity activity) {
        Bundle extras = activity.getIntent().getExtras();
        if (extras == null) {
            Log.d(TAG, "Unable to create ["
                  + activity.getClass().getCanonicalName()
                  + "] without extras.");
            activity.finish();
            return true;
        }
        if (extras.getString(USER_ID_KEY) == null) {
            Log.d(TAG, "Unable to create user activity without ["
                  + USER_ID_KEY + "] provided.");
            activity.finish();
            return true;
        }
        return true;
    }

    public static String getUserId(Activity activity) {
        return activity.getIntent().getExtras().getString(USER_ID_KEY);
    }
}
