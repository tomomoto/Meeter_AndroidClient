package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tom.meeter.App;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public abstract class BaseEventActivity extends AppCompatActivity {

    public static final String EVENT_ID_KEY = "event_id";

    @Inject
    TokenService tokenService;
    @Inject
    EventService eventService;
    @Inject
    ViewModelFactory viewModelFactory;

    protected EventViewModel eventViewModel;
    protected AccountManager accountManager;
    protected EventDTO eventCache;
    protected ResponseBody photoCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountManager = AccountManager.get(this);

        String eventId = getIntent().getStringExtra(EVENT_ID_KEY);
        if (eventId == null) {
            finish();
            return;
        }

        ((App) getApplication()).getEventComponent().inject(this);

        checkToken(
              token -> initViewModelAndLoadEvent(token, eventId),
              this::finish,
              accountManager,
              this,
              tokenService);
    }

    private void initViewModelAndLoadEvent(String token, String eventId) {
        eventViewModel = new ViewModelProvider(this, viewModelFactory)
              .get(EventViewModel.class);
        eventViewModel.fetchEventInformation(token, eventId, this);
        eventViewModel.getEventLiveData()
              .observe(
                    this,
                    event -> {
                        eventCache = event;
                        if (AuthHelper.getUserUuid(accountManager).equals(eventCache.getCreatorId())) {
                            startActivity(new Intent(this, ProfileEventActivity.class));
                            finish();
                        }
                        initLayout(token);
                    });
    }

    protected abstract void initLayout(String token);
}
