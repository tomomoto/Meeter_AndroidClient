package com.tom.meeter.context.profile.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.EXTRA_LAT;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.EXTRA_LNG;
import static com.tom.meeter.context.event.activity.ProfileEventActivity.dispatchToProfileEventActivity;
import static com.tom.meeter.context.profile.activity.NewEventOnMapActivity.createNewEventOnMapActivityIntent;
import static com.tom.meeter.context.profile.utils.Utils.createPublishEventRequest;
import static com.tom.meeter.infrastructure.common.CommonHelper.isEmpty;
import static com.tom.meeter.infrastructure.common.DateHelper.isDateValid;
import static com.tom.meeter.infrastructure.common.DateHelper.setCurrentDate;
import static com.tom.meeter.infrastructure.common.DateHelper.setCurrentTime;
import static com.tom.meeter.infrastructure.common.DateHelper.showDatePicker;
import static com.tom.meeter.infrastructure.common.DateHelper.showTimePicker;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
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
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.message.PublishEventRequest;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.databinding.FragmentNewEventBinding;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Tom on 14.12.2016.
 */
public class CreateNewEventFragment extends Fragment {

    private static final String TAG = CreateNewEventFragment.class.getCanonicalName();

    @Inject
    ProfileService service;

    private FragmentNewEventBinding binding;
    private ServiceConnection sConn;
    private LocationTrackerService locationService;
    private AccountManager accountManager;
    private ActivityResultLauncher<Intent> mapResult;

    public CreateNewEventFragment() {
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
                  binding.newEventLatitudeEditText.setText(String.valueOf(lat));
                  binding.newEventLongitudeEditText.setText(String.valueOf(lng));
              });

        ctx.bindService(
              new Intent(ctx, LocationTrackerService.class), sConn, BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        binding = FragmentNewEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
          @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context ctx = requireContext();

        /* Starting */
        binding.newEventStartsCurrentDateBtn.setOnClickListener(
              v -> setCurrentDate(binding.newEventStartsDateEditText));
        binding.newEventStartsOtherDateBtn.setOnClickListener(
              v -> showDatePicker(ctx, binding.newEventStartsDateEditText));
        binding.newEventStartsCurrentTimeBtn.setOnClickListener(
              v -> setCurrentTime(binding.newEventStartsTimeEditText));
        binding.newEventStartsOtherTimeBtn.setOnClickListener(
              v -> showTimePicker(ctx, binding.newEventStartsTimeEditText));
        binding.newEventStartsDateEditText.addTextChangedListener(
              createDataWatcher(
                    binding.newEventStartsCurrentTimeBtn,
                    binding.newEventStartsOtherTimeBtn,
                    binding.newEventStartsDateTextView));

        /* Ending */
        binding.newEventEndsCurrentDateBtn.setOnClickListener(
              v -> setCurrentDate(binding.newEventEndsDateEditText));
        binding.newEventEndsOtherDateBtn.setOnClickListener(
              v -> showDatePicker(ctx, binding.newEventEndsDateEditText));
        binding.newEventEndsCurrentTimeBtn.setOnClickListener(
              v -> setCurrentTime(binding.newEventEndsTimeEditText));
        binding.newEventEndsOtherTimeBtn.setOnClickListener(
              v -> showTimePicker(ctx, binding.newEventEndsTimeEditText));
        binding.newEventEndsDateEditText.addTextChangedListener(
              createDataWatcher(
                    binding.newEventEndsCurrentTimeBtn,
                    binding.newEventEndsOtherTimeBtn,
                    binding.newEventEndsDateTextView));

        /* Location */
        binding.newEventCurrentPlaceBtn.setOnClickListener(v -> {
            Location location = locationService.getLastKnownLocation();
            if (location == null) {
                showMessage(requireContext(), R.string.unable_to_get_the_location);
                return;
            }
            binding.newEventLatitudeEditText.setText(String.valueOf(location.getLatitude()));
            binding.newEventLongitudeEditText.setText(String.valueOf(location.getLongitude()));
        });
        binding.newEventOtherPlaceBtn.setOnClickListener(
              v -> mapResult.launch(createNewEventOnMapActivityIntent(ctx)));

        TextWatcher watcher = createTextWatcher();
        binding.newEventLatitudeEditText.addTextChangedListener(watcher);
        binding.newEventLongitudeEditText.addTextChangedListener(watcher);
        binding.newEventNameEditText.addTextChangedListener(watcher);

        binding.newEventCreateBtn.setOnClickListener(this::createEventClickHandler);
    }

    public void createEventClickHandler(View ign) {
        PublishEventRequest req = createPublishEventRequest(binding);
        if (req.isEmpty()) {
            showMessage(requireContext(), R.string.empty_create_request_is_not_sent);
            return;
        }
        service.publishEvent(AuthHelper.getAuthHeader(accountManager), req)
              .enqueue(new BaseOnNotAuthenticatedCallback<>(
                    requireContext(),
                    () -> InfrastructureHelper.restartActivityFromFragment(this)) {
                  @Override
                  public void onResponse(
                        Call<EventDTO> call, Response<EventDTO> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK && resp.body() != null) {
                          showEventDialog(resp.body());
                          return;
                      }
                      new AlertDialog.Builder(requireContext())
                            .setIcon(R.drawable.ic_meeter_lr)
                            .setTitle(R.string.failed)
                            .setMessage(R.string.failed_to_create_event)
                            .setPositiveButton(R.string.ok, (dialog, id) -> dialog.cancel())
                            .create()
                            .show();
                      return;
                  }
              });
    }

    private void validateWholeForm() {
        if (!allSet()) {
            return;
        }
        binding.newEventCreateBtn.setEnabled(true);
    }

    private boolean allSet() {
        return !isEmpty(binding.newEventNameEditText.getText())
              && !isEmpty(binding.newEventLatitudeEditText.getText())
              && !isEmpty(binding.newEventLongitudeEditText.getText())
              && isDateValid(binding.newEventStartsDateEditText.getText())
              && isDateValid(binding.newEventEndsDateEditText.getText());
    }

    private void showEventDialog(EventDTO event) {
        new AlertDialog.Builder(requireContext())
              .setIcon(R.drawable.ic_meeter_lr)
              .setTitle(R.string.event_published)
              .setMessage(
                    getString(
                          R.string.recently_created_event_is_published,
                          event.getName()))
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
                if (!isDateValid(e.toString())) {
                    target.setText(R.string.wrong_date);
                    binding.newEventCreateBtn.setEnabled(false);
                    currentTime.setEnabled(false);
                    otherTime.setEnabled(false);
                    return;
                }
                currentTime.setEnabled(true);
                otherTime.setEnabled(true);
                target.setText(R.string.correct_date);
                validateWholeForm();
            }
        };
    }

    @NonNull
    private TextWatcher createTextWatcher() {
        return new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                CharSequence name = binding.newEventNameEditText.getText();
                CharSequence latitude = binding.newEventLatitudeEditText.getText();
                CharSequence longitude = binding.newEventLongitudeEditText.getText();

                if (requiredFieldsProvided(name, latitude, longitude)) {
                    validateWholeForm();
                    return;
                }
                binding.newEventCreateBtn.setEnabled(false);
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
