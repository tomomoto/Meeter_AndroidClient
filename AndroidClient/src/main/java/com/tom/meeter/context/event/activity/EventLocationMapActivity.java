package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.profile.component.fragment.GoogleMapsFragment.ZOOM_VALUE;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.gps.domain.LocationTrackerListener;
import com.tom.meeter.context.gps.service.LocationTrackerService;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.ActivityEventPositionBinding;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class EventLocationMapActivity extends AppCompatActivity
      implements OnMapReadyCallback {

    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LNG = "extra_lng";

    private static final String TAG = EventLocationMapActivity.class.getCanonicalName();

    @Inject
    EventService service;
    @Inject
    ImageDownloader imgDownloader;

    private ActivityEventPositionBinding binding;
    private LocationTrackerService locationService;
    private ServiceConnection sConn;
    private LocationTrackerListener singleLocationUpdateListener;
    private final Runnable onNotAuthenticated = this::finish;
    private GoogleMap gmap;

    private Marker eventMarker;
    private boolean cameraMoved = false;
    private LatLng userLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (EventDispatcherActivity.incorrect(this)) {
            return;
        }

        binding = ActivityEventPositionBinding.inflate(getLayoutInflater());
        FrameLayout view = binding.getRoot();
        setContentView(view);

        ((App) getApplication()).getEventComponent().inject(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.eventSelectPosition);
        if (mapFragment == null) {
            return;
        }
        mapFragment.getMapAsync(this);
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
                    unbindService(sConn);
                    locationService = null;
                    sConn = null;
                }
            }
        };
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                logMethod(TAG, this);
                locationService = ((LocationTrackerService.ServiceBinder) binder).getService();
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
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        service.getEvent(
              AuthHelper.getAuthHeader(AccountManager.get(this)),
              EventDispatcherActivity.getEventId(this)).enqueue(
              new BaseOnNotAuthenticatedCallback<>(this, onNotAuthenticated) {
                  @Override
                  public void onResponse(Call<EventDTO> call, Response<EventDTO> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK || resp.body() == null) {
                          return;
                      }
                      EventDTO event = resp.body();

                      gmap.setOnMapClickListener(
                            latLng -> {
                                if (eventMarker != null) {
                                    eventMarker.setPosition(latLng);
                                    return;
                                }
                                if (userLocation != null) {
                                    setupEventMarker(latLng, event);
                                }
                                Log.d(TAG, "Event marker is null, user location" +
                                      " is null, nothing to do...");
                            });

                      if (event.getLatitude() == null || event.getLongitude() == null) {
                          setupSingleLocationListener();
                          return;
                      }
                      LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
                      setupEventMarker(latLng, event);
                      gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_VALUE));
                  }
              });
    }

    private void setupEventMarker(LatLng latLng, EventDTO event) {
        eventMarker = gmap.addMarker(
              new MarkerOptions()
                    .position(latLng)
                    .title(event.getName()));
        String photoPath = event.getPhotoPath();
        if (photoPath == null) {
            return;
        }
        imgDownloader.downloadEventImage(
              photoPath, this, ImagesHelper::circleImage,
              (photo) -> eventMarker.setIcon(BitmapDescriptorFactory.fromBitmap(photo)),
              onNotAuthenticated);
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

    public static void dispatchToEventLocationMapActivity(
          Context ctx, String eventId) {
        ctx.startActivity(createEventLocationMapActivityIntent(ctx, eventId));
    }

    public static Intent createEventLocationMapActivityIntent(Context ctx, String eventId) {
        Intent result = new Intent(ctx, EventLocationMapActivity.class);
        result.putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
        return result;
    }
}
