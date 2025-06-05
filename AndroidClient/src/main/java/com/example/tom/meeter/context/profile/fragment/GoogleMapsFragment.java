package com.example.tom.meeter.context.profile.fragment;

/**
 * Created by Tom on 09.12.2016.
 */


import static android.content.Context.BIND_AUTO_CREATE;
import static com.example.tom.meeter.infrastructure.common.Constants.APP_PROPERTIES;
import static com.example.tom.meeter.infrastructure.common.Constants.EVENTS_AREA_PROPERTY;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.gps.domain.LocationTrackerListener;
import com.example.tom.meeter.context.gps.service.LocationTrackerService;
import com.example.tom.meeter.context.network.EventDTO;
import com.example.tom.meeter.context.network.domain.SearchForEvents;
import com.example.tom.meeter.infrastructure.eventbus.events.IncomeEvents;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.iconics.typeface.FontAwesome;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class GoogleMapsFragment extends Fragment
      implements OnMapReadyCallback, LocationTrackerListener {

    private static final String TAG = GoogleMapsFragment.class.getCanonicalName();
    private static final FontAwesome FONT_AWESOME = new FontAwesome();
    private static final float ZOOM_VALUE = 17;
    private SupportMapFragment supportMapFragment;
    private ServiceConnection locationServiceConnection;
    private LocationTrackerService locationService;
    private Marker userMarker;
    private Circle searchCircle;
    private GoogleMap gmap = null;
    private CameraPosition camPosition = null;
    private int searchArea;
    private boolean trackUser = true;
    private boolean firstOpening = true;

    private final List<Marker> eventMarkers = new ArrayList<>();

    public GoogleMapsFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        readParameters();
        EventBus.getDefault().register(this);

        Log.d(TAG, "GoogleMapsFragment registered event bus");

        locationServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                logMethod(TAG, this);
                locationService = ((LocationTrackerService.ServiceBinder) binder).getService();
            }

            public void onServiceDisconnected(ComponentName name) {
                logMethod(TAG, this);
                locationService = null;
            }
        };
        Context ctx = getContext();
        if (ctx != null) {
            Intent service = new Intent(ctx, LocationTrackerService.class);
            ctx.bindService(service, locationServiceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        supportMapFragment = SupportMapFragment.newInstance();
        supportMapFragment.getMapAsync(this);
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            fm.beginTransaction()
                  .replace(R.id.event_fragment_sub_fragment_gmap, supportMapFragment)
                  .commit();
        }
        return inflater.inflate(R.layout.sub_fragment_gmaps, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        logMethod(TAG, this);
        if (locationService == null) {
            return;
        }
        if (locationService.canGetLocation()) {
            Location lkl = locationService.getLastKnownLocation();
            LatLng lastKnownUserLocation = new LatLng(lkl.getLatitude(), lkl.getLongitude());
            gmap = googleMap;
            gmap.setOnMapClickListener((latLng) -> Log.d(TAG, "onMapClickListener " + latLng));
            gmap.setOnCameraIdleListener(
                  () -> {
                      camPosition = gmap.getCameraPosition();
                      Log.d(TAG, "onCameraIdleListener " + camPosition.target + " " + camPosition.zoom);
                      //!!! BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.userlocation);

                      //!!! userMarker.setIcon(icon);
                      //userMarker.zoom
                      if (camPosition != null) {
                          //_OLD_gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), camPosition.zoom), 1200, null);
                          //_NEW_gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, camPosition.zoom), 1200, null);
                      }
                      //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(),camPosition.zoom));
                      searchCircle.setCenter(camPosition.target);
                      EventBus.getDefault()
                            .post(new SearchForEvents(
                                  (float) camPosition.target.latitude,
                                  (float) camPosition.target.longitude,
                                  searchArea));
                      //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,camPosition.zoom));

                  });
            if (firstOpening || camPosition == null) {
                gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnownUserLocation, ZOOM_VALUE), 6000, null);
            } else {
                gmap.moveCamera(CameraUpdateFactory.newCameraPosition(camPosition));
            }
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            //gmap.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null);
            searchCircle = googleMap.addCircle(
                  new CircleOptions()
                        .center(lastKnownUserLocation)
                        .radius(searchArea)
                        //.fillColor(Color.TRANSPARENT)
                        .strokeColor(0x10000000)
                        .strokeWidth(3)
                        .fillColor(0x3aaaffff));
            locationService.addLocationTrackerListener(this);

            userMarker = googleMap.addMarker(
                  new MarkerOptions()
                        .icon(getUserIconBitmap(getContext()))
                        .title(getString(R.string.me))
                        .position(lastKnownUserLocation));
            firstOpening = false;
            EventBus.getDefault()
                  .post(new SearchForEvents((float) lastKnownUserLocation.latitude, (float) lastKnownUserLocation.longitude, searchArea));
        } else {
            Log.w(TAG, "Unable to get last known location.");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        logMethod(TAG, this);
        if (trackUser) {
            Toast.makeText(getContext(), R.string.location_changed, Toast.LENGTH_SHORT).show();
            userMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            if (camPosition != null) {
                gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), camPosition.zoom), 1200, null);
            }
            //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(),camPosition.zoom));
            searchCircle.setCenter(userMarker.getPosition());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
        locationService.removeLocationTrackerListener(this);
        getContext().unbindService(locationServiceConnection);
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "GoogleMapsFragment Unregistered event bus");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IncomeEvents incomeEvent) {
        removeMarkers();
        incomeEvent.getEvents()
              .stream()
              .map(GoogleMapsFragment::mapToMarkerOpts)
              .forEach(this::addMarker);
    }

    private void addMarker(MarkerOptions options) {
        eventMarkers.add(gmap.addMarker(options));
    }

    private void removeMarkers() {
        for (Marker marker : eventMarkers) {
            marker.remove();
        }
    }

    private void readParameters() {
        Properties p = new Properties();
        try {
            p.load(getContext().getAssets().open(APP_PROPERTIES));
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        searchArea = Integer.parseInt(p.getProperty(EVENTS_AREA_PROPERTY));
    }

    private static MarkerOptions mapToMarkerOpts(EventDTO e) {
        return new MarkerOptions()
              .title(e.getName())
              .position(new LatLng(e.getLatitude(), e.getLongitude()));
    }

    private static BitmapDescriptor getUserIconBitmap(Context context) {
        Bitmap myBitmap = Bitmap.createBitmap(125, 175, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(FONT_AWESOME.getTypeface(context));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setTextSize(120);
        myCanvas.drawText(
              String.valueOf(FontAwesome.Icon.faw_child.getCharacter()), 20, 90, paint);

        //BitmapDescriptorFactory.fromResource(myBitmap);
        //BitmapDescriptorFactory.fromAsset(myBitmap);
        //BitmapDescriptorFactory.fromFile(myBitmap);
        //BitmapDescriptorFactory.fromPath(myBitmap);
        return BitmapDescriptorFactory.fromBitmap(myBitmap);
    }
}