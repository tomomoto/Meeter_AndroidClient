package com.tom.meeter.context.user.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.infrastructure.common.CommonHelper.genderResolver;
import static com.tom.meeter.infrastructure.common.DateHelper.getAgeFromDate;
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
import androidx.lifecycle.ViewModelProviders;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.event.activity.EventActivity;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.GridViewAdapter;
import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.databinding.UserLayoutBinding;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = UserActivity.class.getCanonicalName();
    public static final String USER_ID_KEY = "user_id";
    UserLayoutBinding binding;
    @Inject
    TokenService tokenService;
    @Inject
    ViewModelFactory viewModelFactory;
    private UserViewModel userViewModel;
    private String userId;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d(TAG, "Unable to create user activity without extras.");
            finish();
            return;
        }
        userId = extras.getString(USER_ID_KEY);
        if (userId == null) {
            Log.d(TAG, "Unable to create user activity without 'user_id' provided.");
            finish();
            return;
        }

        ((App) getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this);

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken(this::onInit, this::finish, accountManager, this, tokenService);
    }

    private void onInit(String token) {
        binding = UserLayoutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(UserViewModel.class);
        userViewModel.fetchUserInformation(token, userId, this);
        userViewModel.getUserLiveData()
              .observe(this, user -> {
                  if (user != null) {
                      binding.userFormat.setText(getString(
                            R.string.user_format, user.getName(), user.getSurname(),
                            getAgeFromDate(user.getBirthday()),
                            genderResolver(this, user.getGender())));
                      binding.userInfo.setText(user.getInfo());
                  }
              });
        userViewModel.getUserEventsLiveData()
              .observe(this, events -> {
                  if (events != null && !events.isEmpty()) {
                      //GridAdapter adapter = new GridAdapter(this);
                      //binding.userEventsGrid.setAdapter(adapter);

                      GridViewAdapter adapter = new GridViewAdapter(this, events);
                      binding.userEventsGrid.setAdapter(adapter);
                      binding.userEventsGrid.setOnItemClickListener(
                            (parent, view1, position, id) ->
                                  startActivity(new Intent(UserActivity.this, EventActivity.class).putExtra(EventActivity.EVENT_ID_KEY, events.get(position).getId())));
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
