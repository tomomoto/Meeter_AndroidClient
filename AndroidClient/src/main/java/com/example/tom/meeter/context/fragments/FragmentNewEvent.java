package com.example.tom.meeter.context.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.tom.meeter.context.gps.service.GPSTrackerService;
import com.example.tom.meeter.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Tom on 14.12.2016.
 */
public class FragmentNewEvent extends Fragment {

    private GPSTrackerService gpsTrackerService;

    @BindView(R.id.new_event_place)
    EditText newEventPlaceTextView;

    public FragmentNewEvent() {
    }

    private Unbinder unbinder;

    @OnClick(R.id.create_event)
    public void CreateEvent(Button button) {
        button.setText("HI");
    }

    @OnClick(R.id.new_event_current_place)
    public void CurrentPlace(Button button) {
        Location location = gpsTrackerService.getLocation();
        newEventPlaceTextView.setText(String.valueOf(location.getLatitude()) + "; " + location.getLongitude());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTrackerService = new GPSTrackerService(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_event, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
