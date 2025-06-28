package com.tom.meeter.context.profile.activity;

import static com.tom.meeter.context.profile.fragment.GoogleMapsFragment.ZOOM_VALUE;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tom.meeter.R;
import com.tom.meeter.context.event.activity.EventLocationMapActivity;
import com.tom.meeter.context.gps.domain.LocationTrackerListener;
import com.tom.meeter.context.gps.service.LocationTrackerService;
import com.tom.meeter.databinding.ActivityEventPositionBinding;

public class NewEventOnMapActivity extends AppCompatActivity
      implements OnMapReadyCallback {

    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LNG = "extra_lng";

    private static final String TAG = EventLocationMapActivity.class.getCanonicalName();

    private ServiceConnection sConn;
    private LocationTrackerService locationService;
    private LocationTrackerListener singleLocationUpdateListener;
    private GoogleMap gmap;
    private Marker eventMarker;
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEventPositionBinding binding =
              ActivityEventPositionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindLocationService();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.eventSelectPosition);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        binding.btnConfirm.setOnClickListener(v -> {
            if (eventMarker != null) {
                Intent resultIntent = new Intent();
                LatLng position = eventMarker.getPosition();
                resultIntent.putExtra(EXTRA_LAT, position.latitude);
                resultIntent.putExtra(EXTRA_LNG, position.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, R.string.select_point_on_the_map, Toast.LENGTH_SHORT)
                      .show();
            }
        });
    }

    private void bindLocationService() {
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                logMethod(TAG, this);
                locationService = ((LocationTrackerService.ServiceBinder) binder).getService();
                lastKnownLocation = locationService.getLastKnownLocation();
                if (lastKnownLocation != null) {
                    return;
                }
                singleLocationUpdateListener = new LocationTrackerListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        logMethod(TAG, this);
                        if (gmap == null) {
                            Log.d(TAG, "Location update ignored...");
                        }
                        gmap.moveCamera(
                              CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(), location.getLongitude()),
                                    ZOOM_VALUE));
                        locationService.removeLocationTrackerListener(this);
                        singleLocationUpdateListener = null;
                        unbindService(sConn);
                        locationService = null;
                        sConn = null;
                    }
                };
                locationService.addLocationTrackerListener(singleLocationUpdateListener);
            }

            public void onServiceDisconnected(ComponentName name) {
                logMethod(TAG, this);
                locationService = null;
                sConn = null;
            }
        };

        bindService(
              new Intent(this, LocationTrackerService.class),
              sConn, BIND_AUTO_CREATE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        /*
        gmap.setOnMapLoadedCallback(
        () -> {
            Log.d("Map", "onMapLoaded — карта полностью отрисована");
        });
        */
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        if (lastKnownLocation != null) {
            gmap.moveCamera(
                  CameraUpdateFactory.newLatLngZoom(
                        new LatLng(
                              lastKnownLocation.getLatitude(),
                              lastKnownLocation.getLongitude()),
                        ZOOM_VALUE));
        }
        gmap.setOnMapClickListener(
              latLng -> {
                  if (eventMarker == null) {
                      eventMarker = gmap.addMarker(new MarkerOptions().position(latLng));
                  } else {
                      eventMarker.setPosition(latLng);
                  }
              });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
        if (locationService != null && singleLocationUpdateListener != null) {
            locationService.removeLocationTrackerListener(singleLocationUpdateListener);
        }
        if (sConn != null) {
            unbindService(sConn);
        }
    }

    public static void dispatchToNewEventOnMapActivity(Context ctx) {
        ctx.startActivity(createNewEventOnMapActivityIntent(ctx));
    }

    public static Intent createNewEventOnMapActivityIntent(Context ctx) {
        return new Intent(ctx, NewEventOnMapActivity.class);
    }
}
