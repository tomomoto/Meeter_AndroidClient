package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.event.activity.EventDispatcherActivity.EVENT_ID_KEY;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.createEventLocationMapActivityIntent;
import static com.tom.meeter.context.event.utils.Utils.createUpdateEventRequest;
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
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileEventActivity extends AppCompatActivity {

    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LNG = "extra_lng";

    private static final String TAG = ProfileEventActivity.class.getCanonicalName();

    @Inject
    TokenService tokenService;
    @Inject
    EventService eventService;
    @Inject
    ViewModelFactory viewModelFactory;

    private ActivityEventEditableBinding binding;
    private AccountManager accountManager;
    private EventViewModel eventViewModel;
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
        checkToken((token) -> onInit(token, eventId), this::finish,
              accountManager, this, tokenService);
    }

    private void onInit(String token, String eventId) {
        eventViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(EventViewModel.class);
        eventViewModel.fetchEventInformation(token, eventId, this);
        eventViewModel.getEventLiveData()
              .observe(this, event -> {
                  eventCache = event;
                  String userUuid = AuthHelper.getUserUuid(accountManager);
                  String eventCreatorId = eventCache.getCreatorId();
                  if (userUuid.equals(eventCreatorId)) {
                      initLayout(token);
                      return;
                  }
                  throw new IllegalStateException("Profile event activity for" +
                        " non creator " + userUuid + "/" + eventId + " : " + eventCreatorId);
              });
    }

    private void initLayout(String token) {
        binding = ActivityEventEditableBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.saveEventButton.setOnClickListener(v -> {
            UpdateEventRequest req = createUpdateEventRequest(eventCache, binding);
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
                              updateLayout();
                              showMessage(ProfileEventActivity.this, getString(R.string.saved));
                          }
                      }
                  });
        });
        binding.deleteEventButton.setOnClickListener(v -> showAlertDialog());


        /*
   TODO photoPath;
        * */

        updateLayout();

        binding.selectStartingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.eventStarting));
        binding.selectEndingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.eventEnding));
        binding.btnEventLocationMap.setOnClickListener(
              v -> mapResult.launch(
                    createEventLocationMapActivityIntent(this, eventCache.getId())));
        binding.selectPhotoButton.setOnClickListener(
              v -> showMessage(ProfileEventActivity.this, "Кнопка пока не работает..."));

        eventViewModel.getEventPhotoLiveData()
              .observe(
                    this, photo -> {
                        photoCache = photo;
                        updatePhoto();
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
                                    showMessage(ProfileEventActivity.this, getString(R.string.deleted));
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

    private void updatePhoto() {
        binding.eventPhoto.setImageBitmap(circleImage(photoCache, 600, 600));
    }

    private void updateLayout() {
        binding.eventName.setText(eventCache.getName());
        binding.eventCreated.setText(UI_DATE_TIME_FORMAT.format(eventCache.getCreated()));

        binding.eventDescription.setText(eventCache.getDescription());
        binding.eventLatitude.setText(textOrNull(eventCache.getLatitude()));
        binding.eventLongitude.setText(textOrNull(eventCache.getLongitude()));
        binding.eventStarting.setText(dateOrNull(eventCache.getStarting()));
        binding.eventEnding.setText(dateOrNull(eventCache.getEnding()));
        binding.eventCity.setText(eventCache.getCity());
    }


    @Nullable
    @Override
    public View onCreateView(
          @Nullable View parent, @NonNull String name, @NonNull Context ctx,
          @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, ctx, attrs);
    }


    public static void dispatchToProfileEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createProfileEventActivityIntent(ctx, eventId));
    }

    public static Intent createProfileEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, ProfileEventActivity.class)
              .putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
    }
}
