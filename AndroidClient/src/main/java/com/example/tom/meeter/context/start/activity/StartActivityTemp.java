package com.example.tom.meeter.context.start.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.profile.fragment.CreateNewEventFragment;
import com.example.tom.meeter.context.profile.fragment.EventsFragment;
import com.example.tom.meeter.context.profile.fragment.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

@Deprecated
public class StartActivityTemp extends AppCompatActivity {

    private static final String TAG = StartActivityTemp.class.getCanonicalName();

    private static Fragment resolveFragment(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.bot_nav_home:
                return new CreateNewEventFragment();
            case R.id.bot_nav_profile:
                return new ProfileFragment();
            case R.id.bot_nav_events:
                return new EventsFragment();
            case R.id.bot_nav_settings:
            default:
                return new ProfileFragment();
        }
    }

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.start_activity_temp);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this::itemSelectedListener);

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
}