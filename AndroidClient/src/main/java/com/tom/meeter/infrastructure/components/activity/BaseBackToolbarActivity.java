package com.tom.meeter.infrastructure.components.activity;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseBackToolbarActivity extends AppCompatActivity {

    private static final String TAG = BaseBackToolbarActivity.class.getCanonicalName();

    protected void setupToolbar(Toolbar toolbar, int titleResId) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(titleResId);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        logMethod(TAG, this);
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "Home pressed");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
