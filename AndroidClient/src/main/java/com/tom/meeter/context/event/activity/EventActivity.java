package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.image.ImageHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.tom.meeter.App;
import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.activity.UserActivity;
import com.tom.meeter.databinding.EventLayoutBinding;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class EventActivity extends AppCompatActivity {
    public static final String EVENT_ID_KEY = "event_id";
    private static final String TAG = EventActivity.class.getCanonicalName();
    EventLayoutBinding binding;
    @Inject
    TokenService tokenService;
    @Inject
    ViewModelFactory viewModelFactory;
    private EventViewModel eventViewModel;
    private String eventId;
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
        eventId = extras.getString(EVENT_ID_KEY);
        if (eventId == null) {
            Log.d(TAG, "Unable to create event activity without 'event_id' provided.");
            finish();
            return;
        }

        ((App) getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this);

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken(this::onInit, this::finish, accountManager, this, tokenService);
    }

    private void onInit(String token) {
        binding = EventLayoutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        eventViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(EventViewModel.class);
        eventViewModel.fetchEventInformation(token, eventId, this);
        eventViewModel.getEventLiveData()
              .observe(this, event -> {
                  if (event != null) {
                      binding.eventName.setText(event.getName());
                      binding.eventDescription.setText(event.getDescription());
                      binding.eventCreatorIdBtn.setOnClickListener(v -> {
                          startActivity(new Intent(this, UserActivity.class)
                                .putExtra(UserActivity.USER_ID_KEY, event.getCreatorId()));
                      });
                  }
              });

        eventViewModel.getEventPhotoLiveData()
              .observe(this, photoBody -> {
                  if (photoBody != null) {
                      binding.eventPhoto.setImageBitmap(
                            circleImage(BitmapFactory.decodeStream(photoBody.byteStream())));
                  }
              });
    }

    @Nullable
    @Override
    public View onCreateView(
          @Nullable View parent, @NonNull String name, @NonNull Context ctx,
          @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, ctx, attrs);
    }
}
