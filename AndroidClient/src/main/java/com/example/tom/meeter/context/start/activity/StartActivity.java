package com.example.tom.meeter.context.start.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.auth.login.activity.LoginActivity;
import com.example.tom.meeter.context.network.service.SocketIOService;

import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = StartActivity.class.getCanonicalName();

    private String userToken = "";

    private ServiceConnection sConn;
    private boolean nwServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(TAG, "StartActivity onServiceConnected()");
                nwServiceBound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "StartActivity onServiceDisconnected()");
                nwServiceBound = false;
            }
        };

        Log.d(TAG, "StartActivity onCreate()... Bind SocketIOService.");
        setContentView(R.layout.start_activity);
        ButterKnife.bind(this);

        bindService(
                new Intent(this, SocketIOService.class),
                sConn, BIND_AUTO_CREATE);

        if (userToken != null && !userToken.isEmpty()) {

        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

    @Override
    public void onPostCreate(
            @Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        Log.d(TAG, "StartActivity onPostCreate()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "StartActivity onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "StartActivity onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "StartActivity onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "StartActivity onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "StartActivity onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "StartActivity onDestroy()... unbindService " + sConn);
        unbindService(sConn);
    }
}
