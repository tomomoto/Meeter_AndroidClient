package com.tom.meeter.context.profile.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.EXTRA_LAT;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.EXTRA_LNG;
import static com.tom.meeter.context.event.activity.ProfileEventActivity.dispatchToProfileEventActivity;
import static com.tom.meeter.context.image.activity.BaseUploadActivity.PHOTO_PATH_RESULT;
import static com.tom.meeter.context.profile.activity.NewEventOnMapActivity.createNewEventOnMapActivityIntent;
import static com.tom.meeter.context.profile.utils.Utils.createEventRequest;
import static com.tom.meeter.infrastructure.common.CommonHelper.getAppLogo;
import static com.tom.meeter.infrastructure.common.CommonHelper.isEmpty;
import static com.tom.meeter.infrastructure.common.DateHelper.isDateValid;
import static com.tom.meeter.infrastructure.common.DateHelper.setCurrentDate;
import static com.tom.meeter.infrastructure.common.DateHelper.setCurrentTime;
import static com.tom.meeter.infrastructure.common.DateHelper.showDatePicker;
import static com.tom.meeter.infrastructure.common.DateHelper.showTimePicker;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.gps.service.LocationTrackerService;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.image.activity.UploadEventImageActivity;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.message.CreateEventRequest;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.databinding.FragmentCreateEventBinding;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Tom on 14.12.2016.
 */
public class CreateEventFragment extends Fragment {

    private static final String TAG = CreateEventFragment.class.getCanonicalName();

    @Inject
    ProfileService service;
    @Inject
    ImageDownloader imgDownloader;

    private FragmentCreateEventBinding binding;
    private ServiceConnection sConn;
    private LocationTrackerService locationService;
    private AccountManager accountManager;

    private final Runnable onNotAuthenticated =
          () -> InfrastructureHelper.restartActivityFromFragment(this);
    private ActivityResultLauncher<Intent> mapResult;
    private final ActivityResultLauncher<Intent> imageUploadLauncher =
          registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                        return;
                    }
                    String photoPath = result.getData().getStringExtra(PHOTO_PATH_RESULT);
                    imgDownloader.downloadEventImage(
                          photoPath, requireContext(), ImagesHelper::bigCircleImage,
                          (photo) -> binding.photo.setImageBitmap(photo),
                          onNotAuthenticated);
                    binding.photoPath.setText(photoPath);
                });

    public CreateEventFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);

        ((App) getActivity().getApplication()).getComponent().inject(this);

        Context ctx = requireContext();
        accountManager = AccountManager.get(ctx);

        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                logMethod(TAG, this);
                locationService = ((LocationTrackerService.ServiceBinder) binder).getService();
            }

            public void onServiceDisconnected(ComponentName name) {
                logMethod(TAG, this);
                locationService = null;
            }
        };

        mapResult = registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
              result -> {
                  if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                      return;
                  }
                  double lat = result.getData().getDoubleExtra(EXTRA_LAT, 0.0);
                  double lng = result.getData().getDoubleExtra(EXTRA_LNG, 0.0);
                  binding.latitude.setText(String.valueOf(lat));
                  binding.longitude.setText(String.valueOf(lng));
              });

        ctx.bindService(
              new Intent(ctx, LocationTrackerService.class), sConn, BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
          @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context ctx = requireContext();

        /* Photo */
        binding.selectPhotoButton.setOnClickListener(
              v -> imageUploadLauncher.launch(new Intent(ctx, UploadEventImageActivity.class)));

        /* Name */
        binding.name.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateForm();
            }
        });

        /* Starting */
        binding.startsCurrentDateBtn.setOnClickListener(v -> setCurrentDate(binding.startsDate));
        binding.startsOtherDateBtn.setOnClickListener(v -> showDatePicker(ctx, binding.startsDate));
        binding.startsCurrentTimeBtn.setOnClickListener(v -> setCurrentTime(binding.startsTime));
        binding.startsOtherTimeBtn.setOnClickListener(v -> showTimePicker(ctx, binding.startsTime));
        binding.startsDate.addTextChangedListener(
              createDataWatcher(
                    binding.startsCurrentTimeBtn,
                    binding.startsOtherTimeBtn,
                    binding.startsDateValidator));

        /* Ending */
        binding.endsCurrentDateBtn.setOnClickListener(v -> setCurrentDate(binding.endsDate));
        binding.endsOtherDateBtn.setOnClickListener(v -> showDatePicker(ctx, binding.endsDate));
        binding.endsCurrentTimeBtn.setOnClickListener(v -> setCurrentTime(binding.endsTime));
        binding.endsOtherTimeBtn.setOnClickListener(v -> showTimePicker(ctx, binding.endsTime));
        binding.endsDate.addTextChangedListener(
              createDataWatcher(
                    binding.endsCurrentTimeBtn,
                    binding.endsOtherTimeBtn,
                    binding.endsDateValidator));

        /* Location */
        binding.currentLocationBtn.setOnClickListener(v -> {
            Location location = locationService.getLastKnownLocation();
            if (location == null) {
                showMessage(requireContext(), R.string.unable_to_get_the_location);
                return;
            }
            binding.latitude.setText(String.valueOf(location.getLatitude()));
            binding.longitude.setText(String.valueOf(location.getLongitude()));
        });
        binding.otherLocationBtn.setOnClickListener(
              v -> mapResult.launch(createNewEventOnMapActivityIntent(ctx)));

        binding.createBtn.setOnClickListener(this::createEventClickHandler);
    }

    public void createEventClickHandler(View ign) {
        CreateEventRequest req = createEventRequest(binding);
        if (req.isEmpty()) {
            showMessage(requireContext(), R.string.empty_create_request_is_not_sent);
            return;
        }
        service.createEvent(AuthHelper.getAuthHeader(accountManager), req)
              .enqueue(new BaseOnNotAuthenticatedCallback<>(
                    requireContext(), onNotAuthenticated) {
                  @Override
                  public void onResponse(
                        Call<EventDTO> call, Response<EventDTO> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK || resp.body() == null) {
                          return;
                      }
                      showEventDialog(resp.body());
                  }
              });
    }

    private void validateForm() {
        binding.createBtn.setEnabled(allSet());
    }

    private boolean allSet() {
        return !isEmpty(binding.name.getText());
    }

    private void showEventDialog(EventDTO event) {
        new AlertDialog.Builder(requireContext())
              .setIcon(getAppLogo())
              .setTitle(R.string.event_created)
              .setMessage(
                    getString(R.string.event_is_created, event.getName()))
              .setPositiveButton(
                    R.string.to_event,
                    (dialog, which) -> dispatchToProfileEventActivity(
                          requireContext(), event.getId()))
              .setNegativeButton(
                    R.string.back,
                    (dialog, which) -> dialog.dismiss())
              .show();
    }

    @NonNull
    private TextWatcher createDataWatcher(
          Button currentTime, Button otherTime, TextView target) {
        return new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable e) {
                if (isEmpty(e)) {
                    currentTime.setEnabled(false);
                    otherTime.setEnabled(false);
                } else if (!isDateValid(e.toString())) {
                    target.setText(R.string.wrong_date);
                    currentTime.setEnabled(false);
                    otherTime.setEnabled(false);
                } else {
                    target.setText(R.string.correct_date);
                    currentTime.setEnabled(true);
                    otherTime.setEnabled(true);
                }
                validateForm();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
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
        requireContext().unbindService(sConn);
    }

    public static abstract class BaseTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

    }

    private static boolean requiredFieldsProvided(
          CharSequence name, CharSequence latitude, CharSequence longitude) {
        return !isEmpty(name) && !isEmpty(latitude) && !isEmpty(longitude);
    }
}
