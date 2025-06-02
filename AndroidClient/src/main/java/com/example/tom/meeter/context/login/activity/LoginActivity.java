package com.example.tom.meeter.context.login.activity;

import static com.example.tom.meeter.infrastructure.common.Constants.USER_ID_KEY;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.network.domain.LoginAttempt;
import com.example.tom.meeter.context.network.service.NetworkService;
import com.example.tom.meeter.context.profile.activity.ProfileActivity;
import com.example.tom.meeter.context.registration.activity.RegistrationActivity;
import com.example.tom.meeter.infrastructure.eventbus.events.FailureLogin;
import com.example.tom.meeter.infrastructure.eventbus.events.SuccessfulLogin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getCanonicalName();

    @BindView(R.id.editTextLogin)
    TextView login;

    @BindView(R.id.editTextPassword)
    TextView password;

    private ServiceConnection sConn;
    private boolean nwServiceBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                logMethod(TAG, this);
                nwServiceBound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                logMethod(TAG, this);
                nwServiceBound = false;
            }
        };
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        //Log.d(TAG, "LoginActivity onCreate()... Starting NetworkService");
        //startService(new Intent(this, NetworkService.class));
        Log.d(TAG, "LoginActivity Binding NetworkService");
        bindService(
                new Intent(this, NetworkService.class),
                sConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        logMethod(TAG, this);
        EventBus.getDefault().register(this);
        Log.d(TAG, "LoginActivity EventBus registered for " + this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logMethod(TAG, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        logMethod(TAG, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        logMethod(TAG, this);
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "LoginActivity EventBus unregistered for " + this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logMethod(TAG, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
        Log.d(TAG, "LoginActivity unbindService " + sConn);
        unbindService(sConn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        logMethod(TAG, this);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        logMethod(TAG, this);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.LoginButton)
    public void onLoginClick(Button button) {
        CharSequence loginText = login.getText();
        CharSequence pwdText = password.getText();
        if (loginText == null || loginText.toString().isEmpty()
                || pwdText == null || pwdText.toString().isEmpty()) {
            Log.d(TAG, "Illegal login request...");
            return;
        }
        EventBus.getDefault().post(new LoginAttempt(loginText.toString(), pwdText.toString()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SuccessfulLogin ev) {
        Log.d(TAG, ev.toString());
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        intent.putExtra(USER_ID_KEY, ev.getId());
        startActivity(intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FailureLogin event) {
        Log.d(TAG, event.toString());
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(getString(R.string.login_failure))
                .setMessage(getString(R.string.wrong_credentials))
                .setNegativeButton(getString(R.string.ok), (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }

    @OnClick(R.id.RegistrationButton)
    public void onRegisterClick(Button button) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }
}
