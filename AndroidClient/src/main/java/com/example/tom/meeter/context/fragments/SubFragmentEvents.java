package com.example.tom.meeter.context.fragments;

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

import com.example.tom.meeter.infrastructure.EventDTO;
import com.example.tom.meeter.infrastructure.RecycleViewEventAdapter;
import com.example.tom.meeter.context.network.domain.IncomeEvents;
import com.example.tom.meeter.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SubFragmentEvents extends Fragment {

    private static final String SUB_FRAGMENT_EVENTS_TAG = SubFragmentEvents.class.getCanonicalName();

    private RecyclerView rView;
    private RecycleViewEventAdapter rvEventAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<EventDTO> events;


    public SubFragmentEvents() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        events = new ArrayList<>();
        EventBus.getDefault().register(this);
        //events.add(new EventDTO(1,"name","descr",1,"","",""));
        //events.add(new EventDTO(2,"name2","descr2",1,"","",""));
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
        rView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //rView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        rView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        rvEventAdapter = new RecycleViewEventAdapter(events);
        rView.setAdapter(rvEventAdapter);
        rView.invalidate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IncomeEvents event) {
        int eventsSize = event.getEvents().length();
        events.clear();
        if (eventsSize > 0) {
            for (int i = 0; i < eventsSize; i++) {
                try {
                    events.add(EventDTO.encode((JSONObject) event.getEvents().get(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        rvEventAdapter = new RecycleViewEventAdapter(events);
        rView.swapAdapter(rvEventAdapter, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.d(SUB_FRAGMENT_EVENTS_TAG, "unregistered bus");
    }
}