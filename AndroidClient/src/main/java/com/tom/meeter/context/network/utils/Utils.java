package com.tom.meeter.context.network.utils;

import static android.app.Service.START_FLAG_REDELIVERY;
import static android.app.Service.START_FLAG_RETRY;
import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import com.tom.meeter.context.network.exception.IncorrectResponseType;
import com.tom.meeter.infrastructure.common.Globals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;

public class Utils {
    private Utils() {
    }

    public static IO.Options setupOptions(String authToken) {
        IO.Options result = new IO.Options();
        result.extraHeaders = setupAuthHeader(authToken);
        return result;
    }

    static Map<String, List<String>> setupAuthHeader(String authToken) {
        Map<String, List<String>> result = new HashMap<>();
        result.put(
              AUTH_HEADER,
              Collections.singletonList(
                    Globals.getAuthHeader(authToken)));
        return result;
    }

    public static String readFlags(int flags) {
        if ((flags & START_FLAG_REDELIVERY) == START_FLAG_REDELIVERY)
            return "START_FLAG_REDELIVERY";
        if ((flags & START_FLAG_RETRY) == START_FLAG_RETRY)
            return "START_FLAG_RETRY";
        if (flags == 0) {
            return "zero";
        }
        throw new RuntimeException("flag???" + flags);
    }


    public static <T> T getSimpleResponse(
          Class<T> aClass, Object[] args) {
        if (!validateSingleMessageResponse(aClass, args)) {
            throw new IncorrectResponseType("Incorrect response for " + aClass
                  + " with response " + Arrays.toString(args));
        }
        return (T) args[0];
    }

    public static boolean validateSingleMessageResponse(
          Class<?> aClass, Object... args) {
        if (args.length != 1) {
            return false;
        }
        if (!aClass.isInstance(args[0])) {
            return false;
        }
        return true;
    }

}
