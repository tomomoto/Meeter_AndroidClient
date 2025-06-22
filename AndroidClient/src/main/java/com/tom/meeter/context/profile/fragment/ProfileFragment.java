package com.tom.meeter.context.profile.fragment;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.event.activity.EventActivity.dispatchToEventActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.EMPTY_STR;
import static com.tom.meeter.infrastructure.common.CommonHelper.genderResolver;
import static com.tom.meeter.infrastructure.common.CommonHelper.getLocalDateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.getStringOrNull;
import static com.tom.meeter.infrastructure.common.DateHelper.getAgeFromDate;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
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
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.activity.SubscribersActivity;
import com.tom.meeter.context.profile.message.UpdateProfileRequest;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.viewmodel.ProfileViewModel;
import com.tom.meeter.databinding.FragmentProfileBinding;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;
import com.tom.meeter.infrastructure.components.adapter.EventsCardAdapter;
import com.tom.meeter.infrastructure.components.binder.PhotoDownloaderEventBinder;
import com.tom.meeter.infrastructure.http.ActivityRestarterOnAuthFailure;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import java.time.LocalDate;
import java.util.Objects;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Tom on 14.12.2016.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getCanonicalName();
    private boolean isEditableModeEnabled = false;

    private FragmentProfileBinding binding;

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    ImageDownloader imageDownloader;
    @Inject
    ProfileService profileService;
    private ProfileViewModel profileViewModel;
    private AccountManager accountManager;
    private EventsCardAdapter adapter;

    private UserDTO userCache;
    private ResponseBody photoCache;

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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);

        profileViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(ProfileViewModel.class);
        String authHeader = getAuthHeader(accountManager);

        profileViewModel.fetchProfile(authHeader, this);

        profileViewModel.getProfileLiveData()
              .observe(
                    getViewLifecycleOwner(),
                    user -> {
                        userCache = user;
                        updateLayoutValues();
                    });

        profileViewModel.getProfileEventsLiveData()
              .observe(getViewLifecycleOwner(), events -> adapter.setData(events));

        binding.events.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.events.setAdapter(adapter);

        binding.btnEdit.setOnClickListener(v -> {
            if (isEditableModeEnabled) {
                UpdateProfileRequest req = createUpdateProfileRequest();
                if (req.isEmpty()) {
                    showMessage(this.getActivity(), R.string.empty_update_request_is_not_sent);
                    updateLayoutValues();
                    switchEditMode();
                    return;
                }
                profileService.updateProfile(authHeader, req)
                      .enqueue(new ActivityRestarterOnAuthFailure<>(this) {
                          @Override
                          public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                              super.onResponse(call, response);
                              if (response.code() == HttpCodes.OK && response.body() != null) {
                                  userCache = response.body();
                                  showMessage(ProfileFragment.this.getActivity(), R.string.saved);
                              }
                              updateLayoutValues();
                          }
                      });
            }
            switchEditMode();
        });
        binding.subscribers.setOnClickListener(
              v -> startActivity(
                    new Intent(
                          ProfileFragment.this.getContext(), SubscribersActivity.class)));
    }

    private void updateLayoutValues() {
        /*binding.profileId.setText(userCache.getId());*/
        binding.name.setText(userCache.getName());
        binding.surname.setText(userCache.getSurname());
        binding.gender.setText(genderResolver(getContext(), userCache.getGender()));
        LocalDate birthday = userCache.getBirthday();
        binding.birthday.setText(birthday == null ? EMPTY_STR : birthday.toString());
        binding.age.setText(getString(R.string.profile_age_format, getAgeFromDate(birthday)));
        binding.info.setText(userCache.getInfo());
    }

    private void switchEditMode() {
        isEditableModeEnabled = !isEditableModeEnabled;
        binding.name.setEnabled(isEditableModeEnabled);
        binding.surname.setEnabled(isEditableModeEnabled);
        binding.birthday.setEnabled(isEditableModeEnabled);
        binding.info.setEnabled(isEditableModeEnabled);
        binding.btnEdit.setText(
              isEditableModeEnabled ? getString(R.string.save) : getString(R.string.edit));
    }

    private UpdateProfileRequest createUpdateProfileRequest() {
        UpdateProfileRequest req = new UpdateProfileRequest();

        String nameChange = getStringOrNull(binding.name.getText());
        if (!Objects.equals(userCache.getName(), nameChange)) {
            req.setName(nameChange);
        }
        String surnameChange = getStringOrNull(binding.surname.getText());
        if (!Objects.equals(userCache.getSurname(), surnameChange)) {
            req.setSurname(surnameChange);
        }
        LocalDate birthdayChange = getLocalDateOrNull(binding.birthday.getText());
        if (!Objects.equals(userCache.getBirthday(), birthdayChange)) {
            req.setBirthday(birthdayChange);
        }
        String infoChange = getStringOrNull(binding.info.getText());
        if (!Objects.equals(userCache.getInfo(), infoChange)) {
            req.setInfo(infoChange);
        }
        //TODO: userCache.getPhotoPath();
        return req;
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
