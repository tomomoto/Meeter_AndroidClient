package com.example.tom.meeter.context.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tom.meeter.App;
import com.example.tom.meeter.R;
import com.example.tom.meeter.context.event.RecycleViewUserEventsAdapter;
import com.example.tom.meeter.context.event.UserEventsViewModel;
import com.example.tom.meeter.infrastructure.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.tom.meeter.infrastructure.common.Constants.USER_ID_KEY;

public class UserEventsFragment extends Fragment {

    private static final String TAG = UserEventsFragment.class.getCanonicalName();

    @BindView(R.id.user_events_rv)
    RecyclerView rView;

    private RecycleViewUserEventsAdapter adapter;

    @Inject
    ViewModelFactory viewModelFactory;

    private UserEventsViewModel viewModel;

    public UserEventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_user_events, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserEventsViewModel.class);
        Bundle arguments = getArguments();
        viewModel.init(arguments.getString(USER_ID_KEY));
        viewModel.getUserEvents().observe(this, ev -> adapter.setData(ev));

        adapter = new RecycleViewUserEventsAdapter();

        rView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rView.setAdapter(adapter);
        rView.invalidate();

        /*
        adapter = new RecycleViewUserEventsAdapter(events);
        rView.swapAdapter(adapter, false);
        */

    }
}
