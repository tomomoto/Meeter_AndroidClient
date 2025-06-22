package com.tom.meeter.context.profile.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.invalidateToken;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.launcher.Launcher;
import com.tom.meeter.context.profile.fragment.SettingsFragment;
import com.tom.meeter.context.profile.settings.message.SettingsCreateOrUpdate;
import com.tom.meeter.context.profile.settings.message.SettingsResponse;
import com.tom.meeter.context.profile.settings.service.SettingsService;
import com.tom.meeter.databinding.SettingsActivityBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.common.PreferencesHelper;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getCanonicalName();

    @Inject
    SettingsService settingsService;

    SettingsActivityBinding binding;

    private AccountManager accountManager;

    private boolean trackUserBeforeChange;
    private int searchAreaBeforeChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        ((App) getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this);

        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = binding.settingsActivityToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readCurrentPreferences();

        // below line is to change
        // the title of our action bar.
        getSupportActionBar().setTitle(R.string.settings);

        // below line is used to check if
        // frame layout is empty or not.
        if (savedInstanceState != null) {
            return;
        }
        // below line is to inflate our fragment.
        //SettingsFragmentOld fragmentOld = new SettingsFragmentOld();
        SettingsFragment fragment = new SettingsFragment();
        getSupportFragmentManager()
              .beginTransaction()
              .add(binding.idFrameLayout.getId(), fragment)
              .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        logMethod(TAG, this);
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "Home pressed");
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        logMethod(TAG, this);
        int searchArea = PreferencesHelper.getSearchArea(this);
        boolean trackUser = PreferencesHelper.getNeedTrackUser(this);
        if (searchAreaBeforeChange != searchArea || trackUserBeforeChange != trackUser) {
            sendSavePrefs(searchArea, trackUser);
        }
        startActivity(new Intent(this, ProfileActivity.class));
        super.onBackPressed();
    }

    private void sendSavePrefs(int searchArea, boolean trackUser) {
        settingsService.createOrUpdateSettings(
                    new SettingsCreateOrUpdate(searchArea, trackUser),
                    Globals.getAuthHeader(AuthHelper.peekToken(accountManager)))
              .enqueue(new HttpErrorLogger<>(this) {
                  @Override
                  public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> res) {
                      super.onResponse(call, res);
                      if (res.code() == HttpCodes.NOT_AUTHENTICATED) {
                          invalidateToken(
                                accountManager, SettingsActivity.this,
                                (freshToken) -> sendSavePrefsRetry(freshToken, searchArea, trackUser),
                                () -> {
                                    Log.d(TAG, "SettingsActivity: canceled auth.");
                                    startActivity(new Intent(SettingsActivity.this, Launcher.class));
                                });
                          return;
                      }
                      if (res.code() == HttpCodes.OK || res.code() == HttpCodes.CREATED) {
                          Log.d(TAG, "SettingsActivity: created/updated server settings.");
                          return;
                      }
                  }
              });
    }

    private void sendSavePrefsRetry(String token, int searchArea, boolean trackUser) {
        settingsService.createOrUpdateSettings(
                    new SettingsCreateOrUpdate(searchArea, trackUser),
                    Globals.getAuthHeader(token))
              .enqueue(new HttpErrorLogger<>(this) {
                  @Override
                  public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> res) {
                      super.onResponse(call, res);
                      if (res.code() == HttpCodes.OK || res.code() == HttpCodes.CREATED) {
                          Log.d(TAG, "SettingsActivity: created/updated server settings on retry.");
                          return;
                      }
                  }
              });
    }

    private void readCurrentPreferences() {
        trackUserBeforeChange = PreferencesHelper.getNeedTrackUser(this);
        searchAreaBeforeChange = PreferencesHelper.getSearchArea(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        logMethod(TAG, this);
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        logMethod(TAG, this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        logMethod(TAG, this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        logMethod(TAG, this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        logMethod(TAG, this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        logMethod(TAG, this);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        logMethod(TAG, this);
        super.onRestart();
    }
}