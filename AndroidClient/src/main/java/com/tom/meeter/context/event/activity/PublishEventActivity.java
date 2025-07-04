package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getUserUuid;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.EXTRA_LAT;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.EXTRA_LNG;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.createEventLocationMapActivityIntent;
import static com.tom.meeter.context.event.utils.Utils.createPublishEventRequest;
import static com.tom.meeter.context.event.utils.Utils.currentUserIsEventCreator;
import static com.tom.meeter.context.event.utils.Utils.dumpEventDispatcherError;
import static com.tom.meeter.context.image.activity.BaseUploadActivity.PHOTO_PATH_RESULT;
import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_TIME_FORMAT;
import static com.tom.meeter.infrastructure.common.CommonHelper.dateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.handleEventStatus;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ActivityEventPublishBinding;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class PublishEventActivity extends AppCompatActivity {

    private static final String TAG = PublishEventActivity.class.getCanonicalName();

    @Inject
    TokenService tokenService;
    @Inject
    EventService service;
    @Inject
    EventAssistedFactory assistedFactory;
    @Inject
    ImageDownloader imgDownloader;

    private final Runnable onNotAuthenticated = this::recreate;
    private ActivityEventPublishBinding binding;
    private AccountManager accountManager;
    private EventViewModel viewModel;
    private EventDTO eventCache;

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
    ;

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

        logMethod(TAG, this);

        if (EventDispatcherActivity.isIncorrect(this)) {
            return;
        }

        binding = ActivityEventPublishBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ((App) getApplication()).getEventComponent().inject(this);

        accountManager = AccountManager.get(this);
        //setToken(accountManager, Launcher.EXPIRED);
        checkToken((token) -> onInit(), this::finish, this, tokenService);
    }

    private void onInit() {
        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(
                    assistedFactory,
                    EventDispatcherActivity.getEventId(this),
                    this, onNotAuthenticated))
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
    }

    private void initLayout() {
        binding.selectStartingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.starting));
        binding.selectEndingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.ending));
        binding.locationMapButton.setOnClickListener(
              v -> mapResult.launch(
                    createEventLocationMapActivityIntent(this, eventCache.getId())));

        binding.publishButton.setOnClickListener(v -> {
            UpdateEventRequest req = createPublishEventRequest(eventCache, binding);
            service.publishEvent(getAuthHeader(accountManager), eventCache.getId(), req)
                  .enqueue(new BaseOnNotAuthenticatedCallback<>(this, onNotAuthenticated) {
                      @Override
                      public void onResponse(
                            Call<EventDTO> call, Response<EventDTO> resp) {
                          super.onResponse(call, resp);
                          if (resp.code() != HttpCodes.OK) {
                              showMessage(PublishEventActivity.this,
                                    "Unable to publish the event...");
                              updateLayout();
                              return;
                          }
                          String oldPhotoPath = eventCache.getPhotoPath();
                          eventCache = resp.body();
                          if (!Objects.equals(oldPhotoPath, eventCache.getPhotoPath())) {
                              downloadAndUpdateLayoutPhoto(eventCache.getPhotoPath());
                          }
                          showMessage(PublishEventActivity.this, R.string.published);
                          setResult(RESULT_OK);
                          finish();
                      }
                  });
        });
        binding.selectPhotoButton.setOnClickListener(
              v -> imageUploadLauncher.launch(
                    new Intent(this, UploadEventImageActivity.class)));
    }

    void downloadAndUpdateLayoutPhoto(String photoPath) {
        imgDownloader.downloadEventImage(
              photoPath, this, ImagesHelper::bigCircleImage,
              this::updateLayoutPhoto, onNotAuthenticated);
    }

    private void updateLayoutPhoto(Bitmap photo) {
        binding.photo.setImageBitmap(photo);
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

    public static Intent createPublishEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, PublishEventActivity.class)
              .putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
    }
}
