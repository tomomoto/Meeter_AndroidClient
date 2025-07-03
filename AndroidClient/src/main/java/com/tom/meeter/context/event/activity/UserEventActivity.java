package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getUserUuid;
import static com.tom.meeter.context.event.activity.EventOnMapActivity.dispatchToEventOnMapActivity;
import static com.tom.meeter.context.event.utils.Utils.currentUserIsEventCreator;
import static com.tom.meeter.context.event.utils.Utils.dumpEventDispatcherError;
import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_TIME_FORMAT;
import static com.tom.meeter.infrastructure.common.CommonHelper.dateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.handleEventStatus;
import static com.tom.meeter.infrastructure.common.CommonHelper.textOrNull;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tom.meeter.App;
import com.tom.meeter.context.event.factory.EventAssistedFactory;
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
    EventService service;
    @Inject
    EventAssistedFactory assistedFactory;

    private ActivityEventReadableBinding binding;
    private EventViewModel viewModel;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        if (!EventDispatcherActivity.validate(this)) {
            return;
        }

        ((App) getApplication()).getEventComponent().inject(this);

        binding = ActivityEventReadableBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        accountManager = AccountManager.get(this);

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
                    this, this::recreate))
              .get(EventViewModel.class);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.init());

        viewModel.getEvent()
              .observe(this, event -> {
                  if (currentUserIsEventCreator(accountManager, event)) {
                      dumpEventDispatcherError(TAG, getUserUuid(accountManager), event);
                      finish();
                      return;
                  }
                  binding.swipeRefresh.setRefreshing(false);
                  initLayout(event);
                  viewModel.getEventPhoto()
                        .observe(
                              this,
                              photo -> binding.eventPhoto.setImageBitmap(photo));
              });

    }

    private void initLayout(EventDTO event) {
        binding.eventCreator.setOnClickListener(
              v -> dispatchToUserActivity(this, event.getCreatorId()));
        binding.locationMapButton.setOnClickListener(
              v -> dispatchToEventOnMapActivity(this, event.getId()));

        handleEventStatus(this, binding.status, event.getStatus());
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
