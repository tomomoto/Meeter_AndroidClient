package com.tom.meeter.infrastructure.utils;

public class Utils {
    private Utils() {
    }

    public static <T> T requireNonNull(T me, String message) {
        if (me == null) throw new IllegalStateException(message);
        return me;
    }

}
