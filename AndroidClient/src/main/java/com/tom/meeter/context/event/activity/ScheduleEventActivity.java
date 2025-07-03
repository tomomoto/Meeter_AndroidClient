package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.event.activity.EventDispatcherActivity.EVENT_ID_KEY;
import static com.tom.meeter.context.event.utils.Utils.createScheduleEventRequest;
import static com.tom.meeter.context.event.utils.Utils.currentUserIsEventCreator;
import static com.tom.meeter.context.event.utils.Utils.dumpEventDispatcherError;
import static com.tom.meeter.infrastructure.common.CommonHelper.dateOrNull;
import static com.tom.meeter.infrastructure.common.DateHelper.showDateTimePicker;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

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
import com.tom.meeter.R;
import com.tom.meeter.context.event.factory.EventAssistedFactory;
import com.tom.meeter.context.event.message.ScheduleEventRequest;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ActivityEventScheduleBinding;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class ScheduleEventActivity extends AppCompatActivity {

    private static final String TAG = ScheduleEventActivity.class.getCanonicalName();

    @Inject
    TokenService tokenService;
    @Inject
    EventService service;
    @Inject
    EventAssistedFactory assistedFactory;

    private final Runnable onNotAuthenticated = this::recreate;
    private ActivityEventScheduleBinding binding;
    private AccountManager accountManager;
    private EventViewModel viewModel;
    private EventDTO eventCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e(TAG, "Unable to create event activity without extras.");
            finish();
            return;
        }
        String eventId = extras.getString(EVENT_ID_KEY);
        if (eventId == null) {
            Log.e(TAG, "Unable to create event activity without 'event_id' provided.");
            finish();
            return;
        }

        ((App) getApplication()).getEventComponent().inject(this);

        accountManager = AccountManager.get(this);

        binding = ActivityEventScheduleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken((token) -> onInit(eventId), this::finish,
              accountManager, this, tokenService);
    }

    private void onInit(String eventId) {
        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(
                    assistedFactory, eventId, this, onNotAuthenticated))
              .get(EventViewModel.class);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.init());

        initLayout();

        viewModel.getEvent()
              .observe(this, event -> {
                  if (!currentUserIsEventCreator(accountManager, event)) {
                      dumpEventDispatcherError(TAG, accountManager, event);
                      finish();
                      return;
                  }
                  binding.swipeRefresh.setRefreshing(false);
                  eventCache = event;
                  updateLayout();
              });
    }

    private void initLayout() {
        binding.selectStartingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.starting));
        binding.selectEndingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.ending));

        binding.scheduleButton.setOnClickListener(v -> {
            service.scheduleEvent(
                        getAuthHeader(accountManager),
                        eventCache.getId(),
                        createScheduleEventRequest(eventCache, binding))
                  .enqueue(new BaseOnNotAuthenticatedCallback<>(this, onNotAuthenticated) {
                      @Override
                      public void onResponse(
                            Call<EventDTO> call, Response<EventDTO> resp) {
                          super.onResponse(call, resp);
                          if (resp.code() != HttpCodes.OK) {
                              showMessage(ScheduleEventActivity.this,
                                    "Unable to schedule the event...");
                              updateLayout();
                              return;
                          }
                          showMessage(ScheduleEventActivity.this, R.string.scheduled);
                          setResult(RESULT_OK);
                          finish();
                      }
                  });
        });
    }

    private void updateLayout() {
        binding.name.setText(eventCache.getName());
        binding.starting.setText(dateOrNull(eventCache.getStarting()));
        binding.ending.setText(dateOrNull(eventCache.getEnding()));
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

    public static void dispatchToScheduleEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createScheduleEventActivityIntent(ctx, eventId));
    }

    public static Intent createScheduleEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, ScheduleEventActivity.class)
              .putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
    }
}
