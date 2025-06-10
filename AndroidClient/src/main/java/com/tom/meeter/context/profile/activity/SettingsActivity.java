package com.tom.meeter.context.profile.activity;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.tom.meeter.infrastructure.common.Constants.APP_PROPERTIES;
import static com.tom.meeter.infrastructure.common.Constants.MAP_EVENTS_AREA_PROPERTY;
import static com.tom.meeter.infrastructure.common.Constants.MAP_TRACK_USER_PROPERTY;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.profile.fragment.SettingsFragment;
import com.tom.meeter.context.profile.settings.domain.SettingsDomainService;
import com.tom.meeter.context.profile.settings.message.SettingsCreateOrUpdate;
import com.tom.meeter.context.profile.settings.message.SettingsResponse;
import com.tom.meeter.context.profile.settings.service.SettingsService;
import com.tom.meeter.databinding.SettingsActivityBinding;
import com.tom.meeter.infrastructure.common.Constants;

import java.io.IOException;
import java.util.Properties;

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

        SharedPreferences prefs = getDefaultSharedPreferences(this);
        boolean needTrackUser = prefs.getBoolean(getString(R.string.prefs_need_track_user), true);
        int searchArea = prefs.getInt(getString(R.string.prefs_search_area), 1000);
        Log.d(TAG, "NTU " + needTrackUser + " SA " + searchArea);
        SharedPreferences.Editor edit = prefs.edit();
        //edit.putString("preference", "bbb");
        //edit.apply();

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
        if (!SettingsDomainService.isDefaulted(this)) {
            //TODO: need not to send request in case of not updated not defaulted settings...
            AuthHelper.setupTokenAction(accountManager, this, token -> {
                Properties p = new Properties();
                try {
                    p.load(getAssets().open(APP_PROPERTIES));
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }
                SharedPreferences prefs = getDefaultSharedPreferences(this);
                boolean trackUser = prefs.getBoolean(
                      getString(R.string.prefs_need_track_user),
                      Boolean.parseBoolean(p.getProperty(MAP_TRACK_USER_PROPERTY)));
                int searchArea = prefs.getInt(
                      getString(R.string.prefs_search_area),
                      Integer.parseInt(p.getProperty(MAP_EVENTS_AREA_PROPERTY)));
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
            });
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}