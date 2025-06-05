package com.example.tom.meeter.context.auth.login.activity;

import static com.example.tom.meeter.context.auth.infrastructure.AccountAuthenticator.ACCOUNT_TYPE;
import static com.example.tom.meeter.context.auth.infrastructure.AccountAuthenticator.ARG_IS_ADDING_NEW_ACCOUNT;
import static com.example.tom.meeter.context.auth.infrastructure.AccountAuthenticator.PARAM_USER_PASS;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.tom.meeter.context.auth.registration.activity.RegistrationActivity;
import com.example.tom.meeter.context.auth.service.AuthService;

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

    private AccountManager accountManager;
    private AccountAuthenticatorResponse accountAuthenticatorResponse = null;
    private Bundle accountAuthenticationResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);

        ((App) getApplication()).getComponent().inject(this);

        accountManager = AccountManager.get(this);

        accountAuthenticatorResponse =
              getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (accountAuthenticatorResponse != null) {
            accountAuthenticatorResponse.onRequestContinued();
        }


        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
    }


    @Override
    public void finish() {
        if (accountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (accountAuthenticationResult != null) {
                accountAuthenticatorResponse.onResult(accountAuthenticationResult);
            } else {
                accountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
            }
            accountAuthenticatorResponse = null;
        }
        super.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logMethod(TAG, this);
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

        submit();
    }

    public void submit() {
        final String userLogin = login.getText().toString();
        final String userPass = password.getText().toString();
        Call<LoginResponse> loginCall = authService.login(new LoginBody(userLogin, userPass));
        loginCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200) {
                    Intent intent = new Intent();
                    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, userLogin);
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                    intent.putExtra(AccountManager.KEY_AUTHTOKEN, response.body().getToken());
                    intent.putExtra(PARAM_USER_PASS, userPass);
                    finishLogin(intent);
                } else {
                    Log.d(TAG, "Invalid login");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }

    private void finishLogin(Intent intent) {
        String login = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        String token = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String pass = intent.getStringExtra(PARAM_USER_PASS);
        boolean addNewAcc = getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);
        Account account = new Account(login, accountType);
        if (addNewAcc) {
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, pass, null);
            accountManager.setAuthToken(account, ACCOUNT_TYPE, token);
        } else {
            accountManager.setPassword(account, pass);
        }
        accountAuthenticationResult = intent.getExtras();
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.RegistrationButton)
    public void onRegisterClick(Button button) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }
}
