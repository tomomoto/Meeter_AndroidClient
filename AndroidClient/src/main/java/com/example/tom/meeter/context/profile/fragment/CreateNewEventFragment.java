package com.example.tom.meeter.context.profile.fragment;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.gps.service.LocationTrackerService;
import com.example.tom.meeter.context.network.domain.CreateNewEventAttempt;
import com.example.tom.meeter.infrastructure.eventbus.events.FailureEventCreation;
import com.example.tom.meeter.infrastructure.eventbus.events.SuccessfulEventCreation;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Tom on 14.12.2016.
 */
public class CreateNewEventFragment extends Fragment {

    private static final String TAG = CreateNewEventFragment.class.getCanonicalName();
    private static final String EMPTY_STR = "";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @BindView(R.id.newEventNameEditText)
    EditText name;

    @BindView(R.id.newEventDescriptionEditText)
    EditText description;

    @BindView(R.id.newEventLatitudeEditText)
    EditText latitude;

    @BindView(R.id.newEventLongitudeEditText)
    EditText longitude;

    @BindView(R.id.newEventCurrentPlaceBtn)
    Button currentPlace;

    @BindView(R.id.newEventOtherPlaceBtn)
    Button otherPlace;

    @BindView(R.id.newEventStartsDateEditText)
    EditText startsDateEditText;

    @BindView(R.id.newEventStartsDateTextView)
    TextView startsDateValidity;

    @BindView(R.id.newEventStartsCurrentDateBtn)
    Button currentDateToStartingDate;

    @BindView(R.id.newEventStartsOtherDateBtn)
    Button otherDateToStartingDate;

    @BindView(R.id.newEventStartsTimeEditText)
    EditText startsTimeEditText;

    @BindView(R.id.newEventStartsCurrentTimeBtn)
    Button startsCurrentTimeBtn;

    @BindView(R.id.newEventStartsOtherTimeBtn)
    Button startsOtherTimeBtn;

    @BindView(R.id.newEventEndsDateEditText)
    EditText endsDateEditText;

    @BindView(R.id.newEventEndsDateTextView)
    TextView endsDateValidity;

    @BindView(R.id.newEventEndsCurrentDateBtn)
    Button endsCurrentDateBtn;

    @BindView(R.id.newEventEndsOtherDateBtn)
    Button endsOtherDateBtn;

    @BindView(R.id.newEventEndsTimeEditText)
    EditText endsTimeEditText;

    @BindView(R.id.newEventEndsCurrentTimeBtn)
    Button endsCurrentTimeBtn;

    @BindView(R.id.newEventEndsOtherTimeBtn)
    Button endsOtherTimeBtn;

    @BindView(R.id.newEventCreateBtn)
    Button createEventBtn;

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
        View view = inflater.inflate(R.layout.fragment_new_event, container, false);
        ButterKnife.bind(this, view);
        return view;
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

    @OnClick(R.id.newEventCurrentPlaceBtn)
    public void currentPlaceClickHandler(Button button) {
        Location location = locationService.getLastKnownLocation();
        if (location != null) {
            latitude.setText(String.valueOf(location.getLatitude()));
            longitude.setText(String.valueOf(location.getLongitude()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode = " + requestCode + ", resultCode = " + resultCode);
    }

    @OnClick(R.id.newEventOtherPlaceBtn)
    public void otherPlaceClickHandler(Button button) {
        //TODO: Implement otherPlaceClickHandler
    }

    @OnClick(R.id.newEventStartsCurrentDateBtn)
    public void startsCurrentDateClickHandler(Button button) {
        startsDateEditText.setText(DATE_FORMAT.format(new Date()));
    }

    @OnClick(R.id.newEventStartsOtherDateBtn)
    public void startsOtherDateClickHandler(Button button) {
        //TODO: Implement otherDateClickHandler
        startsDateEditText.setText(null);
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.newEventStartsCurrentTimeBtn)
    public void startsCurrentTimeClickHandler(Button button) {
        startsTimeEditText.setText(TIME_FORMAT.format(LocalTime.now()));
    }

    @OnClick(R.id.newEventStartsOtherTimeBtn)
    public void startsOtherTimeClickHandler(Button button) {
        //TODO:
        startsTimeEditText.setText(null);
    }

    @OnClick(R.id.newEventEndsCurrentDateBtn)
    public void endsCurrentDateClickHandler(Button button) {
        endsDateEditText.setText(DATE_FORMAT.format(new Date()));
    }

    @OnClick(R.id.newEventEndsOtherDateBtn)
    public void endsOtherDateClickHandler(Button button) {
        //TODO: Implement otherDateClickHandler
        endsDateEditText.setText(null);
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.newEventEndsCurrentTimeBtn)
    public void endsCurrentTimeClickHandler(Button button) {
        endsTimeEditText.setText(TIME_FORMAT.format(LocalTime.now()));
    }

    @OnClick(R.id.newEventEndsOtherTimeBtn)
    public void endsOtherTimeClickHandler(Button button) {
        //TODO:
        endsTimeEditText.setText(null);
    }

    @OnClick(R.id.newEventCreateBtn)
    public void createEventClickHandler(Button button) {
        String startDate = startsDateEditText.getText().toString();
        String startTime = startsTimeEditText.getText().toString();
        LocalDate localStartDate = LocalDate.parse(startDate);
        LocalTime localStartTime = LocalTime.parse(startTime);

        String endDate = endsDateEditText.getText().toString();
        String endTime = endsTimeEditText.getText().toString();
        LocalDate localEndDate = LocalDate.parse(endDate);
        LocalTime localEndTime = LocalTime.parse(endTime);

        ZoneOffset offset = OffsetDateTime.now().getOffset();
        OffsetDateTime starts = OffsetDateTime.of(localStartDate, localStartTime, offset);
        OffsetDateTime ends = OffsetDateTime.of(localEndDate, localEndTime, offset);
        EventBus.getDefault()
              .post(new CreateNewEventAttempt(
                    name.getText().toString(), description.getText().toString(),
                    starts, ends,
                    Float.valueOf(latitude.getText().toString()),
                    Float.valueOf(longitude.getText().toString())));
    }

    @OnTextChanged(
          value = {
                R.id.newEventLatitudeEditText,
                R.id.newEventLongitudeEditText,
                R.id.newEventNameEditText
          },
          callback = AFTER_TEXT_CHANGED)
    public void locationChanges(Editable text) {
        CharSequence name = this.name.getText();
        CharSequence latitude = this.latitude.getText();
        CharSequence longitude = this.longitude.getText();

        if (requiredFieldsNotProvided(name, latitude, longitude)) {
            createEventBtn.setEnabled(false);
        } else {
            validateWholeForm();
        }
    }

    private static boolean requiredFieldsNotProvided(
          CharSequence name, CharSequence latitude, CharSequence longitude) {
        return name == null || EMPTY_STR.equals(name.toString())
              || latitude == null || EMPTY_STR.equals(latitude.toString())
              || longitude == null || EMPTY_STR.equals(longitude.toString());
    }

    private void validateWholeForm() {
        if (allSet()) {
            createEventBtn.setEnabled(true);
        }
    }

    private boolean allSet() {
        return name.getText() != null && !EMPTY_STR.equals(name.getText().toString())
              && latitude.getText() != null && !EMPTY_STR.equals(latitude.getText().toString())
              && longitude.getText() != null && !EMPTY_STR.equals(longitude.getText().toString())
              && isDateValid(startsDateEditText.getText().toString()) && isDateValid(endsDateEditText.getText().toString());
    }

    @OnTextChanged(value = R.id.newEventStartsDateEditText, callback = AFTER_TEXT_CHANGED)
    public void startsDateChangedListener(Editable text) {
        String s = startsDateEditText.getText().toString();
        if (!isDateValid(s)) {
            startsDateValidity.setText(getString(R.string.wrong_date));
            createEventBtn.setEnabled(false);
            return;
        }
        startsDateValidity.setText(getString(R.string.correct_date));
        validateWholeForm();
    }

    @OnTextChanged(value = R.id.newEventEndsDateEditText, callback = AFTER_TEXT_CHANGED)
    public void endsDateChangedListener(Editable text) {
        String e = endsDateEditText.getText().toString();
        if (!isDateValid(e)) {
            endsDateValidity.setText(getString(R.string.wrong_date));
            createEventBtn.setEnabled(false);
            return;
        }
        endsDateValidity.setText(getString(R.string.correct_date));
        validateWholeForm();
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
