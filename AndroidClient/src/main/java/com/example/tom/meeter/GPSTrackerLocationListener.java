package com.example.tom.meeter;

import android.location.Location;

/**
 * Created by Tom on 14.12.2016.
 */
public interface GPSTrackerLocationListener {
    public void onGPSTrackerLocationChanged(Location newLocation);
}
