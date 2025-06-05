package com.example.tom.meeter.context.gps.service;

/**
 * Created by Tom on 07.12.2016.
 */

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.tom.meeter.infrastructure.common.Constants.APP_PROPERTIES;
import static com.example.tom.meeter.infrastructure.common.Constants.LOCATION_DISTANCE_PROPERTY;
import static com.example.tom.meeter.infrastructure.common.Constants.LOCATION_TIME_PROPERTY;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.tom.meeter.context.gps.domain.LocationTrackerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

//TODO Rework service if permissions are granted after the service start.
// In case of no permissions after service start, location manager returns wrong results.
public class LocationTrackerService extends Service {

    private static final String TAG = LocationTrackerService.class.getCanonicalName();

    private long minDistanceMeters;
    private long minTimeMilliseconds;

    private LocationManager locManager = null;
    private LocationListener gpsListener = null;
    private LocationListener networkListener = null;
    private ServiceBinder binder;

    private final List<LocationTrackerListener> listeners = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        logMethod(TAG, this);
        binder = new ServiceBinder();
        locManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
        readLocationParameters();
        tryRequestLocationUpdates();
        getLastKnownLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logMethod(TAG, this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        logMethod(TAG, this);
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
        stopListeningForUpdates();
    }

    public LocationTrackerService() {
        logMethod(TAG, this);
    }

    public LocationTrackerService(Context context) {
    }

    private void tryRequestLocationUpdates() {
        if (locManager == null) {
            Log.w(TAG, "Location manager is null.");
            return;
        }
        if (ActivityCompat.checkSelfPermission(getBaseContext(), ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
              && ActivityCompat.checkSelfPermission(getBaseContext(), ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.w(TAG, ACCESS_FINE_LOCATION + " and " + ACCESS_COARSE_LOCATION + "are not set.");
            return;
        }

        boolean gpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean nwEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gpsEnabled && !nwEnabled) {
            Log.w(TAG, "GPS Provider and Network Provides are disabled.");
            return;
        }

        if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            networkListener = newLocationListener(this::onLocationUpdate, "NETWORK");
            locManager.requestLocationUpdates(
                  LocationManager.NETWORK_PROVIDER, minTimeMilliseconds,
                  minDistanceMeters, networkListener);
        }

        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsListener = newLocationListener(this::onLocationUpdate, "GPS");
            locManager.requestLocationUpdates(
                  LocationManager.GPS_PROVIDER, minTimeMilliseconds,
                  minDistanceMeters, gpsListener);
        }
    }

    private void readLocationParameters() {
        Properties p = new Properties();
        try {
            p.load(getBaseContext().getAssets().open(APP_PROPERTIES));
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        minDistanceMeters = Long.parseLong(p.getProperty(LOCATION_DISTANCE_PROPERTY));
        minTimeMilliseconds = Long.parseLong(p.getProperty(LOCATION_TIME_PROPERTY));
    }

    public Location getLastKnownLocation() {
        boolean fineLocGranted = ActivityCompat.checkSelfPermission(
              getBaseContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        boolean coarseLocGranted = ActivityCompat.checkSelfPermission(
              getBaseContext(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;
        if (!fineLocGranted && !coarseLocGranted) {
            return null;
        }
        Location gpsLKL = getLastKnownLocation(locManager, LocationManager.GPS_PROVIDER);
        if (gpsLKL != null) {
            return gpsLKL;
        }
        return getLastKnownLocation(locManager, LocationManager.NETWORK_PROVIDER);
    }

    private void stopListeningForUpdates() {
        if (locManager != null) {
            if (networkListener != null) {
                locManager.removeUpdates(networkListener);
                networkListener = null;
            }
            if (gpsListener != null) {
                locManager.removeUpdates(gpsListener);
                gpsListener = null;
            }
        }
    }

    public boolean canGetLocation() {
        boolean fineLocGranted = ActivityCompat.checkSelfPermission(
              getBaseContext(), ACCESS_FINE_LOCATION)
              == PERMISSION_GRANTED;
        boolean coarseLocGranted = ActivityCompat.checkSelfPermission(
              getBaseContext(), ACCESS_COARSE_LOCATION)
              == PERMISSION_GRANTED;
        if (!fineLocGranted && !coarseLocGranted) {
            return false;
        }
        return locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
              || locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void addLocationTrackerListener(LocationTrackerListener me) {
        listeners.add(me);
    }

    public void removeLocationTrackerListener(LocationTrackerListener me) {
        listeners.remove(me);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getBaseContext());
        dialog.setTitle("GPS is settings");
        dialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        dialog.setPositiveButton("Settings", (di, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            getBaseContext().startActivity(intent);
        });
        dialog.setNegativeButton("Cancel", (di, which) -> di.cancel());
        dialog.show();
    }

    private void onLocationUpdate(Location loc) {
        getLastKnownLocation();
        for (LocationTrackerListener l : listeners) {
            l.onLocationChanged(loc);
        }
    }

    public class ServiceBinder extends Binder {
        public LocationTrackerService getService() {
            return LocationTrackerService.this;
        }
    }

    private static LocationListener newLocationListener(Consumer<Location> locC, String logTag) {
        return location -> {
            Log.d(TAG, "Location update from [" + logTag + "]:" + location);
            locC.accept(location);
        };
    }

    private static Location getLastKnownLocation(LocationManager manager, String provider) {
        if (manager == null) {
            return null;
        }
        if (!manager.isProviderEnabled(provider)) {
            return null;
        }
        try {
            return manager.getLastKnownLocation(provider);
        } catch (SecurityException e) {
            Log.w(TAG, "getLastKnownLocation " + e);
            return null;
        }
    }
}
