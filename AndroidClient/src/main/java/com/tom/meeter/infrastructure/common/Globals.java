package com.tom.meeter.infrastructure.common;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Properties;

/**
 * Some well knows application constants.
 */
public class Globals {

    private static final String TAG = Globals.class.getCanonicalName();

    private Globals() {
        throw new UnsupportedOperationException("Prevent initialization");
    }

    public static final String APP_PROPERTIES = "app.properties";

    public static final String SERVER_IP_PROPERTY = "server.ip";
    public static final String SERVER_PORT_PROPERTY = "server.port";
    public static final String SERVER_PROTO_PROPERTY = "server.proto";
    public static final String SERVER_IO_PORT_PROPERTY = "server.io_port";
    public static final String SERVER_IO_PROTO_PROPERTY = "server.io_proto";

    public static final String LOCATION_DISTANCE_PROPERTY = "location.distance";
    public static final String LOCATION_TIME_PROPERTY = "location.time";
    public static final String MAP_EVENTS_AREA_PROPERTY = "map.events_area";
    public static final String MAP_TRACK_USER_PROPERTY = "map.track_user";

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_FORMAT = "Bearer %s";
    public static final String TOKEN_KEY = "token";

    private static String serverPath;
    private static String socketIOPath;
    private static Boolean needTrackUserDefault;
    private static Integer searchAreaDefault;


    public static String getServerPath(Context ctx) {
        if (serverPath != null) {
            return serverPath;
        }
        Properties p = tryGetProps(ctx);
        serverPath = p.getProperty(SERVER_PROTO_PROPERTY) + "://"
              + p.getProperty(SERVER_IP_PROPERTY)
              + ":"
              + Integer.valueOf(p.getProperty(SERVER_PORT_PROPERTY));
        Log.d(TAG, "Server URL is [" + serverPath + "].");
        return serverPath;
    }

    public static String getSocketIOPath(Context ctx) {
        if (socketIOPath != null) {
            return socketIOPath;
        }
        Properties p = tryGetProps(ctx);
        socketIOPath = p.getProperty(SERVER_IO_PROTO_PROPERTY) + "://"
              + p.getProperty(SERVER_IP_PROPERTY)
              + ":"
              + Integer.valueOf(p.getProperty(SERVER_IO_PORT_PROPERTY));
        Log.d(TAG, "SocketIO path is [" + socketIOPath + "].");
        return socketIOPath;
    }

    public static String getAuthHeader(String token) {
        return String.format(BEARER_FORMAT, token);
    }

    public static boolean getDefaultTrackUser(Context ctx) {
        if (needTrackUserDefault != null) {
            return needTrackUserDefault;
        }
        needTrackUserDefault = Boolean.parseBoolean(
              tryGetProps(ctx).getProperty(MAP_TRACK_USER_PROPERTY));
        return needTrackUserDefault;
    }

    public static int getDefaultSearchArea(Context ctx) {
        if (searchAreaDefault != null) {
            return searchAreaDefault;
        }
        searchAreaDefault = Integer.parseInt(
              tryGetProps(ctx).getProperty(MAP_EVENTS_AREA_PROPERTY));
        return searchAreaDefault;
    }

    private static Properties tryGetProps(Context ctx) {
        Properties p = new Properties();
        try {
            p.load(ctx.getAssets().open(APP_PROPERTIES));
        } catch (IOException e) {
            Log.e(TAG, "tryGetProps failed with: ", e);
            throw new RuntimeException("Unable to init get properties: " + e);
        }
        return p;
    }
}
