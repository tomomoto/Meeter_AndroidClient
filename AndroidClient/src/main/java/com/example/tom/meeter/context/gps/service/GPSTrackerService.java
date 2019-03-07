package com.example.tom.meeter.context.gps.service;

/**
 * Created by Tom on 07.12.2016.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.example.tom.meeter.context.gps.domain.GPSTrackerLocationListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.example.tom.meeter.infrastructure.common.Constants.APP_PROPERTIES;
import static com.example.tom.meeter.infrastructure.common.Constants.GPS_DISTANCE_PROPERTY;
import static com.example.tom.meeter.infrastructure.common.Constants.GPS_TIME_PROPERTY;


public class GPSTrackerService extends Service implements LocationListener {

    private static final String GPS_TRACKER_TAG = GPSTrackerService.class.getCanonicalName();

    private long minDistanceChangeForUpdates;
    private long minTimeBwUpdates;

    private List<GPSTrackerLocationListener> listeners = new ArrayList<>();

    public void addGPSTrackerListener(GPSTrackerLocationListener toAdd) {
        listeners.add(toAdd);
    }

    public void removeGPSTrackerListener(GPSTrackerLocationListener toDelete) {
        listeners.remove(toDelete);
    }

    private final Context context;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    private Location location;

    private double latitude;
    private double longitude;

    protected LocationManager lm;

    private void initParameters() throws IOException {
        Properties p = new Properties();
        p.load(getBaseContext().getAssets().open(APP_PROPERTIES));
        minDistanceChangeForUpdates = Long.valueOf(p.getProperty(GPS_DISTANCE_PROPERTY));
        minTimeBwUpdates = Long.valueOf(p.getProperty(GPS_TIME_PROPERTY));
    }

    public GPSTrackerService(Context context) {
        this.context = context;
        try {
            initParameters();
            lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.d(GPS_TRACKER_TAG, "GPS Enabled: " + isGPSEnabled);
            isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d(GPS_TRACKER_TAG, "Network Enabled: " + isNetworkEnabled);

            if (isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    lm.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            minTimeBwUpdates,
                            minDistanceChangeForUpdates, this);
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        lm.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                minTimeBwUpdates,
                                minDistanceChangeForUpdates, this);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        getLocation();
    }

    public Location getLocation() {
        try {
            if (isNetworkEnabled) {
                if (lm != null) {
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            if (isGPSEnabled) {
                if (lm != null) {
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }


    public void stopUsingGPS() {
        if (lm != null) {
            try {
                lm.removeUpdates(GPSTrackerService.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location arg0) {
        getLocation();
        for (GPSTrackerLocationListener li : listeners)
            li.onGPSTrackerLocationChanged(arg0);
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}