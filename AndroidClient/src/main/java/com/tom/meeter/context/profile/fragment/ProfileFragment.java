package com.tom.meeter.context.profile.fragment;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.peekToken;
import static com.tom.meeter.context.event.activity.EventActivity.dispatchToEventActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.genderResolver;
import static com.tom.meeter.infrastructure.common.DateHelper.getAgeFromDate;
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
import androidx.recyclerview.widget.GridLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.viewmodel.ProfileViewModel;
import com.tom.meeter.databinding.FragmentProfileBinding;
import com.tom.meeter.infrastructure.adapter.EventsRecyclerViewAdapter;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

/**
 * Created by Tom on 14.12.2016.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getCanonicalName();

    private FragmentProfileBinding binding;

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    ImageDownloader imageDownloader;

    private ProfileViewModel profileViewModel;

    private AccountManager accountManager;

    public ProfileFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this.getContext());
        logMethod(TAG, this);
    }

    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
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
                        binding.profileId.setText(getString(R.string.profile_user_id_format, user.getId()));
                        binding.profileName.setText(getString(R.string.profile_user_name_format, user.getName(), user.getSurname()));
                        binding.profileGender.setText(getString(R.string.profile_gender_format, genderResolver(getContext(), user.getGender())));
                        binding.profileAge.setText(getString(R.string.profile_age_format, getAgeFromDate(user.getBirthday())));
                        binding.profileInfo.setText(getString(R.string.profile_info_format, user.getInfo()));
                    });
        profileViewModel.getProfileEventsLiveData()
              .observe(
                    getViewLifecycleOwner(),
                    events -> {
                        binding.profileEventsGrid.setLayoutManager(
                              new GridLayoutManager(getContext(), 2));
                        binding.profileEventsGrid.setAdapter(
                              new EventsRecyclerViewAdapter(
                                    getContext(), events, imageDownloader,
                                    () -> InfrastructureHelper.restartActivityFromFragment(this),
                                    event -> dispatchToEventActivity(getContext(), event.getId())));
                    });
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
