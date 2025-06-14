package com.tom.meeter.context.launcher;

import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.ACCOUNT_TYPE;
import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.AUTH_TYPE;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.service.TokenService;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.databinding.LauncherBinding;

import java.io.IOException;

import javax.inject.Inject;

public class Launcher extends AppCompatActivity {

    private static final String TAG = Launcher.class.getCanonicalName();
    public static final String EXPIRED =
          "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoidXNlciIsImlkIjoiOTg4YmM3NzItZDVmNC00YjFmLWEzNDYtMjc3Ym" +
                "E0YzMxZjg3Iiwic3ViIjoiMSIsImlhdCI6MTc0OTU2Nzk5MiwiZXhwIjoxNzQ5NzExOTkyfQ.-Yjws02s" +
                "kCu_StFdoe7jZefpHkXUqKhuyXKIYLNBMdk";
    private AccountManager accountManager;

    private LauncherBinding binding;
    @Inject
    TokenService tokenService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        ((App) getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this);
        binding = LauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void onPostCreate(
          @Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        logMethod(TAG, this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logMethod(TAG, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        logMethod(TAG, this);

        new Handler(Looper.getMainLooper())
              .post(this::initialize);
    }

    private void initialize() {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length == 0) {
            createAccountAndContinue();
        } else if (accounts.length == 1) {
            //accountManager.setAuthToken(accounts[0], AUTH_TYPE, EXPIRED);
            showMessage(Launcher.this, getString(R.string.check_token));
            checkToken((ign) -> dispatch(), this::finish, accountManager, this, tokenService);
        } else {
            removeAllAccounts();
            createAccountAndContinue();
        }
    }

    private void dispatch() {
        startActivity(new Intent(Launcher.this, ProfileActivity.class));
    }

    private void createAccountAndContinue() {
        accountManager.addAccount(
              ACCOUNT_TYPE, AUTH_TYPE, null, null, this,
              bundleF -> {
                  Bundle bnd;
                  try {
                      bnd = bundleF.getResult();
                  } catch (OperationCanceledException | AuthenticatorException | IOException e) {
                      showMessage(this, e.getMessage());
                      finish();
                      return;
                  }
                  showMessage(this, getString(R.string.account_created));
                  Log.d(TAG, "AddNewAccount Bundle is " + bnd);
                  dispatch();
              },
              null);
    }

    private void removeAllAccounts() {
        for (Account acc : accountManager.getAccountsByType(ACCOUNT_TYPE)) {
            Log.d(TAG, "Account to remove: " + acc.toString());
            removeAccount(acc);
        }
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
    protected void onDestroy() {
        logMethod(TAG, this);
        super.onDestroy();
    }

    private void removeAccount(Account account) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(account, this,
                  future -> Log.d(TAG, "Remove" + account.toString() + " succeed."), null);
        }
    }

    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        accountManager.getAuthTokenByFeatures(
              accountType,
              authTokenType,
              null,
              this,
              null,
              null,
              f -> {
                  Bundle bnd = null;
                  try {
                      bnd = f.getResult();
                      String token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                      showMessage(this, ((token != null) ? "SUCCESS!\ntoken: " + token : "FAIL"));
                      Log.d(TAG, "GetTokenForAccount Bundle is " + bnd);
                  } catch (Exception e) {
                      e.printStackTrace();
                      showMessage(this, e.getMessage());
                  }
              },
              null);
    }

    /**
     * Get the auth token for an existing account on the AccountManager
     * @param account
     * @param authTokenType
     */
    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = accountManager.getAuthToken(
              account, authTokenType, null, this, null, null);

        new Thread(() -> {
            try {
                Bundle bnd = future.getResult();

                final String token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                showMessage(this, (token != null) ? "SUCCESS!\ntoken: " + token : "FAIL");
                Log.d(TAG, "GetToken Bundle is " + bnd);
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(this, e.getMessage());
            }
        }).start();
    }
}
