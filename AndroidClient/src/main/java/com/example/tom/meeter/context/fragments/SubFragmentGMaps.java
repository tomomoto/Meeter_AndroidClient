package com.example.tom.meeter.context.fragments;

/**
 * Created by Tom on 09.12.2016.
 */


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tom.meeter.context.gps.service.GPSTrackerService;
import com.example.tom.meeter.context.gps.domain.GPSTrackerLocationListener;
import com.example.tom.meeter.context.network.domain.EventsIncomeEvent;
import com.example.tom.meeter.context.network.domain.FindNewEventsEvent;
import com.example.tom.meeter.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SubFragmentGMaps extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GPSTrackerLocationListener, GoogleMap.OnCameraChangeListener {

    private final String TAG = SubFragmentGMaps.class.getCanonicalName();

    private SupportMapFragment sMapFragment;
    private GPSTrackerService gpsTrackerService;
    private LatLng myLocation;
    private Marker userMarker;
    private GoogleMap gmap = null;
    private Boolean trackUser = true;
    private CameraPosition camPosition = null;
    private Circle userCircle;
    private double searchArea = 0;
    private List<Marker> eventMarkers = new ArrayList<>();

    public SubFragmentGMaps() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTrackerService = new GPSTrackerService(getContext());
        sMapFragment = SupportMapFragment.newInstance();
        sMapFragment.getMapAsync(this);
        searchArea = 5000;
        EventBus.getDefault().register(this);
        Log.d(TAG, "registered bus");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.map, sMapFragment).commit();
        return inflater.inflate(R.layout.subfragment_gmaps, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (gpsTrackerService.canGetLocation()) {
            myLocation = new LatLng(gpsTrackerService.getLatitude(), gpsTrackerService.getLongitude());
        }
        gmap = googleMap;
        gmap.setOnMapClickListener(this);
        gmap.setOnCameraChangeListener(this);
        //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15));
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14), 6000, null);
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //gmap.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null);
        userCircle = googleMap.addCircle(new CircleOptions().center(myLocation).radius(searchArea)
                //.fillColor(Color.TRANSPARENT)
                .strokeColor(0x10000000)
                .strokeWidth(3)
                .fillColor(0x3aaaffff));
        gpsTrackerService.addGPSTrackerListener(this);
        userMarker = googleMap.addMarker(new MarkerOptions().title("I am").position(myLocation));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        userMarker.setPosition(new LatLng(latLng.latitude, latLng.longitude));
        //!!! BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.userlocation);
        EventBus.getDefault().post(new FindNewEventsEvent(latLng.latitude, latLng.longitude, searchArea));
        //!!! userMarker.setIcon(icon);
        //userMarker.zoom
        if (camPosition != null) {
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), camPosition.zoom), 1200, null);
        }
        //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(),camPosition.zoom));
        userCircle.setCenter(latLng);
        //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,camPosition.zoom));
    }

    @Override
    public void onGPSTrackerLocationChanged(Location newLocation) {
        if (trackUser) {
            Toast.makeText(getContext(), "location changed", Toast.LENGTH_SHORT).show();
            userMarker.setPosition(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
            if (camPosition != null)
                gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), camPosition.zoom), 1200, null);
            //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(),camPosition.zoom));
            userCircle.setCenter(userMarker.getPosition());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gpsTrackerService.removeGPSTrackerListener(this);
        gpsTrackerService.stopUsingGPS();
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "unregistered bus");
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        camPosition = cameraPosition;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventsIncomeEvent incomeEvent) {
        int eventsSize = incomeEvent.getEvents().length();
        for (Marker marker : eventMarkers) {
            marker.remove();
        }
        eventMarkers = new ArrayList<>();
        for (int i = 0; i < eventsSize; i++) {
            try {
                JSONObject event = (JSONObject) incomeEvent.getEvents().get(i);
                double latitude = (double) event.get("latitude");
                double longitude = (double) event.get("longitude");
                String name = (String) event.get("name");
                Marker marker = gmap.addMarker(new MarkerOptions().title(name).position(new LatLng(latitude, longitude)));
                eventMarkers.add(marker);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}