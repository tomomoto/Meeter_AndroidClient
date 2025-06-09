package com.tom.meeter.context.profile.activity;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tom.meeter.R;
import com.tom.meeter.context.profile.fragment.SettingsFragment;
import com.tom.meeter.databinding.SettingsActivityBinding;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getCanonicalName();

    SettingsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        logMethod(TAG, this);
        super.onBackPressed();
    }
}