package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.profile.component.fragment.GoogleMapsFragment.ZOOM_VALUE;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class EventOnMapActivity extends AppCompatActivity
      implements OnMapReadyCallback {

    private static final String TAG = EventOnMapActivity.class.getCanonicalName();

    @Inject
    EventService service;
    @Inject
    ImageDownloader imgDownloader;

    //TODO remake onNotAuthenticated
    private final Runnable onNotAuthenticated = this::finish;
    private ActivityEventOnMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventDispatcherActivity.validate(this)) {
            return;
        }

        binding = ActivityEventOnMapBinding.inflate(getLayoutInflater());
        FrameLayout view = binding.getRoot();
        setContentView(view);

        ((App) getApplication()).getEventComponent().inject(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.eventOnMap);
        if (mapFragment == null) {
            return;
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        service.getEvent(
              AuthHelper.getAuthHeader(AccountManager.get(this)),
              EventDispatcherActivity.getEventId(this)).enqueue(
              //TODO:
              // token is not checked at start,
              // in case of invalid token infinity recreation
              new BaseOnNotAuthenticatedCallback<>(this, onNotAuthenticated) {
                  @Override
                  public void onResponse(Call<EventDTO> call, Response<EventDTO> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK || resp.body() == null) {
                          return;
                      }
                      EventDTO event = resp.body();
                      Double latitude = event.getLatitude();
                      Double longitude = event.getLongitude();
                      if (latitude == null || longitude == null) {
                          showMessage(EventOnMapActivity.this, R.string.event_location_is_not_set_yet);
                          return;
                      }
                      LatLng latLng = new LatLng(latitude, longitude);
                      String photoPath = event.getPhotoPath();
                      if (photoPath == null) {
                          addMarkerMoveCamera(googleMap, latLng, event.getName(), null);
                          return;
                      }
                      imgDownloader.downloadEventImage(
                            photoPath, EventOnMapActivity.this,
                            ImagesHelper::circleImage,
                            (photo) -> addMarkerMoveCamera(
                                  googleMap,
                                  latLng,
                                  event.getName(),
                                  BitmapDescriptorFactory.fromBitmap(photo)),
                            onNotAuthenticated);
                      return;
                  }
              });
    }

    private void addMarkerMoveCamera(
          GoogleMap map, LatLng latLng,
          String title, BitmapDescriptor bmd) {
        map.addMarker(
              new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(bmd));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_VALUE));
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
