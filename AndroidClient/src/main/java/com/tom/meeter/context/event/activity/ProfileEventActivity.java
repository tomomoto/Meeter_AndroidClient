package com.tom.meeter.context.event.activity;

import static android.view.View.GONE;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getUserUuid;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.EXTRA_LAT;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.EXTRA_LNG;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.createEventLocationMapActivityIntent;
import static com.tom.meeter.context.event.activity.EventOnMapActivity.dispatchToEventOnMapActivity;
import static com.tom.meeter.context.event.activity.PublishEventActivity.createPublishEventActivityIntent;
import static com.tom.meeter.context.event.activity.ScheduleEventActivity.createScheduleEventActivityIntent;
import static com.tom.meeter.context.event.utils.Utils.createUpdateEventRequest;
import static com.tom.meeter.context.event.utils.Utils.currentUserIsEventCreator;
import static com.tom.meeter.context.event.utils.Utils.dumpEventDispatcherError;
import static com.tom.meeter.context.image.activity.BaseUploadActivity.PHOTO_PATH_RESULT;
import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_TIME_FORMAT;
import static com.tom.meeter.infrastructure.common.CommonHelper.dateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.handleEventStatus;
import static com.tom.meeter.infrastructure.common.CommonHelper.resolveStatusAction;
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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.event.factory.EventAssistedFactory;
import com.tom.meeter.context.event.message.UpdateEventRequest;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.image.activity.UploadEventImageActivity;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.component.activity.ProfileActivity;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ActivityEventEditableBinding;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;

import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileEventActivity extends AppCompatActivity {

    private static final String TAG = ProfileEventActivity.class.getCanonicalName();

    @Inject
    TokenService tokenService;
    @Inject
    EventService service;
    @Inject
    EventAssistedFactory assistedFactory;
    @Inject
    ImageDownloader imgDownloader;

    private final Runnable onAuthFail = this::recreate;
    private ActivityEventEditableBinding binding;
    private AccountManager accountManager;
    private EventViewModel viewModel;

    private EventDTO eventCache;
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

    private final ActivityResultLauncher<Intent> mapResult = registerForActivityResult(
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

    private final ActivityResultLauncher<Intent> publishLauncher =
          registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        viewModel.init();
                    }
                }
          );

    private final ActivityResultLauncher<Intent> scheduleLauncher =
          registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        viewModel.init();
                    }
                }
          );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        if (!EventDispatcherActivity.validate(this)) {
            return;
        }

        ((App) getApplication()).getEventComponent().inject(this);

        accountManager = AccountManager.get(this);

        binding = ActivityEventEditableBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken((token) -> onInit(), this::finish,
              accountManager, this, tokenService);
    }

    private void onInit() {
        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(
                    assistedFactory,
                    EventDispatcherActivity.getEventId(this),
                    this, onAuthFail))
              .get(EventViewModel.class);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.init());

        initLayout();

        viewModel.getEvent()
              .observe(this, event -> {
                  if (!currentUserIsEventCreator(accountManager, event)) {
                      dumpEventDispatcherError(TAG, getUserUuid(accountManager), event);
                      finish();
                      return;
                  }
                  binding.swipeRefresh.setRefreshing(false);
                  eventCache = event;
                  updateLayout();
                  viewModel.getEventPhoto()
                        .observe(this, this::updateLayoutPhoto);
              });

        viewModel.getTransitions()
              .observe(this, transitions -> {
                  if (transitions.isEmpty()) {
                      binding.actionsButtonContainer.setVisibility(GONE);
                      return;
                  }
                  binding.actionsButtonContainer.removeAllViews();
                  for (EventDTO.EventStatus t : transitions) {
                      binding.actionsButtonContainer.addView(createStatusButton(t));
                  }
              });
    }

    private void initLayout() {
        binding.selectStartingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.starting));
        binding.selectEndingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.ending));
        binding.locationMapButton.setOnClickListener(
              v -> mapResult.launch(
                    createEventLocationMapActivityIntent(this, eventCache.getId())));
        binding.showOnMapButton.setOnClickListener(
              v -> dispatchToEventOnMapActivity(this, eventCache.getId()));
        binding.deleteEventButton.setOnClickListener(v -> showAlertDialog());

        binding.editSaveButton.setOnClickListener(v -> {
            if (isEditableModeEnabled) {
                UpdateEventRequest req = createUpdateEventRequest(eventCache, binding);
                if (req.isEmpty()) {
                    showMessage(this, R.string.empty_update_request_is_not_sent);
                    switchEditMode();
                    return;
                }
                service.updateEvent(getAuthHeader(accountManager), eventCache.getId(), req)
                      .enqueue(new BaseOnNotAuthenticatedCallback<>(this, onAuthFail) {
                          @Override
                          public void onResponse(
                                Call<EventDTO> call, Response<EventDTO> resp) {
                              super.onResponse(call, resp);
                              if (resp.code() != HttpCodes.OK) {
                                  updateLayout();
                                  return;
                              }
                              String oldPhotoPath = eventCache.getPhotoPath();
                              eventCache = resp.body();
                              if (!Objects.equals(oldPhotoPath, eventCache.getPhotoPath())) {
                                  downloadAndUpdateLayoutPhoto(eventCache.getPhotoPath());
                              }
                              showMessage(ProfileEventActivity.this, R.string.saved);
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
        imgDownloader.downloadEventImage(
              photoPath, this, ImagesHelper::bigCircleImage,
              this::updateLayoutPhoto, onAuthFail);
    }

    private void updateLayoutPhoto(Bitmap photo) {
        binding.photo.setImageBitmap(photo);
    }

    private void switchEditMode() {
        isEditableModeEnabled = !isEditableModeEnabled;

        setButtonsVisibility(isEditableModeEnabled);

        binding.name.setEnabled(isEditableModeEnabled);
        binding.description.setEnabled(isEditableModeEnabled);
        binding.latitude.setEnabled(isEditableModeEnabled);
        binding.longitude.setEnabled(isEditableModeEnabled);
        binding.starting.setEnabled(isEditableModeEnabled);
        binding.ending.setEnabled(isEditableModeEnabled);
        binding.city.setEnabled(isEditableModeEnabled);
        binding.editSaveButton.setText(isEditableModeEnabled ? R.string.save : R.string.edit);
    }

    private void setButtonsVisibility(boolean isEditableModeEnabled) {
        int onEdit = isEditableModeEnabled ? View.VISIBLE : View.GONE;
        binding.locationMapButton.setVisibility(onEdit);
        binding.selectPhotoButton.setVisibility(onEdit);
        binding.photoPath.setVisibility(onEdit);
        binding.selectStartingDateButton.setVisibility(onEdit);
        binding.selectEndingDateButton.setVisibility(onEdit);

        int onRead = isEditableModeEnabled ? View.GONE : View.VISIBLE;
        binding.showOnMapButton.setVisibility(onRead);
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
              .setTitle(R.string.delete_event)
              .setMessage(R.string.are_you_sure_delete_event)
              .setPositiveButton(R.string.delete, (dialog, which) -> {
                  service.deleteEvent(getAuthHeader(accountManager), eventCache.getId())
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
        handleEventStatus(this, binding.status, eventCache.getStatus());
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

    private Button createStatusButton(EventDTO.EventStatus status) {
        String auth = getAuthHeader(accountManager);
        String eventId = eventCache.getId();
        Button btn = new Button(this);
        btn.setText(resolveStatusAction(this, status));
        btn.setLayoutParams(new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT));
        switch (status) {
            case PUBLISHED:
                btn.setOnClickListener(
                      v -> publishLauncher.launch(
                            createPublishEventActivityIntent(
                                  this, eventId)));
                break;
            case SCHEDULED:
                btn.setOnClickListener(
                      v -> scheduleLauncher.launch(
                            createScheduleEventActivityIntent(
                                  this, eventId)));
                break;
            case UNPUBLISHED:
                btn.setOnClickListener(
                      v -> showConfirmStatusChangeDialog(
                            this,
                            EventDTO.EventStatus.UNPUBLISHED,
                            () -> service.unpublishEvent(auth, eventId)
                                  .enqueue(refreshCallback)));
                break;
            case STARTED:
                btn.setOnClickListener(
                      v -> showConfirmStatusChangeDialog(
                            this,
                            EventDTO.EventStatus.STARTED,
                            () -> service.startEvent(auth, eventId)
                                  .enqueue(refreshCallback)));
                break;
            case PAUSED:
                btn.setOnClickListener(
                      v -> showConfirmStatusChangeDialog(
                            this,
                            EventDTO.EventStatus.PAUSED,
                            () -> service.pauseEvent(auth, eventId)
                                  .enqueue(refreshCallback)));
                break;
            case RESUMED:
                btn.setOnClickListener(
                      v -> showConfirmStatusChangeDialog(
                            this,
                            EventDTO.EventStatus.RESUMED,
                            () -> service.resumeEvent(auth, eventId)
                                  .enqueue(refreshCallback)));
                break;
            case FINISHED:
                btn.setOnClickListener(
                      v -> showConfirmStatusChangeDialog(
                            this,
                            EventDTO.EventStatus.FINISHED,
                            () -> service.finishEvent(auth, eventId)
                                  .enqueue(refreshCallback)));
                break;
            case CANCELLED:
                btn.setOnClickListener(
                      v -> showConfirmStatusChangeDialog(
                            this,
                            EventDTO.EventStatus.CANCELLED,
                            () -> service.cancelEvent(auth, eventId)
                                  .enqueue(refreshCallback)));
                break;
            case ARCHIVED:
                btn.setOnClickListener(
                      v -> showConfirmStatusChangeDialog(
                            this,
                            EventDTO.EventStatus.ARCHIVED,
                            () -> service.archiveEvent(auth, eventId)
                                  .enqueue(refreshCallback)));
                break;
            default:
                throw new IllegalStateException("Wrong status: " + status);
        }
        return btn;
    }

    private void showConfirmStatusChangeDialog(
          Context ctx, EventDTO.EventStatus newStatus, Runnable onConfirmed) {
        String message = "Вы точно хотите выполнить действие?\n\n"
              + resolveStatusAction(ctx, newStatus);

        new AlertDialog.Builder(ctx)
              .setTitle("Подтвердите действие")
              .setMessage(message)
              .setPositiveButton("Да", (dialog, which) -> {
                  dialog.dismiss();
                  onConfirmed.run();
              })
              .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
              .show();
    }

    private final BaseOnNotAuthenticatedCallback<EventDTO> refreshCallback
          = new BaseOnNotAuthenticatedCallback<>(this, onAuthFail) {
        @Override
        public void onResponse(Call<EventDTO> call, Response<EventDTO> resp) {
            super.onResponse(call, resp);
            if (resp.isSuccessful()) {
                viewModel.init();
            }
        }
    };


    public static void dispatchToProfileEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createProfileEventActivityIntent(ctx, eventId));
    }

    public static Intent createProfileEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, ProfileEventActivity.class)
              .putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
    }
}
