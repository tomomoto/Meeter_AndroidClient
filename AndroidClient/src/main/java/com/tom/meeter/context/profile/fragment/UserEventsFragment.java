package com.tom.meeter.context.profile.fragment;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.setupTokenAction;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
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

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.profile.viewmodel.ProfileEventsViewModel;
import com.tom.meeter.context.profile.adapter.RecycleViewUserEventsAdapter;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserEventsFragment extends Fragment {

    private static final String TAG = UserEventsFragment.class.getCanonicalName();

    @BindView(R.id.user_events_fragment_recycler_view)
    RecyclerView recyclerView;

    private RecycleViewUserEventsAdapter adapter;

    @Inject
    ViewModelFactory viewModelFactory;

    private ProfileEventsViewModel profileEventsViewModel;

    private AccountManager accountManager;

    public UserEventsFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this.getContext());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sub_fragment_user_events, container, false);
        ButterKnife.bind(this, view);
        logMethod(TAG, this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logMethod(TAG, this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);
        profileEventsViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileEventsViewModel.class);

        setupTokenAction(accountManager, this.getActivity(),
              token -> profileEventsViewModel.getProfileEvents(Constants.getAuthHeader(token)));

        adapter = new RecycleViewUserEventsAdapter();
        profileEventsViewModel.getProfileEventsLiveData()
              .observe(this, ev -> adapter.setData(ev));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();

        /*
        adapter = new RecycleViewUserEventsAdapter(events);
        rView.swapAdapter(adapter, false);
        */

    }
}
