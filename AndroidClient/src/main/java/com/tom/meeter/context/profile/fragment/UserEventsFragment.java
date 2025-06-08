package com.tom.meeter.context.profile.fragment;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.setupTokenAction;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.context.profile.adapter.RecycleViewUserEventsAdapter;
import com.tom.meeter.context.profile.viewmodel.ProfileEventsViewModel;
import com.tom.meeter.databinding.SubFragmentUserEventsBinding;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class UserEventsFragment extends Fragment {

    private static final String TAG = UserEventsFragment.class.getCanonicalName();

    SubFragmentUserEventsBinding binding;

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
        logMethod(TAG, this);
        binding = SubFragmentUserEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
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

        binding.userEventsFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.userEventsFragmentRecyclerView.setAdapter(adapter);
        binding.userEventsFragmentRecyclerView.invalidate();

        /*
        adapter = new RecycleViewUserEventsAdapter(events);
        rView.swapAdapter(adapter, false);
        */

    }
}
