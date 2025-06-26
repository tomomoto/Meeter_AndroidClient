package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.profile.fragment.GoogleMapsFragment.ZOOM_VALUE;
import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.ActivityEventOnMapBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.ErrorLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class EventOnMapActivity extends AppCompatActivity
      implements OnMapReadyCallback {

    private static final String TAG = EventOnMapActivity.class.getCanonicalName();
    private GoogleMap gmap;
    private ActivityEventOnMapBinding binding;

    @Inject
    EventService eventService;
    @Inject
    ImageDownloader imageDownloader;

    private AccountManager accountManager;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            showMessage(this, "Unable to show map without extras provided.");
            finish();
        }
        eventId = extras.getString(EventDispatcherActivity.EVENT_ID_KEY);
        if (eventId == null) {
            showMessage(this, "Unable to show map without event_id provided.");
            finish();
        }

        binding = ActivityEventOnMapBinding.inflate(getLayoutInflater());
        FrameLayout view = binding.getRoot();
        setContentView(view);

        ((App) getApplication()).getEventComponent().inject(this);
        accountManager = AccountManager.get(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.eventOnMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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
                    Double latitude = event.getLatitude();
                    Double longitude = event.getLongitude();
                    if (latitude == null || longitude == null) {
                        showMessage(EventOnMapActivity.this, R.string.event_location_is_not_set_yet);
                        return;
                    }
                    LatLng eventLatLng = new LatLng(latitude, longitude);
                    String photoPath = event.getPhotoPath();
                    if (photoPath == null) {
                        gmap.addMarker(
                              new MarkerOptions()
                                    .position(eventLatLng)
                                    .title(event.getName()));
                        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, ZOOM_VALUE));
                    } else {
                        imageDownloader.downloadEventImage(
                              photoPath,
                              EventOnMapActivity.this.getApplicationContext(),
                              (photo) -> {
                                  gmap.addMarker(
                                        new MarkerOptions()
                                              .position(eventLatLng)
                                              .icon(BitmapDescriptorFactory.fromBitmap(circleImage(photo)))
                                              .title(event.getName()));
                                  gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, ZOOM_VALUE));
                              },
                              EventOnMapActivity.this::recreate);
                    }
                    return;
                }
                if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                    EventOnMapActivity.this.recreate();
                }
                Log.i(TAG, "/event/{id}: " + response.code() + " : " + response.body());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
    }

    public static void dispatchToEventOnMapActivity(Context ctx, String eventId) {
        if (ctx == null || eventId == null) {
            throw new IllegalArgumentException("ctx and event_id should present");
        }
        ctx.startActivity(createEventOnMapActivityIntent(ctx, eventId));
    }

    public static Intent createEventOnMapActivityIntent(
          Context ctx, String eventId) {
        Intent result = new Intent(ctx, EventOnMapActivity.class);
        result.putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
        return result;
    }
}
