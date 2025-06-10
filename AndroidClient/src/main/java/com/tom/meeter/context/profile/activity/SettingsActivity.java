package com.tom.meeter.context.profile.activity;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.profile.fragment.SettingsFragment;
import com.tom.meeter.context.profile.settings.message.SettingsCreateOrUpdate;
import com.tom.meeter.context.profile.settings.message.SettingsResponse;
import com.tom.meeter.context.profile.settings.service.SettingsService;
import com.tom.meeter.databinding.SettingsActivityBinding;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.common.PreferencesHelper;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
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
        if (searchAreaBeforeChange == searchArea && trackUserBeforeChange == trackUser) {
            super.onBackPressed();
            return;
        }
        AuthHelper.setupTokenAction(
              accountManager, this,
              token -> sendSavePrefs(token, searchArea, trackUser));
        super.onBackPressed();
    }

    private void sendSavePrefs(String token, int searchArea, boolean trackUser) {
        settingsService.createOrUpdateSettings(
                    new SettingsCreateOrUpdate(searchArea, trackUser),
                    Constants.getAuthHeader(token))
              .enqueue(new Callback<>() {
                  @Override
                  public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> res) {
                      if (res.code() == 200 || res.code() == 201) {
                          Log.d(TAG, "SettingsActivity: created/updated server settings.");
                      } else {
                          Log.d(TAG, "SettingsActivity: failed request. " + res.body());
                      }
                  }

                  @Override
                  public void onFailure(Call<SettingsResponse> call, Throwable t) {
                      int serverIsUnreachable = R.string.server_is_unreachable;
                      Toast.makeText(getApplicationContext(), serverIsUnreachable, Toast.LENGTH_SHORT)
                            .show();
                      Log.d(TAG, "SettingsActivity: " + getResources().getString(serverIsUnreachable));
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