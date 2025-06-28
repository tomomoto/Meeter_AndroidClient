package com.tom.meeter.infrastructure.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Arrays;

public class InfrastructureHelper {

    private static final String METHOD_ENDING = "()";

    private InfrastructureHelper() {
    }

    public static void showMessage(Activity activity, String msg) {
        if (TextUtils.isEmpty(msg))
            return;

        activity.runOnUiThread(
              () -> Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    public static void showMessage(Activity activity, int resId) {
        activity.runOnUiThread(
              () -> Toast.makeText(activity.getApplicationContext(), resId, Toast.LENGTH_SHORT).show());
    }

    public static void showMessage(Context ctx, String msg) {
        if (TextUtils.isEmpty(msg))
            return;
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(Context ctx, int resId) {
        Toast.makeText(ctx, resId, Toast.LENGTH_SHORT).show();
    }

    public static void logMethod(String tag, Object obj) {
        if (obj.getClass().isAnonymousClass()) {
            Log.d(tag, obj.getClass().getName() + " " + getCurrentMethodName());
        } else {
            Log.d(tag, obj.getClass().getSimpleName() + " " + getCurrentMethodName());
        }
    }

    public static void logMethod(String tag, Object obj, String message) {
        if (obj.getClass().isAnonymousClass()) {
            Log.d(tag, obj.getClass().getName() + " " + getCurrentMethodName()
                  + ". Message: " + message);
        } else {
            Log.d(tag, obj.getClass().getSimpleName() + " " + getCurrentMethodName()
                  + ". Message: " + message);
        }
    }

    public static void logMethod(String tag, Object obj, Object... args) {
        if (obj.getClass().isAnonymousClass()) {
            Log.d(tag, obj.getClass().getName() + " " + getCurrentMethodName()
                  + " with args: " + Arrays.toString(args));
        } else {
            Log.d(tag, obj.getClass().getSimpleName() + " " + getCurrentMethodName()
                  + " with args: " + Arrays.toString(args));
        }
    }

    public static void logMethod(String tag, String name) {
        Log.d(tag, name + " " + getCurrentMethodName());
    }

    private static String getCurrentMethodName() {
        /*
        0 - dalvik.system.VMStack.getThreadStackTrace(Native Method)
        1 - java.lang.Thread.getStackTrace(Thread.java:1841)
        2 - com.tom.meeter.infrastructure.common.InfrastructureHelper.getCurrentMethodName(InfrastructureHelper.java:31)
        3 - com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod(InfrastructureHelper.java:20)
        4 - target
        * */
        return Thread.currentThread().getStackTrace()[4].getMethodName() + METHOD_ENDING;
    }

    public static void recreateActivityFromFragment(Fragment me) {
        new Handler(Looper.getMainLooper()).post(
              () -> {
                  FragmentActivity activity = me.getActivity();
                  activity.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(me)
                        .commit();
                  activity.recreate();
              });
    }

    public static void restartActivityFromFragment(Fragment me) {
        new Handler(Looper.getMainLooper()).post(
              () -> {
                  FragmentActivity activity = me.getActivity();
                  Intent intent = activity.getIntent();
                  intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                              | Intent.FLAG_ACTIVITY_NEW_TASK
                              | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                  activity.overridePendingTransition(0, 0);
                  activity.finish();

                  activity.overridePendingTransition(0, 0);
                  me.startActivity(intent);
              });
    }
}
