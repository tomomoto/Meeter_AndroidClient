package com.tom.meeter.context.profile.fragment;

/**
 * Created by Tom on 09.12.2016.
 */

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.adapter.RecycleViewActiveEventsAdapter;
import com.tom.meeter.databinding.SubFragmentActiveEventsBinding;
import com.tom.meeter.infrastructure.eventbus.events.IncomeEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class ActiveEventsFragment extends Fragment {

    private static final String TAG = ActiveEventsFragment.class.getCanonicalName();

    SubFragmentActiveEventsBinding binding;

    @Inject
    ImageDownloader imageDownloader;

    private RecycleViewActiveEventsAdapter recycleViewActiveEventsAdapter;

    public ActiveEventsFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        EventBus.getDefault().register(this);
        Log.d(TAG, "ActiveEventsFragment Registering eventBus");
    }

    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        binding = SubFragmentActiveEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //rView.setHasFixedSize(true);

        // use a linear layout manager
        binding.activeEventsFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // specify an adapter (see also next example)
        recycleViewActiveEventsAdapter = new RecycleViewActiveEventsAdapter(this, imageDownloader);
        binding.activeEventsFragmentRecyclerView.setAdapter(recycleViewActiveEventsAdapter);
        binding.activeEventsFragmentRecyclerView.invalidate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IncomeEvents eventsSearch) {
        recycleViewActiveEventsAdapter.cleanEvents();
        if (!eventsSearch.events().isEmpty()) {
            recycleViewActiveEventsAdapter.addEvents(eventsSearch.events());
        }
        binding.activeEventsFragmentRecyclerView.requestLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "ActiveEventsFragment Unregistered event bus");
    }
}