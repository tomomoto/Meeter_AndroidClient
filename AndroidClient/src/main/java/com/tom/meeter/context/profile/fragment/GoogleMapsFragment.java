package com.tom.meeter.context.profile.fragment;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.tom.meeter.infrastructure.common.Constants.APP_PROPERTIES;
import static com.tom.meeter.infrastructure.common.Constants.MAP_EVENTS_AREA_PROPERTY;
import static com.tom.meeter.infrastructure.common.Constants.MAP_TRACK_USER_PROPERTY;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
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
import com.google.common.collect.Sets;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.tom.meeter.R;
import com.tom.meeter.context.gps.domain.LocationTrackerListener;
import com.tom.meeter.context.gps.service.LocationTrackerService;
import com.tom.meeter.context.network.domain.SearchForEvents;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.eventbus.events.IncomeEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Tom on 09.12.2016.
 */
public class GoogleMapsFragment extends Fragment
      implements OnMapReadyCallback, LocationTrackerListener {

    private static final String TAG = GoogleMapsFragment.class.getCanonicalName();
    private static final FontAwesome FONT_AWESOME = new FontAwesome();
    private static final float ZOOM_VALUE = 17;
    private static final LatLng DEFAULT = new LatLng(0.0, 0.0);

    private ServiceConnection locationServiceConnection;
    private LocationTrackerService locationService;

    private String meString;
    private boolean trackUser;
    private int searchArea;

    private boolean firstOpening = true;
    private Marker userMarker = null;
    private Circle searchCircle = null;
    private GoogleMap gmap = null;
    private CameraPosition camPosition = null;
    private final Map<String, GMapEvent> events = new HashMap<>();

    public GoogleMapsFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onAttach(Context context) {
        logMethod(TAG, this);
        super.onAttach(context);
        meString = getString(R.string.me);
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
                locationService.addLocationTrackerListener(GoogleMapsFragment.this);
            }

            public void onServiceDisconnected(ComponentName name) {
                logMethod(TAG, this);
                locationService = null;
            }
        };
        Context ctx = getContext();
        if (ctx == null) {
            throw new IllegalStateException("Context is null");
        }
        Intent service = new Intent(ctx, LocationTrackerService.class);
        ctx.bindService(service, locationServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        GoogleMapOptions opts = new GoogleMapOptions();
        opts.zoomControlsEnabled(true);
        SupportMapFragment sMapFragment = SupportMapFragment.newInstance(opts);
        sMapFragment.getMapAsync(this);
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            fm.beginTransaction()
                  .replace(R.id.event_fragment_sub_fragment_gmap, sMapFragment)
                  .commit();
        }
        return inflater.inflate(R.layout.sub_fragment_gmaps, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        logMethod(TAG, this);
        gmap = googleMap;

        putExistingMarkersOnMap();

        if (locationService == null) {
            Log.w(TAG, "Location service is not ready...");
        }
        LatLng lastKnownUserLocation = null;
        if (locationService != null && locationService.canGetLocation()) {
            Location lkl = locationService.getLastKnownLocation();
            if (lkl != null) {
                lastKnownUserLocation = mapToLatTng(lkl);
            } else {
                Log.w(TAG, "Can get location, but service returns null.");
            }
        } else {
            Toast.makeText(getContext(), R.string.location_is_disabled, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Unable to get last known user location.");
        }

        moveCamera(lastKnownUserLocation, gmap, firstOpening, camPosition);

        if (lastKnownUserLocation != null) {
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            //gmap.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null);
            userMarker = gmap.addMarker(getMarkerOptions(lastKnownUserLocation, getContext(), meString));
            searchCircle = gmap.addCircle(getCircleOptions(lastKnownUserLocation, searchArea));
        } else if (camPosition != null) {
            searchCircle = gmap.addCircle(getCircleOptions(camPosition.target, searchArea));
        } else {
            searchCircle = gmap.addCircle(getCircleOptions(DEFAULT, searchArea));
        }
        gmap.setOnMapClickListener((latLng) -> Log.d(TAG, "onMapClickListener() " + latLng));
        gmap.setOnCameraIdleListener(this::idleListener);
        gmap.setOnMarkerClickListener(this::markerClickListener);
        firstOpening = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        logMethod(TAG, this);
        if (trackUser) {
            Toast.makeText(getContext(), R.string.location_changed, Toast.LENGTH_SHORT).show();
            if (gmap == null) {
                Log.w(TAG, "Gmap is not ready...");
                if (userMarker != null) {
                    userMarker.setPosition(mapToLatTng(location));
                }
            } else {
                if (userMarker == null) {
                    userMarker = gmap.addMarker(getMarkerOptions(mapToLatTng(location), getContext(), meString));
                } else {
                    userMarker.setPosition(mapToLatTng(location));
                }
                searchCircle.setCenter(userMarker.getPosition());
                if (camPosition != null) {
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), camPosition.zoom), 1200, null);
                }
            }
        }
    }

    private void idleListener() {
        if (camPosition == null
              || camPosition.target.latitude != gmap.getCameraPosition().target.latitude
              || camPosition.target.longitude != gmap.getCameraPosition().target.longitude) {
            camPosition = gmap.getCameraPosition();
            LatLng position = camPosition.target;
            Log.d(TAG, "onCameraIdleListener() target:" + position + " zoom:" + camPosition.zoom);
            searchCircle.setCenter(position);
            searchForEvents(position.latitude, position.longitude, searchArea);
        } else {
            // As new coordinates income...
            camPosition = gmap.getCameraPosition();
            searchCircle.setCenter(camPosition.target);
        }
    }

    private boolean markerClickListener(Marker marker) {
        Log.d(TAG, "OnMarkerClickListener() " + marker.getId());
        GMapEvent search = null;
        for (GMapEvent event : events.values()) {
            if (marker.getId().equals(event.getMarkerId())) {
                search = event;
                break;
            }
        }
        if (search != null) {
            //start event description activity etc...
            Log.d(TAG, "OnMarkerClickListener() find event " + search.getName());
        }
        return false;
    }

    private void putExistingMarkersOnMap() {
        for (GMapEvent e : events.values()) {
            e.replaceMarker(
                  gmap.addMarker(
                        createFreshOpts(e.getName(), e.getLatitude(), e.getLongitude())));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
        if (locationService != null) {
            locationService.removeLocationTrackerListener(this);
        }
        getContext().unbindService(locationServiceConnection);
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "GoogleMapsFragment Unregistered event bus");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IncomeEvents msg) {
        Map<String, EventDTO> incomeEvents = new HashMap<>();
        for (EventDTO e : msg.getEvents()) {
            incomeEvents.put(e.getId(), e);
        }

        Set<String> currentEventIds = new HashSet<>(events.keySet());
        Set<String> incomeEventIds = incomeEvents.keySet();

        Sets.SetView<String> toRemove = Sets.difference(currentEventIds, incomeEventIds);
        Sets.SetView<String> toAdd = Sets.difference(incomeEventIds, currentEventIds);
        Sets.SetView<String> toUpdate = Sets.intersection(currentEventIds, incomeEventIds);

        // Events to remove -
        for (String eId : toRemove) {
            events.remove(eId).removeMarker();
        }

        // Events to add -
        for (String eId : toAdd) {
            EventDTO ev = incomeEvents.get(eId);
            events.put(
                  eId,
                  new GMapEvent(
                        ev, gmap.addMarker(
                        createFreshOpts(ev.getName(), ev.getLatitude(), ev.getLongitude()))));
        }

        // Intersection - need to apply events update, if any
        for (String eId : toUpdate) {
            updateWith(events.get(eId), incomeEvents.get(eId));
        }
    }

    private void readParameters() {
        Properties p = new Properties();
        try {
            p.load(getContext().getAssets().open(APP_PROPERTIES));
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        searchArea = Integer.parseInt(p.getProperty(MAP_EVENTS_AREA_PROPERTY));
        trackUser = Boolean.parseBoolean(p.getProperty(MAP_TRACK_USER_PROPERTY));
    }

    private static void moveCamera(
          LatLng lastKnownUserLocation, GoogleMap gmap, boolean firstOpening, CameraPosition camPosition) {
        if (firstOpening) {
            if (lastKnownUserLocation != null) {
                gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnownUserLocation, ZOOM_VALUE), 6000, null);
            }
        } else if (camPosition != null) {
            gmap.moveCamera(CameraUpdateFactory.newCameraPosition(camPosition));
        }
    }

    private static void updateWith(GMapEvent me, EventDTO update) {
        if (!update.getName().equals(me.getName())) {
            me.updateName(update.getName());
        }
        if (update.getLatitude() != me.getLatitude()
              || update.getLongitude() != me.getLongitude()) {
            Log.d(TAG, "Location for event " + update.getName()
                  + " " + update.getId() + " is changed. Moving the marker.");
            me.updatePosition(update.getLatitude(), update.getLongitude());
        }
    }

    private static MarkerOptions createFreshOpts(String name, double latitude, double longitude) {
        return new MarkerOptions()
              .title(name)
              .position(new LatLng(latitude, longitude));
    }

    private static LatLng mapToLatTng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
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

        //BitmapDescriptorFactory.fromResource(R.drawable.userlocation);
        //BitmapDescriptorFactory.fromAsset(myBitmap);
        //BitmapDescriptorFactory.fromFile(myBitmap);
        //BitmapDescriptorFactory.fromPath(myBitmap);
        return BitmapDescriptorFactory.fromBitmap(myBitmap);
    }

    private static MarkerOptions getMarkerOptions(LatLng latLng, Context context, String title) {
        return new MarkerOptions()
              .icon(getUserIconBitmap(context))
              .title(title)
              .position(latLng);
    }

    private static CircleOptions getCircleOptions(LatLng center, int searchArea) {
        return new CircleOptions()
              .center(center)
              .radius(searchArea)
              //.fillColor(Color.TRANSPARENT)
              .strokeColor(0x10000000)
              .strokeWidth(3)
              .fillColor(0x3aaaffff);
    }

    private static void searchForEvents(double latitude, double longitude, int searchArea) {
        EventBus.getDefault()
              .post(new SearchForEvents((float) latitude, (float) longitude, searchArea));
    }

    static class GMapEvent {

        private EventDTO event;
        private Marker marker;

        public GMapEvent(EventDTO event, Marker marker) {
            validate(event, marker);
            this.event = event;
            this.marker = marker;
        }

        public void removeMarker() {
            marker.remove();
        }

        public String getName() {
            return event.getName();
        }

        public double getLatitude() {
            return event.getLatitude();
        }

        public double getLongitude() {
            return event.getLongitude();
        }

        public String getMarkerId() {
            return marker.getId();
        }

        public void updateName(String name) {
            event.setName(name);
            marker.setTitle(name);
        }

        public void updatePosition(double latitude, double longitude) {
            event.setLatitude(latitude);
            event.setLongitude(longitude);
            marker.setPosition(new LatLng(latitude, longitude));
        }

        public void replaceMarker(Marker marker) {
            validate(event, marker);
            this.marker = marker;
        }

        private static void validate(EventDTO event, Marker marker) {
            String name = event.getName();
            String title = marker.getTitle();
            if (!name.equals(title)) {
                throw new IllegalArgumentException("Names are not equals " + name + ":" + title);
            }
            LatLng position = marker.getPosition();
            double latitude = event.getLatitude();
            if (latitude != position.latitude) {
                throw new IllegalArgumentException(
                      "Latitudes are not equals " + latitude + ":" + position.latitude);
            }
            double longitude = event.getLongitude();
            if (longitude != position.longitude) {
                throw new IllegalArgumentException(
                      "Longitude are not equals " + longitude + ":" + position.longitude);
            }
        }
    }

    static class EventKey {

        private String id;
        private double latitude;
        private double longitude;

        public EventKey(String id, double latitude, double longitude) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            EventKey eventKey = (EventKey) o;
            return Double.compare(latitude, eventKey.latitude) == 0
                  && Double.compare(longitude, eventKey.longitude) == 0
                  && Objects.equals(id, eventKey.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, latitude, longitude);
        }
    }
}