package com.tom.meeter.context.profile.component.activity;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.PreferencesHelper.savePrefsToServer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.profile.component.fragment.SettingsFragment;
import com.tom.meeter.context.profile.message.SettingsCreateOrUpdate;
import com.tom.meeter.context.profile.service.SettingsService;
import com.tom.meeter.databinding.ActivitySettingsBinding;
import com.tom.meeter.infrastructure.common.PreferencesHelper;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getCanonicalName();

    @Inject
    SettingsService settingsService;

    private ActivitySettingsBinding binding;

    private boolean trackUserBeforeChange;
    private int searchAreaBeforeChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ((App) getApplication()).getProfileComponent().inject(this);

        setSupportActionBar(binding.includeToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);

        readCurrentPreferences();
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
        SettingsCreateOrUpdate req = new SettingsCreateOrUpdate();
        if (searchAreaBeforeChange != searchArea) {
            req.setSearchArea(searchArea);
        }
        if (trackUserBeforeChange != trackUser) {
            req.setNeedTrackUser(trackUser);
        }
        if (!req.isEmpty()) {
            savePrefsToServer(this, settingsService, req);
        }
        startActivity(new Intent(this, ProfileActivity.class));
        super.onBackPressed();
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
