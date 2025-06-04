package com.example.tom.meeter.context.auth.login.activity;

import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.tom.meeter.App;
import com.example.tom.meeter.R;
import com.example.tom.meeter.context.auth.login.message.LoginBody;
import com.example.tom.meeter.context.auth.login.message.LoginResponse;
import com.example.tom.meeter.context.auth.service.AuthService;
import com.example.tom.meeter.context.profile.activity.ProfileActivity;
import com.example.tom.meeter.context.auth.registration.activity.RegistrationActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getCanonicalName();

    @BindView(R.id.editTextLogin)
    TextView login;

    @BindView(R.id.editTextPassword)
    TextView password;

    @Inject
    AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);

        ((App) getApplication()).getComponent().inject(this);

        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        logMethod(TAG, this);
        //EventBus.getDefault().register(this);
        //Log.d(TAG, "LoginActivity EventBus registered for " + this);
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
        //EventBus.getDefault().unregister(this);
        //Log.d(TAG, "LoginActivity EventBus unregistered for " + this);
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

        Call<LoginResponse> loginCall = authService.login(
              new LoginBody(loginText.toString(), pwdText.toString()));
        loginCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                logMethod(TAG, this);

                if (response.code() == 200) {
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    //TODO intent.putExtra(USER_ID_KEY, ev.getId());
                    startActivity(intent);
                    finish();
                    return;
                }
                if (response.code() == 403) {
                    new AlertDialog.Builder(LoginActivity.this)
                          .setTitle(getString(R.string.login_failure))
                          .setMessage(getString(R.string.wrong_credentials))
                          .setNegativeButton(getString(R.string.ok), (dialog, id) -> dialog.cancel())
                          .create()
                          .show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                logMethod(TAG, this);

                new AlertDialog.Builder(LoginActivity.this)
                      .setTitle(getString(R.string.login_failure))
                      .setMessage(getString(R.string.wrong_credentials))
                      .setNegativeButton(getString(R.string.ok), (dialog, id) -> dialog.cancel())
                      .create()
                      .show();
            }
        });
    }

    @OnClick(R.id.RegistrationButton)
    public void onRegisterClick(Button button) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }
}
