package com.tom.meeter.context.profile.component.fragment;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.event.activity.EventDispatcherActivity.dispatchToEventActivity;
import static com.tom.meeter.context.image.activity.BaseUploadActivity.PHOTO_PATH_RESULT;
import static com.tom.meeter.context.profile.utils.Utils.createUpdateProfileRequest;
import static com.tom.meeter.infrastructure.common.CommonHelper.EMPTY_STR;
import static com.tom.meeter.infrastructure.common.CommonHelper.genderResolver;
import static com.tom.meeter.infrastructure.common.DateHelper.getAgeFromDate;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.image.activity.UploadUserImageActivity;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.component.activity.SubscribersActivity;
import com.tom.meeter.context.profile.component.activity.SubscriptionsActivity;
import com.tom.meeter.context.profile.component.viewmodel.ProfileViewModel;
import com.tom.meeter.context.profile.factory.ProfileAssistedFactory;
import com.tom.meeter.context.profile.message.UpdateProfileRequest;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.databinding.FragmentProfileBinding;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;
import com.tom.meeter.infrastructure.components.adapter.EventsCardAdapter;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.time.LocalDate;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Tom on 14.12.2016.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getCanonicalName();

    @Inject
    ProfileAssistedFactory assistedFactory;
    @Inject
    ImageDownloader imageDownloader;
    @Inject
    ProfileService service;
    @Inject
    EventsCardAdapter adapter;

    private final Runnable onAuthFail =
          () -> InfrastructureHelper.restartActivityFromFragment(this);
    private AccountManager accountManager;
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private boolean isEditableModeEnabled = false;
    private UserDTO userCache;

    public ProfileFragment() {
        logMethod(TAG, this);
    }

    private final ActivityResultLauncher<Intent> imageUploadLauncher =
          registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String photoPath = result.getData().getStringExtra(PHOTO_PATH_RESULT);
                        downloadAndUpdateLayoutPhoto(photoPath);
                        binding.photoPath.setText(photoPath);
                    }
                });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);

        ((App) getActivity().getApplication()).getProfileComponent().inject(this);

        Context ctx = requireContext();
        accountManager = AccountManager.get(ctx);
        adapter.initialize(
              ctx, onAuthFail,
              event -> dispatchToEventActivity(ctx, event.getId()));
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

        Context ctx = requireContext();
        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(assistedFactory, ctx, onAuthFail))
              .get(ProfileViewModel.class);

        LifecycleOwner owner = getViewLifecycleOwner();
        viewModel.getProfile()
              .observe(
                    owner,
                    user -> {
                        userCache = user;
                        updateLayoutValues();
                    });

        binding.events.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.events.setAdapter(adapter);

        viewModel.getEvents()
              .observe(owner, events -> adapter.setData(events));
        viewModel.getPhoto()
              .observe(owner, this::updateLayoutPhoto);

        binding.btnEdit.setOnClickListener(v -> {
            if (isEditableModeEnabled) {
                UpdateProfileRequest req = createUpdateProfileRequest(binding, userCache);
                if (req.isEmpty()) {
                    showMessage(requireActivity(), R.string.empty_update_request_is_not_sent);
                    switchEditMode();
                    return;
                }
                service.updateProfile(getAuthHeader(accountManager), req)
                      .enqueue(new BaseOnNotAuthenticatedCallback<>(ctx, onAuthFail) {
                          @Override
                          public void onResponse(
                                Call<UserDTO> call, Response<UserDTO> response) {
                              super.onResponse(call, response);
                              if (response.code() != HttpCodes.OK) {
                                  updateLayoutValues();
                                  return;
                              }
                              String oldPhotoPath = userCache.getPhotoPath();
                              userCache = response.body();
                              if (!Objects.equals(oldPhotoPath, userCache.getPhotoPath())) {
                                  downloadAndUpdateLayoutPhoto(userCache.getPhotoPath());
                              }
                              showMessage(requireActivity(), R.string.saved);
                              updateLayoutValues();
                          }
                      });
            }
            switchEditMode();
        });
        binding.subscribers.setOnClickListener(
              v -> startActivity(new Intent(ctx, SubscribersActivity.class)));
        binding.subscriptions.setOnClickListener(
              v -> startActivity(new Intent(ctx, SubscriptionsActivity.class)));
        binding.btnPhoto.setOnClickListener(
              v -> imageUploadLauncher.launch(new Intent(ctx, UploadUserImageActivity.class)));
    }

    private void updateLayoutValues() {
        /*binding.profileId.setText(userCache.getId());*/
        binding.photoPath.setText(userCache.getPhotoPath());
        binding.name.setText(userCache.getName());
        binding.surname.setText(userCache.getSurname());
        binding.gender.setText(genderResolver(getContext(), userCache.getGender()));
        LocalDate birthday = userCache.getBirthday();
        binding.birthday.setText(birthday == null ? EMPTY_STR : birthday.toString());
        binding.age.setText(getString(R.string.profile_age_format, getAgeFromDate(birthday)));
        binding.info.setText(userCache.getInfo());
    }

    void downloadAndUpdateLayoutPhoto(String photoPath) {
        imageDownloader.downloadUserImage(
              photoPath, requireContext(), ImagesHelper::bigCircleImage,
              this::updateLayoutPhoto, onAuthFail);
    }

    private void updateLayoutPhoto(Bitmap photo) {
        binding.photo.setImageBitmap(photo);
    }

    private void switchEditMode() {
        isEditableModeEnabled = !isEditableModeEnabled;
        binding.btnPhoto.setEnabled(isEditableModeEnabled);
        binding.name.setEnabled(isEditableModeEnabled);
        binding.surname.setEnabled(isEditableModeEnabled);
        binding.birthday.setEnabled(isEditableModeEnabled);
        binding.info.setEnabled(isEditableModeEnabled);
        binding.btnEdit.setText(
              isEditableModeEnabled ? getString(R.string.save) : getString(R.string.edit));
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
