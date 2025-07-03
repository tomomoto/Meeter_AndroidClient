package com.tom.meeter.context.profile.component.fragment;

import static com.tom.meeter.context.event.activity.EventDispatcherActivity.dispatchToEventActivity;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.context.profile.component.adapter.EventsAdapter;
import com.tom.meeter.databinding.SubFragmentActiveEventsBinding;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;
import com.tom.meeter.infrastructure.eventbus.events.IncomeEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

/**
 * Created by Tom on 09.12.2016.
 */
public class ActiveEventsFragment extends Fragment {

    private static final String TAG = ActiveEventsFragment.class.getCanonicalName();

    @Inject
    EventsAdapter adapter;

    private SubFragmentActiveEventsBinding binding;
    private final Runnable onAuthFail =
          () -> InfrastructureHelper.restartActivityFromFragment(this);

    public ActiveEventsFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this, "Registering eventBus");

        ((App) getActivity().getApplication()).getProfileComponent().inject(this);

        EventBus.getDefault().register(this);

        Context ctx = requireContext();
        adapter.initialize(
              ctx, onAuthFail,
              (e) -> dispatchToEventActivity(ctx, e.getId()));
    }

    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
        logMethod(TAG, this);
        binding = SubFragmentActiveEventsBinding.inflate(
              inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
          @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);
        binding.activeEventsFragmentRecyclerView.setLayoutManager(
              new LinearLayoutManager(getActivity()));
        binding.activeEventsFragmentRecyclerView.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IncomeEvents eventsSearch) {
        adapter.setData(eventsSearch.events());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        logMethod(TAG, this, "Unregistered event bus");
    }

    @Override
    public void onDestroyView() {
        logMethod(TAG, this);
        super.onDestroyView();
    }
}
