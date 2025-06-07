package com.example.tom.meeter.context.auth.registration.activity;

import static com.example.tom.meeter.context.auth.infrastructure.AccountAuthenticator.ACCOUNT_TYPE;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.meeter.App;
import com.example.tom.meeter.R;
import com.example.tom.meeter.context.auth.infrastructure.AccountAuthenticator;
import com.example.tom.meeter.context.auth.message.TokenResponse;
import com.example.tom.meeter.context.auth.registration.message.RegisterBody;
import com.example.tom.meeter.context.auth.service.AuthService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tom on 01.12.2016.
 */
public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getCanonicalName();
    private static final String EMPTY_TEXT = "";

    @Inject
    AuthService authService;

    @BindView(R.id.registerLoginEditText)
    EditText login;
    @BindView(R.id.registerPasswordEditText)
    EditText password;
    @BindView(R.id.registerRepeatPasswordEditText)
    EditText repeatPassword;
    @BindView(R.id.registerMatchesEditText)
    TextView passwordsMatches;
    @BindView(R.id.registerNameEditText)
    EditText name;
    @BindView(R.id.registerGenderRadioGroup)
    RadioGroup gender;
    @BindView(R.id.registerRegisterBtn)
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        ((App) getApplication()).getComponent().inject(this);
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        logMethod(TAG, this);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        logMethod(TAG, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        logMethod(TAG, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        logMethod(TAG, this);
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged(
          value = {R.id.registerPasswordEditText, R.id.registerRepeatPasswordEditText},
          callback = AFTER_TEXT_CHANGED)
    public void passwordsChangedHandler(Editable text) {
        CharSequence pass = password.getText();
        CharSequence repeatedPass = repeatPassword.getText();

        if (pass == null || EMPTY_TEXT.equals(pass.toString())
              || repeatedPass == null || EMPTY_TEXT.equals(repeatedPass.toString())) {
            passwordsMatches.setText(getString(R.string.enter_your_password));
            register.setEnabled(false);
            return;
        }
        boolean matches = pass.toString().equals(repeatedPass.toString());
        passwordsMatches.setText(matches ? getString(R.string.matches) : getString(R.string.does_not_match));
        register.setEnabled(matches);
    }

    @OnClick(R.id.registerRegisterBtn)
    public void registerClickHandler(Button btn) {
        submit();
    }

    public void submit() {
        String userLogin = login.getText().toString();
        String userPass = password.getText().toString();
        String userName = name.getText().toString();
        String userGender = resolveGender(gender.getCheckedRadioButtonId());
        Call<TokenResponse> registerCall = authService.register(
              new RegisterBody(userLogin, userPass, userName, userGender));
        registerCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.code() == 200) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AccountManager.KEY_ACCOUNT_NAME, userLogin);
                    bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                    bundle.putString(AccountManager.KEY_AUTHTOKEN, response.body().getToken());
                    bundle.putString(AccountAuthenticator.USER_PASS_KEY, userPass);

                    Intent res = new Intent();
                    res.putExtras(bundle);
                    setResult(RESULT_OK, res);
                    finish();
                } else {
/*                    Toast.makeText(getApplicationContext(), R.string.register_error, Toast.LENGTH_SHORT)
                          .show();*/
                    new AlertDialog.Builder(RegistrationActivity.this)
                          .setTitle(getString(R.string.register_error))
                          .setMessage(response.code() + ":" + response.body())
                          .setNegativeButton(getString(R.string.ok), (dialog, id) -> dialog.cancel())
                          .create()
                          .show();
                    Log.d(TAG, "Response failed with [" + response.code()
                          + "] code and body {" + response.body() + "}");
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                int registerError = R.string.register_error;
                Toast.makeText(getApplicationContext(), registerError, Toast.LENGTH_SHORT)
                      .show();
                Log.d(TAG, "Register: " + getResources().getString(registerError));
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private static String resolveGender(int radio) {
        switch (radio) {
            case R.id.registerRadioBtnMale:
                return "male";
            case R.id.registerRadioBtnFemale:
                return "female";
            default:
                throw new IllegalArgumentException("#args - radio: " + radio);
        }
    }
}
