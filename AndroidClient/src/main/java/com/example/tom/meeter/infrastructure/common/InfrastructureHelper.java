package com.example.tom.meeter.infrastructure.common;

import android.os.Bundle;
import android.util.Log;

public class InfrastructureHelper {

    private static final String METHOD_ENDING = "()";

    private InfrastructureHelper() {
    }

    public static Bundle createBundle(String key, String value) {
        Bundle result = new Bundle();
        result.putString(key, value);
        return result;
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
        2 - com.example.tom.meeter.infrastructure.common.InfrastructureHelper.getCurrentMethodName(InfrastructureHelper.java:31)
        3 - com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod(InfrastructureHelper.java:20)
        4 - target
        * */
        return Thread.currentThread().getStackTrace()[4].getMethodName() + METHOD_ENDING;
    }
}
