package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.event.activity.ProfileEventActivity.dispatchToProfileEventActivity;
import static com.tom.meeter.context.event.activity.UserEventActivity.dispatchToUserEventActivity;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tom.meeter.App;
import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class EventDispatcherActivity extends AppCompatActivity {

    public static final String EVENT_ID_KEY = "event_id";

    private static final String TAG = EventDispatcherActivity.class.getCanonicalName();
    @Inject
    TokenService tokenService;
    @Inject
    EventService eventService;
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
        eventService.amICreator(Globals.getAuthHeader(token), eventId)
              .enqueue(new BaseOnNotAuthenticatedCallback<>(this, this::recreate) {
                  @Override
                  public void onResponse(Call<Boolean> call, Response<Boolean> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK) {
                          if (resp.body()) {
                              dispatchToProfileEventActivity(EventDispatcherActivity.this, eventId);
                          } else {
                              dispatchToUserEventActivity(EventDispatcherActivity.this, eventId);
                          }
                      }
                  }
              });
    }


    public static void dispatchToEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createEventActivityIntent(ctx, eventId));
    }

    public static Intent createEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, EventDispatcherActivity.class)
              .putExtra(EventDispatcherActivity.EVENT_ID_KEY, eventId);
    }
}
