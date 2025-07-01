package com.tom.meeter.context.profile.fragment;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.adapter.EventsAdapter;
import com.tom.meeter.context.profile.factory.ProfileEventsAssistedFactory;
import com.tom.meeter.context.profile.viewmodel.ProfileEventsViewModel;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.databinding.SubFragmentUserEventsBinding;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;

import javax.inject.Inject;

public class ProfileEventsFragment extends Fragment {

    private static final String TAG = ProfileEventsFragment.class.getCanonicalName();

    SubFragmentUserEventsBinding binding;

    @Inject
    EventsAdapter adapter;
    @Inject
    ProfileEventsAssistedFactory assistedFactory;
    @Inject
    ImageDownloader imageDownloader;
    @Inject
    UserService service;

    private final Runnable onAuthFail =
          () -> InfrastructureHelper.restartActivityFromFragment(this);
    private ProfileEventsViewModel viewModel;

    public ProfileEventsFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);

        ((App) getActivity().getApplication()).getComponent().inject(this);

        Context ctx = requireContext();

        adapter.setupBinder(
              ctx, onAuthFail,
              (e) -> dispatchToEventActivity(ctx, e.getId()));
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

        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(
                    assistedFactory, requireContext(),
                    () -> InfrastructureHelper.restartActivityFromFragment(this)))
              .get(ProfileEventsViewModel.class);

        binding.userEventsFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.userEventsFragmentRecyclerView.setAdapter(adapter);

        viewModel.getEvents()
              .observe(getViewLifecycleOwner(), events -> adapter.setData(events));
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
