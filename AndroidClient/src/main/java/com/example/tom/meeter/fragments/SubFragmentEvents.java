package com.example.tom.meeter.fragments;

/**
 * Created by Tom on 09.12.2016.
 */


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tom.meeter.Infrastructure.Event;
import com.example.tom.meeter.Infrastructure.RecycleViewEventAdapter;
import com.example.tom.meeter.MyAdapter;
import com.example.tom.meeter.NetworkEvents.EventsIncomeEvent;
import com.example.tom.meeter.R;
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


public class SubFragmentEvents extends Fragment {

    private static final String TAG = SubFragmentEvents.class.getName();
    private RecyclerView mRecyclerView;
    private RecycleViewEventAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public SubFragmentEvents() {
        // Required empty public constructor
    }

    private List<Event> events;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        events = new ArrayList<>();
        EventBus.getDefault().register(this);
        //events.add(new Event(1,"name","descr",1,"","",""));
        //events.add(new Event(2,"name2","descr2",1,"","",""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.subfragment_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecycleViewEventAdapter(events);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.invalidate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventsIncomeEvent incomeEvent) {
        int myJsonArraySize = incomeEvent.getEvents().length();
        events.clear();
        if (myJsonArraySize > 0) {
            for (int i = 0; i < myJsonArraySize; i++) {
                try {
                    JSONObject event = (JSONObject) incomeEvent.getEvents().get(i);
                    Integer eID = (Integer) event.get("event_id");
                    String name = (String) event.get("name");
                    String desc = (String) event.get("description");
                    Integer oID = (Integer) event.get("creator_id");
                    double latitude = (double) event.get("latitude");
                    double longitude = (double) event.get("longitude");
                    String created = (String) event.get("created");
                    String starting = (String) event.get("starting");
                    String ending = (String) event.get("ending");
                    Event localEvent = new Event(eID, name, desc, oID, created, starting, ending);
                    events.add(localEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        mAdapter = new RecycleViewEventAdapter(events);
        mRecyclerView.swapAdapter(mAdapter,false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.d(TAG,"unregistered bus");
    }
}