package com.example.tom.meeter.infrastructure.common;

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

    public static final String GPS_DISTANCE_PROPERTY = "gps.distance";
    public static final String GPS_TIME_PROPERTY = "gps.time";

    public static String initServerPath(Context context) throws IOException {
        Properties p = new Properties();
        p.load(context.getAssets().open(APP_PROPERTIES));
        return "http://" + p.getProperty(SERVER_IP_PROPERTY) + ":"
                + Integer.valueOf(p.getProperty(SERVER_PORT_PROPERTY));
    }
}
