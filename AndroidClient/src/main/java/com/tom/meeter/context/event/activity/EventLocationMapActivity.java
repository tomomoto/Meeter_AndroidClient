package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.profile.fragment.GoogleMapsFragment.ZOOM_VALUE;
import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

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

import androidx.annotation.Nullable;
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
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.ErrorLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class EventLocationMapActivity extends AppCompatActivity
      implements OnMapReadyCallback {

    private static final String TAG = EventLocationMapActivity.class.getCanonicalName();
    private static final String EVENT_ID_KEY = "event_id";
    private Marker eventMarker;
    private GoogleMap gmap;
    private ActivityEventPositionBinding binding;

    private ServiceConnection locationServiceConn;
    private LocationTrackerService locationService;
    private boolean cameraMoved = false;

    private LocationTrackerListener singleLocationUpdateListener;
    private String eventId;

    @Inject
    EventService eventService;
    @Inject
    ImageDownloader imageDownloader;
    private AccountManager accountManager;
    private LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            showMessage(this, "Unable to show map without extras provided.");
            finish();
        }
        eventId = extras.getString(EVENT_ID_KEY);
        if (eventId == null) {
            showMessage(this, "Unable to show map without event_id provided.");
            finish();
        }

        binding = ActivityEventPositionBinding.inflate(getLayoutInflater());
        FrameLayout view = binding.getRoot();
        setContentView(view);

        ((App) getApplication()).getEventComponent().inject(this);
        accountManager = AccountManager.get(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.eventSelectPosition);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        binding.btnConfirm.setOnClickListener(v -> {
            if (eventMarker != null) {
                Intent resultIntent = new Intent();
                LatLng position = eventMarker.getPosition();
                resultIntent.putExtra(ProfileEventActivity.EXTRA_LAT, position.latitude);
                resultIntent.putExtra(ProfileEventActivity.EXTRA_LNG, position.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Выберите точку на карте", Toast.LENGTH_SHORT).show();
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

        String token = AuthHelper.peekToken(accountManager);
        String authHeader = Globals.getAuthHeader(token);
        eventService.getEvent(authHeader, eventId).enqueue(new ErrorLogger<>(this) {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.code() == HttpCodes.OK) {
                    EventDTO event = response.body();

                    gmap.setOnMapClickListener(
                          latLng -> {
                              if (eventMarker != null) {
                                  eventMarker.setPosition(latLng);
                                  return;
                              }
                              if (userLocation != null) {
                                  eventMarker = createEventMarker(latLng, event);
                                  downloadEventImage(event);
                              }
                              Log.d(TAG, "Event marker is null, user location is null, nothing to do...");
                          });

                    if (event.getLatitude() == null || event.getLongitude() == null) {
                        setupSingleLocationListener();
                        return;
                    }
                    LatLng eventLatLng = new LatLng(event.getLatitude(), event.getLongitude());
                    eventMarker = createEventMarker(eventLatLng, event);
                    gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, ZOOM_VALUE));
                    downloadEventImage(event);
                    return;
                }
                if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                    EventLocationMapActivity.this.recreate();
                }
                Log.i(TAG, "/event/{id}: " + response.code() + " : " + response.body());
            }
        });
    }

    @Nullable
    private Marker createEventMarker(LatLng latLng, EventDTO event) {
        return gmap.addMarker(
              new MarkerOptions()
                    .position(latLng)
                    .title(event.getName()));
    }

    private void downloadEventImage(EventDTO event) {
        assert eventMarker != null;
        String photoPath = event.getPhotoPath();
        if (photoPath != null) {
            imageDownloader.downloadEventImage(
                  photoPath,
                  EventLocationMapActivity.this.getApplicationContext(),
                  (photo) -> eventMarker.setIcon(BitmapDescriptorFactory.fromBitmap(circleImage(photo))),
                  EventLocationMapActivity.this::recreate);
        }
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
          Context ctx, String eventId) {
        ctx.startActivity(createEventLocationMapActivityIntent(ctx, eventId));
    }

    public static Intent createEventLocationMapActivityIntent(Context ctx, String eventId) {
        Intent result = new Intent(ctx, EventLocationMapActivity.class);
        result.putExtra(EVENT_ID_KEY, eventId);
        return result;
    }
}
