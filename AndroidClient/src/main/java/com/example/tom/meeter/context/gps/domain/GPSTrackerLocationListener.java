package com.example.tom.meeter.context.gps.domain;

import android.location.Location;

/**
 * Created by Tom on 14.12.2016.
 */
public interface GPSTrackerLocationListener {
    void onGPSTrackerLocationChanged(Location newLocation);
}
