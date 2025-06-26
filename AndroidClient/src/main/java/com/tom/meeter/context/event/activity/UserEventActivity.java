package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.event.activity.EventOnMapActivity.dispatchToEventOnMapActivity;
import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_TIME_FORMAT;
import static com.tom.meeter.infrastructure.common.CommonHelper.dateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.textOrNull;
import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tom.meeter.App;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ActivityEventReadableBinding;

import javax.inject.Inject;

public class UserEventActivity extends AppCompatActivity {

    private static final String TAG = UserEventActivity.class.getCanonicalName();

    @Inject
    TokenService tokenService;
    @Inject
    EventService eventService;
    @Inject
    EventViewModel.EventViewModelAssistedFactory factory;

    ActivityEventReadableBinding binding;
    private EventViewModel viewModel;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d(TAG, "Unable to create event activity without extras.");
            finish();
            return;
        }
        String eventId = extras.getString(EventDispatcherActivity.EVENT_ID_KEY);
        if (eventId == null) {
            Log.d(TAG, "Unable to create event activity without 'event_id' provided.");
            finish();
            return;
        }

        ((App) getApplication()).getEventComponent().inject(this);
        accountManager = AccountManager.get(this);

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken((token) -> onInit(token, eventId),
              this::finish, accountManager, this, tokenService);
    }

    private void onInit(String token, String eventId) {
        ViewModelProvider.Factory factory = EventViewModel.providesFactory(
              this.factory, eventId, token, this,
              this::recreate, this::recreate);
        viewModel = new ViewModelProvider(this, factory)
              .get(EventViewModel.class);
        viewModel.getEvent()
              .observe(this, event -> {
                  String userUuid = AuthHelper.getUserUuid(accountManager);
                  String eventCreatorId = event.getCreatorId();
                  if (userUuid.equals(eventCreatorId)) {
                      Log.e(TAG, "User event activity for" +
                            " creator " + userUuid + "/" + eventId + " : " + eventCreatorId);
                      finish();
                  }
                  initLayout(event);
                  viewModel.getEventPhoto()
                        .observe(
                              this,
                              photo -> binding.eventPhoto.setImageBitmap(photo));
              });

    }

    private void initLayout(EventDTO event) {
        binding = ActivityEventReadableBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.eventCreator.setOnClickListener(
              v -> dispatchToUserActivity(this, event.getCreatorId()));
        binding.locationMapButton.setOnClickListener(
              v -> dispatchToEventOnMapActivity(this, event.getId()));

        binding.name.setText(event.getName());
        binding.eventCreated.setText(UI_DATE_TIME_FORMAT.format(event.getCreated()));
        binding.description.setText(event.getDescription());
        binding.latitude.setText(textOrNull(event.getLatitude()));
        binding.longitude.setText(textOrNull(event.getLongitude()));
        binding.starting.setText(dateOrNull(event.getStarting()));
        binding.ending.setText(dateOrNull(event.getEnding()));
        binding.city.setText(event.getCity());
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

    public static void dispatchToUserEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createUserEventActivityIntent(ctx, eventId));
    }

    public static Intent createUserEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, UserEventActivity.class)
              .putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
    }
}
