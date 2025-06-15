package com.tom.meeter.context.auth.activity;

import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.ACCOUNT_TYPE;
import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.USER_PASS_KEY;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AccountAuthenticator;
import com.tom.meeter.context.auth.message.LoginBody;
import com.tom.meeter.context.auth.message.TokenResponse;
import com.tom.meeter.context.auth.service.AuthService;
import com.tom.meeter.databinding.LoginActivityBinding;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getCanonicalName();

    private static final int REQ_SIGN_UP_OK = 1;

    LoginActivityBinding binding;

    @Inject
    AuthService authService;

    private AccountManager accountManager;
    private AccountAuthenticatorResponse accountAuthenticatorResponse = null;
    private Bundle accountAuthenticationResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);

        ((App) getApplication()).getAuthComponent().inject(this);

        accountManager = AccountManager.get(this);

        accountAuthenticatorResponse =
              getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (accountAuthenticatorResponse != null) {
            accountAuthenticatorResponse.onRequestContinued();
        }

        binding = LoginActivityBinding.inflate(getLayoutInflater());

        binding.loginSubmit.setOnClickListener(v -> onLoginClick());
        binding.loginRegistrationButton.setOnClickListener(v -> onRegisterClick());
        View view = binding.getRoot();
        setContentView(view);
    }


    @Override
    public void finish() {
        if (accountAuthenticatorResponse != null) {
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

    public void onLoginClick() {
        CharSequence loginText = binding.loginLoginEditText.getText();
        CharSequence pwdText = binding.loginPasswordEditText.getText();
        if (loginText == null || loginText.toString().isEmpty()
              || pwdText == null || pwdText.toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.fill_login_params, Toast.LENGTH_SHORT)
                  .show();
            return;
        }

        submit();
    }

    public void submit() {
        String userLogin = binding.loginLoginEditText.getText().toString();
        String userPass = binding.loginPasswordEditText.getText().toString();
        Call<TokenResponse> loginCall = authService.login(new LoginBody(userLogin, userPass));
        loginCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.code() == HttpCodes.OK) {
                    Intent intent = new Intent();
                    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, userLogin);
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                    intent.putExtra(AccountManager.KEY_AUTHTOKEN, response.body().getToken());
                    intent.putExtra(USER_PASS_KEY, userPass);
                    finishLogin(intent);
                } else {
                    new AlertDialog.Builder(LoginActivity.this)
                          .setTitle(getString(R.string.error))
                          .setMessage(getString(R.string.wrong_credentials))
                          .setNegativeButton(getString(R.string.ok), (dialog, id) -> dialog.cancel())
                          .create()
                          .show();
                    Log.d(TAG, "Invalid login.");
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                int serverIsUnreachable = R.string.server_is_unreachable;
                Toast.makeText(getApplicationContext(), serverIsUnreachable, Toast.LENGTH_SHORT)
                      .show();
                Log.d(TAG, "Login: " + getResources().getString(serverIsUnreachable));
            }
        });
    }

    private void finishLogin(Intent intent) {
        String login = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        String token = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String pass = intent.getStringExtra(AccountAuthenticator.USER_PASS_KEY);
        Account account = new Account(login, accountType);
        if (getIntent().getBooleanExtra(AccountAuthenticator.IS_ADDING_NEW_ACCOUNT_KEY, false)) {
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, pass, null);
            accountManager.setAuthToken(account, AccountAuthenticator.AUTH_TYPE, token);
        } else {
            accountManager.setPassword(account, pass);
        }
        accountAuthenticationResult = intent.getExtras();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onRegisterClick() {
        Intent signup = new Intent(getBaseContext(), RegistrationActivity.class);
        signup.putExtras(getIntent().getExtras());
        startActivityForResult(signup, REQ_SIGN_UP_OK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGN_UP_OK && resultCode == RESULT_OK) {
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
