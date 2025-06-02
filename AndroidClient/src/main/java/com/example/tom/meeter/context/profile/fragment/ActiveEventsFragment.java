package com.example.tom.meeter.context.profile.fragment;

/**
 * Created by Tom on 09.12.2016.
 */

import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.profile.RecycleViewActiveEventsAdapter;
import com.example.tom.meeter.infrastructure.eventbus.events.IncomeEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ActiveEventsFragment extends Fragment {

    private static final String TAG = ActiveEventsFragment.class.getCanonicalName();

    public static ActiveEventsFragment createEventListFragment(Bundle args) {
        ActiveEventsFragment result = new ActiveEventsFragment();
        result.setArguments(args);
        return result;
    }

    @BindView(R.id.active_events_fragment_recycler_view)
    RecyclerView recyclerView;

    private RecycleViewActiveEventsAdapter recycleViewActiveEventsAdapter;

    public ActiveEventsFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        EventBus.getDefault().register(this);
        Log.d(TAG, "EventListFragment Registering eventBus");
    }

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        View view = inflater.inflate(R.layout.sub_fragment_active_events, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //rView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // specify an adapter (see also next example)
        recycleViewActiveEventsAdapter = new RecycleViewActiveEventsAdapter();
        recyclerView.setAdapter(recycleViewActiveEventsAdapter);
        recyclerView.invalidate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IncomeEvents eventsSearch) {
        recycleViewActiveEventsAdapter.cleanEvents();
        if (!eventsSearch.getEvents().isEmpty()) {
            recycleViewActiveEventsAdapter.addEvents(eventsSearch.getEvents());
        }
        recyclerView.requestLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "EventListFragment Unregistered event bus");
    }
}