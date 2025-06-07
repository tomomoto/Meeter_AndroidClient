package com.tom.meeter.infrastructure.common;

import android.content.Context;

import java.io.IOException;
import java.util.Properties;

/**
 * Some well knows application constants.
 */
public class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Prevent initialization");
    }

    public static final String APP_PROPERTIES = "app.properties";

    public static final String SERVER_IP_PROPERTY = "server.ip";
    public static final String SERVER_PORT_PROPERTY = "server.port";
    public static final String SERVER_IO_PORT_PROPERTY = "server.io_port";

    public static final String LOCATION_DISTANCE_PROPERTY = "location.distance";
    public static final String LOCATION_TIME_PROPERTY = "location.time";
    public static final String MAP_EVENTS_AREA_PROPERTY = "map.events_area";
    public static final String MAP_TRACK_USER_PROPERTY = "map.track_user";

    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_VALUE_START = "Bearer ";
    public static final String TOKEN_KEY = "token";


    public static String initServerPath(Context context) throws IOException {
        Properties p = new Properties();
        p.load(context.getAssets().open(APP_PROPERTIES));
        return "http://"
              + p.getProperty(SERVER_IP_PROPERTY)
              + ":"
              + Integer.valueOf(p.getProperty(SERVER_PORT_PROPERTY));
    }

    public static String initSocketIOPath(Context context) throws IOException {
        Properties p = new Properties();
        p.load(context.getAssets().open(APP_PROPERTIES));
        return "ws://"
              + p.getProperty(SERVER_IP_PROPERTY)
              + ":"
              + Integer.valueOf(p.getProperty(SERVER_IO_PORT_PROPERTY));
    }

    public static String getAuthHeader(String token) {
        return AUTH_VALUE_START + token;
    }
}
