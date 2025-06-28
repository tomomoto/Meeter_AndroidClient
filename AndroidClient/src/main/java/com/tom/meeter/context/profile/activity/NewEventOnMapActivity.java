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
    private Marker eventMarker;
    private GoogleMap gmap;

    private ServiceConnection locationServiceConn;
    private LocationTrackerService locationService;
    private boolean cameraMoved = false;

    private LocationTrackerListener singleLocationUpdateListener;
    private LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEventPositionBinding binding =
              ActivityEventPositionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

    private void setupSingleLocationListener() {
        singleLocationUpdateListener = new LocationTrackerListener() {
            @Override
            public void onLocationChanged(Location location) {
                logMethod(TAG, this);
                if (gmap != null && !cameraMoved) {
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM_VALUE));
                    cameraMoved = true;
                    locationService.removeLocationTrackerListener(this);
                    singleLocationUpdateListener = null;
                    unbindService(locationServiceConn);
                    locationService = null;
                    locationServiceConn = null;
                }
            }
        };
        locationServiceConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                logMethod(TAG, this);
                locationService = ((LocationTrackerService.ServiceBinder) binder).getService();
                locationService.addLocationTrackerListener(singleLocationUpdateListener);
            }

            public void onServiceDisconnected(ComponentName name) {
                logMethod(TAG, this);
                locationService = null;
                locationServiceConn = null;
            }
        };
        Intent service = new Intent(this, LocationTrackerService.class);
        bindService(service, locationServiceConn, BIND_AUTO_CREATE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        gmap.setOnMapClickListener(
              latLng -> {
                  if (eventMarker != null) {
                      eventMarker.setPosition(latLng);
                      return;
                  }
                  if (userLocation != null) {
                      eventMarker = gmap.addMarker(new MarkerOptions().position(latLng));
                  }
                  Log.d(TAG, "Event marker is null, user location" +
                        " is null, nothing to do...");
              });
        setupSingleLocationListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
        if (locationService != null && singleLocationUpdateListener != null) {
            locationService.removeLocationTrackerListener(singleLocationUpdateListener);
        }
        if (locationServiceConn != null) {
            unbindService(locationServiceConn);
        }
    }

    public static void dispatchToNewEventOnMapActivity(Context ctx) {
        ctx.startActivity(createNewEventOnMapActivityIntent(ctx));
    }

    public static Intent createNewEventOnMapActivityIntent(Context ctx) {
        return new Intent(ctx, NewEventOnMapActivity.class);
    }
}
