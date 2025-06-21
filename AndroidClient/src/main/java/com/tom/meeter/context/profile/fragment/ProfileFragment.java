package com.tom.meeter.context.profile.fragment;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.peekToken;
import static com.tom.meeter.context.event.activity.EventActivity.dispatchToEventActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.genderResolver;
import static com.tom.meeter.infrastructure.common.DateHelper.getAgeFromDate;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.viewmodel.ProfileViewModel;
import com.tom.meeter.databinding.FragmentProfileEditableBinding;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;
import com.tom.meeter.infrastructure.components.adapter.EventsCardAdapter;
import com.tom.meeter.infrastructure.components.binder.PhotoDownloaderEventBinder;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

/**
 * Created by Tom on 14.12.2016.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getCanonicalName();

    private FragmentProfileEditableBinding binding;

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    ImageDownloader imageDownloader;

    private ProfileViewModel profileViewModel;

    private AccountManager accountManager;

    private EventsCardAdapter adapter;

    public ProfileFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);

        ((App) getActivity().getApplication()).getComponent().inject(this);

        Context ctx = getContext();
        accountManager = AccountManager.get(ctx);

        adapter = new EventsCardAdapter(
              new PhotoDownloaderEventBinder(ctx, imageDownloader,
                    event -> dispatchToEventActivity(ctx, event.getId()),
                    () -> InfrastructureHelper.restartActivityFromFragment(this)));
    }

    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        binding = FragmentProfileEditableBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);
        profileViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(ProfileViewModel.class);
        profileViewModel.fetchProfile(peekToken(accountManager), this);
        profileViewModel.getProfileLiveData()
              .observe(
                    getViewLifecycleOwner(),
                    user -> {
                        binding.profileId.setText(user.getId());
                        binding.profileName.setText(user.getName());
                        binding.profileSurname.setText(user.getSurname());
                        binding.profileGender.setText(genderResolver(getContext(), user.getGender()));
                        binding.profileBirthday.setText(user.getBirthday());
                        binding.profileAge.setText(getString(R.string.profile_age_format, getAgeFromDate(user.getBirthday())));
                        binding.profileInfo.setText(user.getInfo());
                    });

        profileViewModel.getProfileEventsLiveData()
              .observe(getViewLifecycleOwner(), events -> adapter.setData(events));

        binding.profileEventsGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.profileEventsGrid.setAdapter(adapter);
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
