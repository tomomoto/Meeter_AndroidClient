package com.tom.meeter.context.profile.fragment;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tom.meeter.R;
import com.tom.meeter.context.gps.service.LocationTrackerService;
import com.tom.meeter.context.network.domain.CreateNewEventAttempt;
import com.tom.meeter.databinding.FragmentNewEventBinding;
import com.tom.meeter.infrastructure.eventbus.events.FailureEventCreation;
import com.tom.meeter.infrastructure.eventbus.events.SuccessfulEventCreation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tom on 14.12.2016.
 */
public class CreateNewEventFragment extends Fragment {

    private static final String TAG = CreateNewEventFragment.class.getCanonicalName();
    private static final String EMPTY_STR = "";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    FragmentNewEventBinding binding;

    private ServiceConnection locationServiceConnection;
    private LocationTrackerService locationService;


    public CreateNewEventFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        locationServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                logMethod(TAG, this);
                locationService = ((LocationTrackerService.ServiceBinder) binder).getService();
            }

            public void onServiceDisconnected(ComponentName name) {
                logMethod(TAG, this);
                locationService = null;
            }
        };
        Context ctx = getContext();
        if (ctx != null) {
            Intent service = new Intent(ctx, LocationTrackerService.class);
            ctx.bindService(service, locationServiceConnection, BIND_AUTO_CREATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        binding = FragmentNewEventBinding.inflate(inflater, container, false);

        binding.newEventOtherPlaceBtn.setOnClickListener(v -> {
            //TODO: Implement otherPlaceClickHandler
        });
        binding.newEventEndsOtherTimeBtn.setOnClickListener(v -> {
            //TODO:
            binding.newEventEndsTimeEditText.setText(null);
        });
        binding.newEventStartsOtherDateBtn.setOnClickListener(v -> {
            //TODO: Implement otherDateClickHandler
            binding.newEventStartsDateEditText.setText(null);
        });
        binding.newEventEndsOtherDateBtn.setOnClickListener(v -> {
            //TODO: Implement otherDateClickHandler
            binding.newEventEndsDateEditText.setText(null);
        });
        binding.newEventStartsOtherTimeBtn.setOnClickListener(v -> {
            //TODO: Implement otherDateClickHandler
            binding.newEventStartsTimeEditText.setText(null);
        });

        binding.newEventStartsCurrentDateBtn.setOnClickListener(
              v -> binding.newEventStartsDateEditText.setText(DATE_FORMAT.format(new Date())));
        binding.newEventEndsCurrentDateBtn.setOnClickListener(
              v -> binding.newEventEndsDateEditText.setText(DATE_FORMAT.format(new Date())));
        binding.newEventStartsCurrentTimeBtn.setOnClickListener(v -> startsCurrentTimeClickHandler());
        binding.newEventEndsCurrentTimeBtn.setOnClickListener(v -> endsCurrentTimeClickHandler());

        binding.newEventCurrentPlaceBtn.setOnClickListener(v -> currentPlaceClickHandler());
        binding.newEventCreateBtn.setOnClickListener(v -> createEventClickHandler());

        TextWatcher watcher = createTextWatcher();
        binding.newEventLatitudeEditText.addTextChangedListener(watcher);
        binding.newEventLongitudeEditText.addTextChangedListener(watcher);
        binding.newEventNameEditText.addTextChangedListener(watcher);

        binding.newEventStartsDateEditText.addTextChangedListener(createStartDataWatcher());
        binding.newEventEndsDateEditText.addTextChangedListener(createEndDataWatcher());

        return binding.getRoot();
    }

    @NonNull
    private TextWatcher createEndDataWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                endsDateChangedListener();
            }
        };
    }

    @NonNull
    private TextWatcher createStartDataWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                startsDateChangedListener();
            }
        };
    }

    public void startsDateChangedListener() {
        String s = binding.newEventStartsDateEditText.getText().toString();
        if (!isDateValid(s)) {
            binding.newEventStartsDateTextView.setText(getString(R.string.wrong_date));
            binding.newEventCreateBtn.setEnabled(false);
            return;
        }
        binding.newEventStartsDateTextView.setText(getString(R.string.correct_date));
        validateWholeForm();
    }

    public void endsDateChangedListener() {
        String e = binding.newEventEndsDateEditText.getText().toString();
        if (!isDateValid(e)) {
            binding.newEventEndsDateTextView.setText(getString(R.string.wrong_date));
            binding.newEventCreateBtn.setEnabled(false);
            return;
        }
        binding.newEventEndsDateTextView.setText(getString(R.string.correct_date));
        validateWholeForm();
    }

    public void locationChanges() {
        CharSequence name = binding.newEventNameEditText.getText();
        CharSequence latitude = binding.newEventLatitudeEditText.getText();
        CharSequence longitude = binding.newEventLongitudeEditText.getText();

        if (requiredFieldsNotProvided(name, latitude, longitude)) {
            binding.newEventCreateBtn.setEnabled(false);
        } else {
            validateWholeForm();
        }
    }

    @NonNull
    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                locationChanges();
            }
        };
    }

    @SuppressLint("SetTextI18n")
    public void endsCurrentTimeClickHandler() {
        binding.newEventEndsTimeEditText.setText(TIME_FORMAT.format(LocalTime.now()));
    }

    @SuppressLint("SetTextI18n")
    public void startsCurrentTimeClickHandler() {
        binding.newEventStartsTimeEditText.setText(TIME_FORMAT.format(LocalTime.now()));
    }

    @Override
    public void onStart() {
        super.onStart();
        logMethod(TAG, this);
        EventBus.getDefault().register(this);
        Log.d(TAG, "CreateNewEventFragment Event bus registered...");
        //Log.d(TAG, "Time :" + ZonedDateTime.now().toString());
    }

    @Override
    public void onStop() {
        super.onStop();
        logMethod(TAG, this);
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "CreateNewEventFragment Event bus unregistered...");
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
        getContext().unbindService(locationServiceConnection);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void currentPlaceClickHandler() {
        Location location = locationService.getLastKnownLocation();
        if (location != null) {
            binding.newEventLatitudeEditText.setText(String.valueOf(location.getLatitude()));
            binding.newEventLongitudeEditText.setText(String.valueOf(location.getLongitude()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode = " + requestCode + ", resultCode = " + resultCode);
    }

    public void createEventClickHandler() {
        String startDate = binding.newEventStartsDateEditText.getText().toString();
        String startTime = binding.newEventStartsTimeEditText.getText().toString();
        LocalDate localStartDate = LocalDate.parse(startDate);
        LocalTime localStartTime = LocalTime.parse(startTime);

        String endDate = binding.newEventEndsDateEditText.getText().toString();
        String endTime = binding.newEventEndsTimeEditText.getText().toString();
        LocalDate localEndDate = LocalDate.parse(endDate);
        LocalTime localEndTime = LocalTime.parse(endTime);

        ZoneOffset offset = OffsetDateTime.now().getOffset();
        OffsetDateTime starts = OffsetDateTime.of(localStartDate, localStartTime, offset);
        OffsetDateTime ends = OffsetDateTime.of(localEndDate, localEndTime, offset);
        EventBus.getDefault()
              .post(new CreateNewEventAttempt(
                    binding.newEventNameEditText.getText().toString(),
                    binding.newEventDescriptionEditText.getText().toString(),
                    starts, ends,
                    Float.valueOf(binding.newEventLatitudeEditText.getText().toString()),
                    Float.valueOf(binding.newEventLongitudeEditText.getText().toString())));
    }

    private static boolean requiredFieldsNotProvided(
          CharSequence name, CharSequence latitude, CharSequence longitude) {
        return name == null || EMPTY_STR.equals(name.toString())
              || latitude == null || EMPTY_STR.equals(latitude.toString())
              || longitude == null || EMPTY_STR.equals(longitude.toString());
    }

    private void validateWholeForm() {
        if (allSet()) {
            binding.newEventCreateBtn.setEnabled(true);
        }
    }

    private boolean allSet() {
        return binding.newEventNameEditText.getText() != null
              && !EMPTY_STR.equals(binding.newEventNameEditText.getText().toString())
              && binding.newEventLatitudeEditText.getText() != null
              && !EMPTY_STR.equals(binding.newEventLatitudeEditText.getText().toString())
              && binding.newEventLongitudeEditText.getText() != null
              && !EMPTY_STR.equals(binding.newEventLongitudeEditText.getText().toString())
              && isDateValid(binding.newEventStartsDateEditText.getText().toString())
              && isDateValid(binding.newEventEndsDateEditText.getText().toString());
    }

    private static boolean isDateValid(String date) {
        try {
            Calendar.getInstance().setTime(DATE_FORMAT.parse(date));
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SuccessfulEventCreation ev) {
        Log.d(TAG, ev.toString());
        new AlertDialog.Builder(getContext())
              .setTitle("Event created, id: " + ev.getId())
              .setMessage("Created.")
              .setNegativeButton(getString(R.string.ok), (dialog, id) -> dialog.cancel())
              .create()
              .show();
    /*startActivity(new Intent(RegistrationActivity.this, ProfileActivity.class
        .putExtra(USER_ID_KEY, ev.getUserId()));*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FailureEventCreation ev) {
        Log.d(TAG, ev.toString());
        new AlertDialog.Builder(getContext())
              .setTitle("Failed to create event")
              .setMessage("Failed.")
              .setNegativeButton(getString(R.string.ok), (dialog, id) -> dialog.cancel())
              .create()
              .show();
    }
}
