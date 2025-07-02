package com.tom.meeter.context.network.utils;

import java.util.HashMap;
import java.util.Map;

public enum SocketIOEventCode {
    CREATED(100),
    PUBLISHED(101),
    UNPUBLISHED(102),
    DELETED(103),
    UPDATED(104),
    SCHEDULED(105),
    STARTED(106),
    PAUSED(107),
    RESUMED(108),
    FINISHED(109),
    CANCELLED(110),
    ARCHIVED(111);

    private final int code;

    SocketIOEventCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private static final Map<Integer, SocketIOEventCode> mapping = new HashMap<>();

    static {
        for (SocketIOEventCode ec : values()) {
            mapping.put(ec.code, ec);
        }
    }

    public static SocketIOEventCode fromCode(int code) {
        return mapping.get(code);
    }
}
