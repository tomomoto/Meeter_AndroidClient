package com.example.tom.meeter.context.fragments;

import android.location.Location;
import android.os.Bundle;
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

import com.example.tom.meeter.context.gps.service.GPSTrackerService;
import com.example.tom.meeter.R;
import com.example.tom.meeter.context.network.domain.CreateNewEventAttempt;
import com.example.tom.meeter.context.network.domain.FailureEventCreation;
import com.example.tom.meeter.context.network.domain.SuccessfulEventCreation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;
import static java.text.DateFormat.getDateInstance;

/**
 * Created by Tom on 14.12.2016.
 */
public class CreateNewEventFragment extends Fragment {

  private static final String TAG = CreateNewEventFragment.class.getCanonicalName();
  private static final String EMPTY_TEXT = "";

  private GPSTrackerService gpsTrackerService;

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

  @BindView(R.id.newEventStartsEditText)
  EditText starts;

  @BindView(R.id.newEventEndsEditText)
  EditText ends;

  @BindView(R.id.newEventCurrentDateBtn)
  Button currentDate;

  @BindView(R.id.newEventOtherDateBtn)
  Button otherDate;

  @BindView(R.id.newEventDateTextView)
  TextView dateValidity;

  @BindView(R.id.newEventCreateBtn)
  Button createEvent;


  public CreateNewEventFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    gpsTrackerService = new GPSTrackerService(getContext());
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_new_event, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
    Log.d(TAG, "Event bus registered...");
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
    Log.d(TAG, "Event bus unregistered...");
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @OnClick(R.id.newEventCurrentPlaceBtn)
  public void currentPlaceClickHandler(Button button) {
    Location location = gpsTrackerService.getLastKnownLocation();
    latitude.setText(String.valueOf(location.getLatitude()));
    longitude.setText(String.valueOf(location.getLongitude()));
  }

  @OnClick(R.id.newEventOtherPlaceBtn)
  public void otherPlaceClickHandler(Button button) {
    //TODO: Implement otherPlaceClickHandler
  }

  @OnClick(R.id.newEventCurrentDateBtn)
  public void currentDateClickHandler(Button button) {
    starts.setText(getDateInstance().toString());
  }

  @OnClick(R.id.newEventOtherDateBtn)
  public void otherDateClickHandler(Button button) {
    //TODO: Implement otherDateClickHandler
    starts.setText(getDateInstance().toString());
  }

  @OnClick(R.id.newEventCreateBtn)
  public void createEventClickHandler(Button button) {
    EventBus.getDefault().post(new CreateNewEventAttempt(name.getText().toString(), description.getText().toString(),
        starts.getText().toString(), ends.getText().toString(), Double.valueOf(latitude.getText().toString()),
        Double.valueOf(longitude.getText().toString())));
  }

  @OnTextChanged(
      value = {R.id.newEventLatitudeEditText, R.id.newEventLongitudeEditText, R.id.newEventNameEditText},
      callback = AFTER_TEXT_CHANGED
  )
  public void locationChanges(Editable text) {
    CharSequence name = this.name.getText();
    CharSequence latitude = this.latitude.getText();
    CharSequence longitude = this.longitude.getText();

    if (name == null || EMPTY_TEXT.equals(name.toString())
        || latitude == null || EMPTY_TEXT.equals(latitude.toString())
        || longitude == null || EMPTY_TEXT.equals(longitude.toString())) {
      createEvent.setEnabled(false);
    } else {
      validateWholeForm();
    }
  }

  private void validateWholeForm() {
    if (name.getText() != null && !EMPTY_TEXT.equals(name.getText().toString())
        && latitude.getText() != null && !EMPTY_TEXT.equals(latitude.getText().toString())
        && longitude.getText() != null && !EMPTY_TEXT.equals(longitude.getText().toString())
        && isDateValid(starts.getText().toString()) && isDateValid(ends.getText().toString())) {
      createEvent.setEnabled(true);
    }
  }

  @OnTextChanged(value = {R.id.newEventStartsEditText, R.id.newEventEndsEditText}, callback = AFTER_TEXT_CHANGED)
  public void dateChangedListener(Editable text) {
    String s = starts.getText().toString();
    String e = ends.getText().toString();
    Log.d(TAG, s + " " + s);
    if (!isDateValid(s) || !isDateValid(e)) {
      dateValidity.setText("Invalid date");
      createEvent.setEnabled(false);
    } else {
      dateValidity.setText("Date is valid");
      validateWholeForm();
    }
  }

  private boolean isDateValid(String date) {
    try {
      Calendar.getInstance().setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
    } catch (ParseException e) {
      return false;
    }
    return true;
  }


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(SuccessfulEventCreation ev) {
    Log.d(TAG, ev.toString());
    new AlertDialog.Builder(getContext())
        .setTitle("Event created, id: " + ev.getEventId())
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
