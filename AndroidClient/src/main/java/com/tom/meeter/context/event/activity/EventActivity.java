package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.createEventLocationMapActivityIntent;
import static com.tom.meeter.context.event.activity.EventOnMapActivity.dispatchToEventOnMapActivity;
import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.EMPTY_STR;
import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.tom.meeter.App;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.event.message.UpdateEventRequest;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ActivityEventEditableBinding;
import com.tom.meeter.databinding.ActivityEventReadableBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.ErrorLogger;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    public static final String EVENT_ID_KEY = "event_id";
    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LNG = "extra_lng";

    private static final String TAG = EventActivity.class.getCanonicalName();

    private static final DateTimeFormatter UI_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    ViewBinding binding;
    @Inject
    TokenService tokenService;
    @Inject
    EventService eventService;
    @Inject
    ViewModelFactory viewModelFactory;
    private EventViewModel eventViewModel;
    private AccountManager accountManager;

    private ActivityResultLauncher<Intent> mapResult;
    private EventDTO eventCache;
    private ResponseBody photoCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapResult = registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
              result -> {
                  if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                      double lat = result.getData().getDoubleExtra(EXTRA_LAT, 0.0);
                      double lng = result.getData().getDoubleExtra(EXTRA_LNG, 0.0);
                      if (binding instanceof ActivityEventEditableBinding eBinding) {
                          eBinding.eventLatitude.setText(String.valueOf(lat));
                          eBinding.eventLongitude.setText(String.valueOf(lng));
                      }
                  }
              });

        logMethod(TAG, this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d(TAG, "Unable to create event activity without extras.");
            finish();
            return;
        }
        String eventId = extras.getString(EVENT_ID_KEY);
        if (eventId == null) {
            Log.d(TAG, "Unable to create event activity without 'event_id' provided.");
            finish();
            return;
        }

        ((App) getApplication()).getEventComponent().inject(this);
        accountManager = AccountManager.get(this);

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken((token) -> onInit(token, eventId), this::finish, accountManager, this, tokenService);
    }

    private void onInit(String token, String eventId) {
        eventViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(EventViewModel.class);
        eventViewModel.fetchEventInformation(token, eventId, this);
        eventViewModel.getEventLiveData()
              .observe(this, event -> {
                  eventCache = event;
                  if (AuthHelper.getUserUuid(accountManager).equals(eventCache.getCreatorId())) {
                      initEditableLayout(token);
                  } else {
                      initReadableLayout();
                  }
              });
    }

    private void initReadableLayout() {
        binding = ActivityEventReadableBinding.inflate(getLayoutInflater());
        ActivityEventReadableBinding rBinding = (ActivityEventReadableBinding) binding;
        View view = rBinding.getRoot();
        setContentView(view);

        updateReadableLayout();
        rBinding.eventCreator.setOnClickListener(
              v -> dispatchToUserActivity(this, eventCache.getCreatorId()));
        rBinding.btnEventLocationMap.setOnClickListener(
              v -> dispatchToEventOnMapActivity(this, eventCache.getId()));


        eventViewModel.getEventPhotoLiveData()
              .observe(
                    this, photo -> rBinding.eventPhoto.setImageBitmap(
                          circleImage(photo, 600, 600)));
    }

    private void initEditableLayout(String token) {
        binding = ActivityEventEditableBinding.inflate(getLayoutInflater());
        ActivityEventEditableBinding eBinding = (ActivityEventEditableBinding) binding;
        View view = eBinding.getRoot();
        setContentView(view);

        eBinding.saveEventButton.setOnClickListener(v -> {
            UpdateEventRequest req = new UpdateEventRequest();
            String eventNameChange = getStringOrNull(eBinding.eventName.getText());
            if (!Objects.equals(eventCache.getName(), eventNameChange)) {
                req.setName(eventNameChange);
            }
            String eventDescrChange = getStringOrNull(eBinding.eventDescription.getText());
            if (!Objects.equals(eventCache.getDescription(), eventDescrChange)) {
                req.setDescription(eventDescrChange);
            }
            OffsetDateTime eventStartingChange = getOffsetDateTime(eBinding.eventStarting.getText());
            if (!Objects.equals(eventCache.getStarting(), eventStartingChange)) {
                req.setStarting(eventStartingChange);
            }
            OffsetDateTime eventEndingChange = getOffsetDateTime(eBinding.eventEnding.getText());
            if (!Objects.equals(eventCache.getEnding(), eventEndingChange)) {
                req.setEnding(eventEndingChange);
            }
            String eventCityChange = getStringOrNull(eBinding.eventCity.getText());
            if (!Objects.equals(eventCache.getCity(), eventCityChange)) {
                req.setCity(eventCityChange);
            }
            Float eventLatitudeChange = getFloatOrNull(eBinding.eventLatitude.getText());
            if (!Objects.equals(eventCache.getLatitude(), eventLatitudeChange)) {
                req.setLatitude(eventLatitudeChange);
            }
            Float eventLongitudeChange = getFloatOrNull(eBinding.eventLongitude.getText());
            if (!Objects.equals(eventCache.getLongitude(), eventLongitudeChange)) {
                req.setLongitude(eventLongitudeChange);
            }
            //TODO: eventCache.getPhotoPath();
            eventService.updateEvent(Globals.getAuthHeader(token), eventCache.getId(), req).enqueue(
                  new ErrorLogger<>(this) {
                      @Override
                      public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                          int code = response.code();
                          EventDTO body = response.body();
                          if (response.isSuccessful()) {
                              Log.d(TAG, code + " " + body);
                              eventCache = body;
                              updateEditableLayout();
                              showMessage(EventActivity.this, "Saved.");
                          } else {
                              try {
                                  String msg = code + "/" + response.errorBody().string();
                                  showMessage(EventActivity.this, msg);
                                  Log.d(TAG, msg);
                              } catch (IOException e) {
                                  Log.d(TAG, "Unable to get response error body...");
                              }
                          }
                      }
                  });
        });


        /*
   TODO photoPath;
        * */

        updateEditableLayout();

        eBinding.selectStartingDateButton.setOnClickListener(
              v -> showDateTimePicker(eBinding.eventStarting));
        eBinding.selectEndingDateButton.setOnClickListener(
              v -> showDateTimePicker(eBinding.eventEnding));
        eBinding.btnEventLocationMap.setOnClickListener(
              v -> mapResult.launch(
                    createEventLocationMapActivityIntent(this, eventCache.getId())));
        eBinding.selectPhotoButton.setOnClickListener(
              v -> showMessage(EventActivity.this, "Кнопка пока не работает..."));

        eventViewModel.getEventPhotoLiveData()
              .observe(
                    this, photo -> {
                        photoCache = photo;
                        updateEditablePhoto();
                    });
    }

    private void updateEditablePhoto() {
        if (binding instanceof ActivityEventEditableBinding eBinding) {
            eBinding.eventPhoto.setImageBitmap(circleImage(photoCache, 600, 600));
        }
    }

    private void updateEditableLayout() {
        ActivityEventEditableBinding eBinding = (ActivityEventEditableBinding) binding;
        eBinding.eventName.setText(eventCache.getName());
        eBinding.eventCreated.setText(UI_DATE_TIME_FORMAT.format(eventCache.getCreated()));

        eBinding.eventDescription.setText(eventCache.getDescription());
        eBinding.eventLatitude.setText(textOrNull(eventCache.getLatitude()));
        eBinding.eventLongitude.setText(textOrNull(eventCache.getLongitude()));
        eBinding.eventStarting.setText(dateOrNull(eventCache.getStarting()));
        eBinding.eventEnding.setText(dateOrNull(eventCache.getEnding()));
        eBinding.eventCity.setText(eventCache.getCity());

    }

    private void updateReadableLayout() {
        ActivityEventReadableBinding rBinding = (ActivityEventReadableBinding) binding;
        rBinding.eventName.setText(eventCache.getName());
        rBinding.eventCreated.setText(UI_DATE_TIME_FORMAT.format(eventCache.getCreated()));
        rBinding.eventDescription.setText(eventCache.getDescription());
        rBinding.eventLatitude.setText(textOrNull(eventCache.getLatitude()));
        rBinding.eventLongitude.setText(textOrNull(eventCache.getLongitude()));
        rBinding.eventStarting.setText(dateOrNull(eventCache.getStarting()));
        rBinding.eventEnding.setText(dateOrNull(eventCache.getEnding()));
        rBinding.eventCity.setText(eventCache.getCity());
    }

    @Nullable
    private static CharSequence dateOrNull(OffsetDateTime date) {
        return date == null ? null : UI_DATE_TIME_FORMAT.format(date);
    }

    @Nullable
    private static CharSequence textOrNull(Double val) {
        return val == null ? null : val.toString();
    }

    private static String getStringOrNull(CharSequence input) {
        if (input == null || EMPTY_STR.contentEquals(input)) {
            return null;
        }
        return input.toString();
    }

    private static Float getFloatOrNull(CharSequence input) {
        if (input == null || EMPTY_STR.contentEquals(input)) {
            return null;
        }
        return Float.valueOf(input.toString());
    }

    private static OffsetDateTime getOffsetDateTime(CharSequence input) {
        if (input == null || EMPTY_STR.contentEquals(input)) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(input, UI_DATE_TIME_FORMAT);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.toOffsetDateTime();
    }


    @Nullable
    @Override
    public View onCreateView(
          @Nullable View parent, @NonNull String name, @NonNull Context ctx,
          @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, ctx, attrs);
    }

    // Метод для отображения DatePickerDialog
    private void showDatePickerDialog(final EditText targetEditText) {
        // Получаем текущую дату
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Создаем и показываем DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
              (view, selectedYear, selectedMonth, selectedDay) -> {
                  // Устанавливаем выбранную дату в EditText
                  String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                  targetEditText.setText(selectedDate);
              }, year, month, day);

        // Показываем диалог
        datePickerDialog.show();
    }

    // Метод для отображения Material DatePicker
    private void showMaterialDatePicker(final EditText targetEditText) {
        // Создаём constraints (ограничения для выбора даты)
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        Calendar calendar = Calendar.getInstance();
        constraintsBuilder.setValidator(DateValidatorPointForward.from(calendar.getTimeInMillis()));

        // Создаем Material DatePicker
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setCalendarConstraints(constraintsBuilder.build());
        builder.setTitleText("Select Date");

        MaterialDatePicker<Long> datePicker = builder.build();

        // Устанавливаем слушатель на выбор даты
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Форматируем выбранную дату
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selection);
            String selectedDateString = selectedDate.get(Calendar.DAY_OF_MONTH) + "/" +
                  (selectedDate.get(Calendar.MONTH) + 1) + "/" +
                  selectedDate.get(Calendar.YEAR);

            // Устанавливаем выбранную дату в поле
            targetEditText.setText(selectedDateString);
        });

        // Показываем диалог
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
    }

    private void showDateTimePicker(EditText target) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
              this,
              (view, year, month, dayOfMonth) -> {
                  calendar.set(Calendar.YEAR, year);
                  calendar.set(Calendar.MONTH, month);
                  calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                  TimePickerDialog timePickerDialog = new TimePickerDialog(
                        this,
                        (timeView, hourOfDay, minute) -> {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            SimpleDateFormat sdf = new SimpleDateFormat(
                                  "yyyy-MM-dd HH:mm", Locale.getDefault());
                            String formatted = sdf.format(calendar.getTime());
                            target.setText(formatted);
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                  );

                  timePickerDialog.show();
              },
              calendar.get(Calendar.YEAR),
              calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    public static void dispatchToEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createEventActivityIntent(ctx, eventId));
    }

    public static Intent createEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, EventActivity.class)
              .putExtra(EventActivity.EVENT_ID_KEY, eventId);
    }
}
