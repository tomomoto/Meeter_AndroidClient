package com.tom.meeter.context.launcher;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tom.meeter.R;
import com.tom.meeter.context.profile.fragment.ProfileFragment;
import com.tom.meeter.databinding.StartActivityTempBinding;

@Deprecated
public class StartActivityTemp extends AppCompatActivity {

    private static final String TAG = StartActivityTemp.class.getCanonicalName();

    StartActivityTempBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = StartActivityTempBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(
              this::itemSelectedListener);

        getSupportFragmentManager().beginTransaction()
              .replace(R.id.frame_layout, new ProfileFragment())
              .commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    private boolean itemSelectedListener(MenuItem i) {
        getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.frame_layout, resolveFragment(i))
              .commit();
        return true;
    }

    private static Fragment resolveFragment(MenuItem menuItem) {
        return new ProfileFragment();
/*        switch (menuItem.getItemId()) {
            case R.id.bot_nav_home:
                return new CreateEventFragment();
            case R.id.bot_nav_profile:
                return new ProfileFragment();
            case R.id.bot_nav_events:
                return new EventsFragment();
            case R.id.bot_nav_settings:
            default:
                return new ProfileFragment();
        }*/
    }
}