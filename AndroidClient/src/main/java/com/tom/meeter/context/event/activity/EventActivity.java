package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.createEventLocationMapActivityIntent;
import static com.tom.meeter.context.event.activity.EventOnMapActivity.dispatchToEventOnMapActivity;
import static com.tom.meeter.context.event.utils.Utils.createUpdateEventRequest;
import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_TIME_FORMAT;
import static com.tom.meeter.infrastructure.common.CommonHelper.dateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.textOrNull;
import static com.tom.meeter.infrastructure.common.DateHelper.showDateTimePicker;
import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewbinding.ViewBinding;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.event.message.UpdateEventRequest;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ActivityEventEditableBinding;
import com.tom.meeter.databinding.ActivityEventReadableBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    public static final String EVENT_ID_KEY = "event_id";
    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LNG = "extra_lng";

    private static final String TAG = EventActivity.class.getCanonicalName();

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
            UpdateEventRequest req = createUpdateEventRequest(eventCache, eBinding);
            if (req.isEmpty()) {
                showMessage(this, getString(R.string.empty_update_request_is_not_sent));
                return;
            }
            eventService.updateEvent(Globals.getAuthHeader(token), eventCache.getId(), req).enqueue(
                  new HttpErrorLogger<>(getApplicationContext()) {
                      @Override
                      public void onResponse(Call<EventDTO> call, Response<EventDTO> res) {
                          super.onResponse(call, res);
                          if (res.isSuccessful()) {
                              eventCache = res.body();
                              updateEditableLayout();
                              showMessage(EventActivity.this, getString(R.string.saved));
                          }
                      }
                  });
        });
        eBinding.deleteEventButton.setOnClickListener(v -> showAlertDialog());


        /*
   TODO photoPath;
        * */

        updateEditableLayout();

        eBinding.selectStartingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, eBinding.eventStarting));
        eBinding.selectEndingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, eBinding.eventEnding));
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

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
              .setTitle(R.string.delete_event)
              .setMessage(R.string.are_you_sure_delete_event)
              .setPositiveButton(R.string.delete, (dialog, which) -> {
                  eventService.deleteEvent(getAuthHeader(accountManager), eventCache.getId())
                        .enqueue(new HttpErrorLogger<>(getApplicationContext()) {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> resp) {
                                super.onResponse(call, resp);
                                if (resp.isSuccessful()) {
                                    showMessage(EventActivity.this, getString(R.string.deleted));
                                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                    finish();
                                }
                            }
                        });
                  dialog.dismiss();
              })
              .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
              .show();
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
    @Override
    public View onCreateView(
          @Nullable View parent, @NonNull String name, @NonNull Context ctx,
          @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, ctx, attrs);
    }


    public static void dispatchToEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createEventActivityIntent(ctx, eventId));
    }

    public static Intent createEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, EventActivity.class)
              .putExtra(EventActivity.EVENT_ID_KEY, eventId);
    }
}
