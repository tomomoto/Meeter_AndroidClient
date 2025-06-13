package com.tom.meeter.context.auth.activity;

import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.ACCOUNT_TYPE;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.tom.meeter.context.auth.message.RegisterBody;
import com.tom.meeter.context.auth.message.TokenResponse;
import com.tom.meeter.context.auth.service.AuthService;
import com.tom.meeter.databinding.RegisterActivityBinding;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

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

    RegisterActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        ((App) getApplication()).getComponent().inject(this);
        binding = RegisterActivityBinding.inflate(getLayoutInflater());
        binding.registerRegisterBtn.setOnClickListener(v -> submit());
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordsChangedHandler(s);
            }
        };
        binding.registerPasswordEditText.addTextChangedListener(watcher);
        binding.registerRepeatPasswordEditText.addTextChangedListener(watcher);
        View view = binding.getRoot();
        setContentView(view);
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

    public void passwordsChangedHandler(Editable text) {
        CharSequence pass = binding.registerPasswordEditText.getText();
        CharSequence repeatedPass = binding.registerRepeatPasswordEditText.getText();

        if (pass == null || EMPTY_TEXT.equals(pass.toString())
              || repeatedPass == null || EMPTY_TEXT.equals(repeatedPass.toString())) {
            binding.registerMatchesEditText.setText(getString(R.string.enter_your_password));
            binding.registerRegisterBtn.setEnabled(false);
            return;
        }
        boolean matches = pass.toString().equals(repeatedPass.toString());
        binding.registerMatchesEditText.setText(
              matches ? getString(R.string.matches) : getString(R.string.does_not_match));
        binding.registerRegisterBtn.setEnabled(matches);
    }

    public void submit() {
        String userLogin = binding.registerLoginEditText.getText().toString();
        String userPass = binding.registerPasswordEditText.getText().toString();
        String userName = binding.registerNameEditText.getText().toString();
        String userGender = resolveGender();
        Call<TokenResponse> registerCall = authService.register(
              new RegisterBody(userLogin, userPass, userName, userGender));
        registerCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.code() == HttpCodes.OK) {
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
                int serverIsUnreachable = R.string.server_is_unreachable;
                Toast.makeText(getApplicationContext(), serverIsUnreachable, Toast.LENGTH_SHORT)
                      .show();
                Log.d(TAG, "Register: " + getResources().getString(serverIsUnreachable));
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private String resolveGender() {
        int id = binding.registerGenderRadioGroup.getCheckedRadioButtonId();
        if (id == binding.registerRadioBtnMale.getId()) {
            return "male";
        }
        if (id == binding.registerRadioBtnFemale.getId()) {
            return "female";
        }
        throw new IllegalArgumentException("#args - radio: " + id);
    }
}
