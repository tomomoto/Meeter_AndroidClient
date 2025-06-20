package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.profile.fragment.GoogleMapsFragment.ZOOM_VALUE;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.FrameLayout;
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
import com.tom.meeter.context.gps.domain.LocationTrackerListener;
import com.tom.meeter.context.gps.service.LocationTrackerService;
import com.tom.meeter.databinding.ActivityEventPositionBinding;

public class EventLocationMapActivity extends AppCompatActivity
      implements OnMapReadyCallback {

    private static final String TAG = EventLocationMapActivity.class.getCanonicalName();
    private static final String LONGITUDE_KEY = "longitude";
    private static final String LATITUDE_KEY = "latitude";
    private Marker selectedMarker;
    private LatLng selectedLatLng = null;
    private GoogleMap gmap;
    private ActivityEventPositionBinding binding;

    private ServiceConnection locationServiceConn;
    private LocationTrackerService locationService;
    private boolean cameraMoved = false;

    private LocationTrackerListener singleLocationUpdateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Object latitudeObj = null;
        Object longitudeObj = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitudeObj = extras.get(LATITUDE_KEY);
            longitudeObj = extras.get(LONGITUDE_KEY);
        }

        binding = ActivityEventPositionBinding.inflate(getLayoutInflater());
        FrameLayout view = binding.getRoot();
        setContentView(view);

        if (latitudeObj instanceof Double latitude
              && longitudeObj instanceof Double longitude) {
            selectedLatLng = new LatLng(latitude, longitude);
        } else {
            singleLocationUpdateListener = new LocationTrackerListener() {
                @Override
                public void onLocationChanged(Location location) {
                    logMethod(TAG, this);
                    if (gmap != null && !cameraMoved) {
                        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                              new LatLng(location.getLatitude(), location.getLongitude()),
                              ZOOM_VALUE), 6000, null);
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        binding.btnConfirm.setOnClickListener(v -> {
            if (selectedLatLng != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EventActivity.EXTRA_LAT, selectedLatLng.latitude);
                resultIntent.putExtra(EventActivity.EXTRA_LNG, selectedLatLng.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Выберите точку на карте", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        if (selectedLatLng != null) {
            selectedMarker = gmap.addMarker(
                  new MarkerOptions()
                        .position(selectedLatLng)
                        .title("Выбранная позиция"));
            gmap.animateCamera(
                  CameraUpdateFactory.newLatLngZoom(
                        selectedLatLng, ZOOM_VALUE), 6000, null);
        }
        gmap.setOnMapClickListener(
              latLng -> {
                  if (selectedMarker == null) {
                      selectedMarker = gmap.addMarker(
                            new MarkerOptions()
                                  .position(latLng)
                                  .title("Выбранная позиция"));
                  } else {
                      selectedMarker.setPosition(latLng);
                  }
                  selectedLatLng = latLng;


/*                  if (selectedMarker != null) {
                      selectedMarker.remove();
                  }
                  selectedMarker = gmap.addMarker(
                        new MarkerOptions()
                              .position(latLng)
                              .title("Выбранная позиция"));
                  selectedLatLng = latLng;*/
              });
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

    public static void dispatchToEventLocationMapActivity(
          Context ctx, Double latitude, Double longitude) {
        ctx.startActivity(createEventLocationMapActivityIntent(ctx, latitude, longitude));
    }

    public static Intent createEventLocationMapActivityIntent(
          Context ctx, Double latitude, Double longitude) {
        Intent result = new Intent(ctx, EventLocationMapActivity.class);
        if (latitude != null && longitude != null) {
            result.putExtra(LATITUDE_KEY, latitude);
            result.putExtra(LONGITUDE_KEY, longitude);
        }
        return result;
    }
}
