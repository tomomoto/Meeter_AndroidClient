package com.tom.meeter.infrastructure.common;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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

    public static void logMethod(String tag, Object obj) {
        if (obj.getClass().isAnonymousClass()) {
            Log.d(tag, obj.getClass().getName() + " " + getCurrentMethodName());
        } else {
            Log.d(tag, obj.getClass().getSimpleName() + " " + getCurrentMethodName());
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
}
