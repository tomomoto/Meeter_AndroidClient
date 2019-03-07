package com.example.tom.meeter.infrastructure.common;

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
}
