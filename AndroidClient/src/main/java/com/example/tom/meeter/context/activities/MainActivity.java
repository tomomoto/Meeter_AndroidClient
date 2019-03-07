package com.example.tom.meeter.context.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.tom.meeter.context.network.domain.LoginAttemptEvent;
import com.example.tom.meeter.context.network.domain.SuccessfulLoginEvent;
import com.example.tom.meeter.context.network.domain.FailureLoginEvent;
import com.example.tom.meeter.R;
import com.example.tom.meeter.context.network.service.NetworkService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_ACTIVITY_TAG = MainActivity.class.getCanonicalName();

    @BindView(R.id.editTextLogin)
    TextView login;

    @BindView(R.id.editTextPassword)
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MAIN_ACTIVITY_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent serviceIntent = new Intent(this, NetworkService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(MAIN_ACTIVITY_TAG, "Bus registered");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MAIN_ACTIVITY_TAG, "Bus unregistered");
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SuccessfulLoginEvent event) {
        Log.d(MAIN_ACTIVITY_TAG, event.toString());
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra(SuccessfulLoginEvent.class.getCanonicalName(), event);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FailureLoginEvent event) {
        Log.d(MAIN_ACTIVITY_TAG, event.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Ошибка авторизации")
                .setMessage("Неверная пара логин/пароль")
                .setNegativeButton("Ок", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.LoginButton)
    public void LoginClick(Button button) {
        EventBus.getDefault().post(
                new LoginAttemptEvent(login.getText().toString(), password.getText().toString()));
    }

    @OnClick(R.id.RegistrationButton)
    public void RegistrationClick(Button button) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LoginButton:
                EventBus.getDefault().post(new LoginAttemptEvent(login.getText().toString(),
                        password.getText().toString()));
                break;
            case R.id.RegistrationButton:
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }*/
}
