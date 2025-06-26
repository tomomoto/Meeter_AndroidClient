package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.event.activity.EventDispatcherActivity.EVENT_ID_KEY;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.createEventLocationMapActivityIntent;
import static com.tom.meeter.context.event.utils.Utils.createUpdateEventRequest;
import static com.tom.meeter.context.event.utils.Utils.currentUserIsEventCreator;
import static com.tom.meeter.context.event.utils.Utils.dumpEventDispatcherError;
import static com.tom.meeter.context.image.activity.BaseUploadActivity.PHOTO_PATH_RESULT;
import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_TIME_FORMAT;
import static com.tom.meeter.infrastructure.common.CommonHelper.dateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.textOrNull;
import static com.tom.meeter.infrastructure.common.DateHelper.showDateTimePicker;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.lifecycle.ViewModelProvider;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.event.message.UpdateEventRequest;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.image.activity.UploadEventImageActivity;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ActivityEventEditableBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;

import java.util.Objects;

import javax.inject.Inject;

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
    EventViewModel.AssistedFactory assistedFactory;
    @Inject
    ImageDownloader imgDownloader;

    private ActivityEventEditableBinding binding;
    private AccountManager accountManager;
    private EventViewModel viewModel;
    private ActivityResultLauncher<Intent> mapResult;

    private EventDTO eventCache;
    private Bitmap photoCache;
    private boolean isEditableModeEnabled = false;

    private final ActivityResultLauncher<Intent> imageUploadLauncher =
          registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                        return;
                    }
                    String photoPath = result.getData().getStringExtra(PHOTO_PATH_RESULT);
                    downloadAndUpdateLayoutPhoto(photoPath);
                    binding.photoPath.setText(photoPath);
                });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        viewModel = new ViewModelProvider(
              this,
              EventViewModel.factory(
                    assistedFactory, eventId, token, this, this::recreate))
              .get(EventViewModel.class);

        initLayout(token);

        viewModel.getEvent()
              .observe(this, event -> {
                  if (!currentUserIsEventCreator(accountManager, event)) {
                      dumpEventDispatcherError(TAG, accountManager, event);
                      finish();
                      return;
                  }
                  eventCache = event;
                  updateLayout();
                  viewModel.getEventPhoto()
                        .observe(this, this::updateLayoutPhoto);
              });
    }

    private void initLayout(String token) {
        binding = ActivityEventEditableBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.selectStartingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.starting));
        binding.selectEndingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.ending));
        binding.locationMapButton.setOnClickListener(
              v -> mapResult.launch(
                    createEventLocationMapActivityIntent(this, eventCache.getId())));
        binding.deleteEventButton.setOnClickListener(v -> showAlertDialog());

        binding.editSaveButton.setOnClickListener(v -> {
            if (isEditableModeEnabled) {
                UpdateEventRequest req = createUpdateEventRequest(eventCache, binding);
                if (req.isEmpty()) {
                    showMessage(this, R.string.empty_update_request_is_not_sent);
                    switchEditMode();
                    return;
                }
                eventService.updateEvent(Globals.getAuthHeader(token), eventCache.getId(), req)
                      .enqueue(new BaseOnNotAuthenticatedCallback<>(this, this::recreate) {
                          @Override
                          public void onResponse(Call<EventDTO> call, Response<EventDTO> resp) {
                              super.onResponse(call, resp);
                              if (resp.code() == HttpCodes.OK) {
                                  String oldPhotoPath = eventCache.getPhotoPath();
                                  eventCache = resp.body();
                                  if (!Objects.equals(oldPhotoPath, eventCache.getPhotoPath())) {
                                      downloadAndUpdateLayoutPhoto(eventCache.getPhotoPath());
                                  }
                                  showMessage(ProfileEventActivity.this, R.string.saved);
                              }
                              updateLayout();
                          }
                      });
            }
            switchEditMode();
        });
        binding.selectPhotoButton.setOnClickListener(
              v -> imageUploadLauncher.launch(
                    new Intent(this, UploadEventImageActivity.class)));
    }

    void downloadAndUpdateLayoutPhoto(String photoPath) {
        imgDownloader.downloadEventImage(photoPath, this,
              this::updateLayoutPhoto, ImagesHelper::bigCircleImage,
              this::recreate);
    }

    private void updateLayoutPhoto(Bitmap photo) {
        photoCache = photo;
        binding.photo.setImageBitmap(photoCache);
    }

    private void switchEditMode() {
        isEditableModeEnabled = !isEditableModeEnabled;

        binding.selectPhotoButton.setEnabled(isEditableModeEnabled);
        binding.locationMapButton.setEnabled(isEditableModeEnabled);
        binding.selectStartingDateButton.setEnabled(isEditableModeEnabled);
        binding.selectEndingDateButton.setEnabled(isEditableModeEnabled);

        binding.name.setEnabled(isEditableModeEnabled);
        binding.description.setEnabled(isEditableModeEnabled);
        binding.latitude.setEnabled(isEditableModeEnabled);
        binding.longitude.setEnabled(isEditableModeEnabled);
        binding.starting.setEnabled(isEditableModeEnabled);
        binding.ending.setEnabled(isEditableModeEnabled);
        binding.city.setEnabled(isEditableModeEnabled);
        binding.editSaveButton.setText(isEditableModeEnabled ? R.string.save : R.string.edit);
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

    private void updateLayout() {
        binding.photoPath.setText(eventCache.getPhotoPath());
        binding.name.setText(eventCache.getName());
        binding.eventCreated.setText(UI_DATE_TIME_FORMAT.format(eventCache.getCreated()));

        binding.description.setText(eventCache.getDescription());
        binding.latitude.setText(textOrNull(eventCache.getLatitude()));
        binding.longitude.setText(textOrNull(eventCache.getLongitude()));
        binding.starting.setText(dateOrNull(eventCache.getStarting()));
        binding.ending.setText(dateOrNull(eventCache.getEnding()));
        binding.city.setText(eventCache.getCity());
    }


    @Nullable
    @Override
    public View onCreateView(
          @Nullable View parent, @NonNull String name, @NonNull Context ctx,
          @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, ctx, attrs);
    }

    @Override
    protected void onDestroy() {
        logMethod(TAG, this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        logMethod(TAG, this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        logMethod(TAG, this);
        super.onPause();
    }


    public static void dispatchToProfileEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createProfileEventActivityIntent(ctx, eventId));
    }

    public static Intent createProfileEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, ProfileEventActivity.class)
              .putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
    }
}
