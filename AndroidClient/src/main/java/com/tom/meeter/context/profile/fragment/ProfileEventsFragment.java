package com.tom.meeter.context.profile.fragment;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.event.activity.EventActivity.dispatchToEventActivity;
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
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.adapter.EventsAdapter;
import com.tom.meeter.context.profile.viewmodel.ProfileEventsViewModel;
import com.tom.meeter.databinding.SubFragmentUserEventsBinding;
import com.tom.meeter.infrastructure.binder.PhotoDownloaderWithCacheEventBinder;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class ProfileEventsFragment extends Fragment {

    private static final String TAG = ProfileEventsFragment.class.getCanonicalName();

    SubFragmentUserEventsBinding binding;

    private EventsAdapter adapter;

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    ImageDownloader imageDownloader;

    private ProfileEventsViewModel profileEventsViewModel;

    private AccountManager accountManager;

    public ProfileEventsFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this.getContext());

        adapter = new EventsAdapter(
              new PhotoDownloaderWithCacheEventBinder(
                    this, imageDownloader,
                    (e) -> dispatchToEventActivity(getContext(), e.getId())));
        /*
        btnDelete.setOnClickListener(v -> {
            if (onDeleteButtonClickListener != null)
                onDeleteButtonClickListener.onDeleteButtonClicked(post);
        });*/
    }

    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        binding = SubFragmentUserEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);
        profileEventsViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(ProfileEventsViewModel.class);
        profileEventsViewModel.fetchProfileEvents(getAuthHeader(accountManager), this);
        profileEventsViewModel.getProfileEventsLiveData()
              .observe(getViewLifecycleOwner(), events -> adapter.setData(events));
        binding.userEventsFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.userEventsFragmentRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        logMethod(TAG, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        logMethod(TAG, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        logMethod(TAG, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
    }
}
