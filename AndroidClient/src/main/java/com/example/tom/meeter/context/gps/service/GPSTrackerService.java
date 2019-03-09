package com.example.tom.meeter.context.gps.service;

/**
 * Created by Tom on 07.12.2016.
 */

import android.app.AlertDialog;
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

    private static final String TAG = GPSTrackerService.class.getCanonicalName();

    private final Context context;

    private long MIN_DISTANCE_CHANGE_FOR_UPDATES;
    private long MIN_TIME_BW_UPDATES;

    private boolean gpsEnabled;
    private boolean networkEnabled;
    private boolean canGetLocation;

    private double lastKnownLatitude;
    private double lastKnownLongitude;

    private LocationManager locManager;
    private Location location;

    private List<GPSTrackerLocationListener> listeners = new ArrayList<>();

    public GPSTrackerService(Context context) {
        this.context = context;
        initGPSParameters();
        this.locManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        tryRequestLocationUpdates();
        getLastKnownLocation();
    }

    private void tryRequestLocationUpdates() {
        if (locManager == null) {
            Log.w(TAG, "Location manager is null.");
            return;
        }

        gpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !networkEnabled) {
            Log.w(TAG, "GPS and Network are disabled.");
            return;
        }

        canGetLocation = true;

        if (networkEnabled) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

        if (gpsEnabled) {
            if (location == null) {
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        }
    }

    private void initGPSParameters() {
        Properties p = new Properties();
        try {
            p.load(context.getAssets().open(APP_PROPERTIES));
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        MIN_DISTANCE_CHANGE_FOR_UPDATES = Long.valueOf(p.getProperty(GPS_DISTANCE_PROPERTY));
        MIN_TIME_BW_UPDATES = Long.valueOf(p.getProperty(GPS_TIME_PROPERTY));
    }

    public Location getLastKnownLocation() {
        if (networkEnabled) {
            if (locManager != null) {
                location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    lastKnownLatitude = location.getLatitude();
                    lastKnownLongitude = location.getLongitude();
                }
            }
        }
        if (gpsEnabled) {
            if (locManager != null) {
                location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    lastKnownLatitude = location.getLatitude();
                    lastKnownLongitude = location.getLongitude();
                }
            }
        }
        return location;
    }


    public void stopUsingGPS() {
        if (locManager != null) {
            locManager.removeUpdates(GPSTrackerService.this);
        }
    }

    public double getLastKnownLatitude() {
        if (location != null) {
            lastKnownLatitude = location.getLatitude();
        }
        return lastKnownLatitude;
    }

    public double getLastKnownLongitude() {
        if (location != null) {
            lastKnownLongitude = location.getLongitude();
        }
        return lastKnownLongitude;
    }

    public boolean canGetLocation() {
        return canGetLocation;
    }

    public void addGPSTrackerListener(GPSTrackerLocationListener me) {
        listeners.add(me);
    }

    public void removeGPSTrackerListener(GPSTrackerLocationListener me) {
        listeners.remove(me);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
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
    public void onLocationChanged(Location loc) {
        getLastKnownLocation();
        for (GPSTrackerLocationListener l : listeners) {
            l.onGPSTrackerLocationChanged(loc);
        }
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
