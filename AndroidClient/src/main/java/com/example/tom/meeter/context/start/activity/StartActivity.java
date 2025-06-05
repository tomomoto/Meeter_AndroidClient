package com.example.tom.meeter.context.start.activity;

import static com.example.tom.meeter.context.auth.infrastructure.AuthHelper.setupTokenAction;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.auth.infrastructure.AccountAuthenticator;
import com.example.tom.meeter.context.profile.activity.ProfileActivity;

import java.io.IOException;
import java.util.function.Consumer;

import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = StartActivity.class.getCanonicalName();
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        setContentView(R.layout.start_activity);
        ButterKnife.bind(this);
        accountManager = AccountManager.get(this);
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

        Account[] accounts = accountManager.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);

        if (accounts.length == 0) {
            addNewAccount(
                  bundle -> {
                      showMessage(StartActivity.this, "Account was created");
                      Log.d(TAG, "AddNewAccount Bundle is " + bundle);
                      checkTokenAndStartProfileActivity();
                  });
        } else if (accounts.length == 1) {
            //removeAccount(accounts);
            showMessage(StartActivity.this, "Check token and run.");
            checkTokenAndStartProfileActivity();
        } else {
            //???
        }
    }

    private void checkTokenAndStartProfileActivity() {
        setupTokenAction(accountManager, this,
              token -> {
                  Intent intent = new Intent(this, ProfileActivity.class);
                  startActivity(intent);
              });
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

    private AccountManagerFuture<Bundle> removeAccount(Account[] accounts) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return accountManager.removeAccount(accounts[0], this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    Log.d(TAG, "removeAccount succeed");
                }
            }, null);
        }
        return null;
    }

    private void addNewAccount(String accountType, String authTokenType) {
        AccountManagerFuture<Bundle> future = accountManager.addAccount(
              accountType, authTokenType,
              null,
              null,
              this,
              bundleF -> {
                  try {
                      Bundle bnd = bundleF.getResult();
                      showMessage(this, "Account was created");
                      Log.d(TAG, "AddNewAccount Bundle is " + bnd);
                  } catch (Exception e) {
                      e.printStackTrace();
                      showMessage(this, e.getMessage());
                  }
              }, null);
    }

    private void addNewAccount(Consumer<Bundle> bundleConsumer) {
        accountManager.addAccount(
              AccountAuthenticator.ACCOUNT_TYPE,
              AccountAuthenticator.JWT_TOKEN,
              null,
              null,
              this,
              bundleF -> {
                  try {
                      Bundle bnd = bundleF.getResult();
                      bundleConsumer.accept(bnd);
                  } catch (OperationCanceledException | AuthenticatorException | IOException e) {
                      showMessage(this, e.getMessage());
                      throw new RuntimeException(e);
                  }
              }, null);
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


    //todo rework it
    private Account addOrFindAccount(String login, String password) {
        Account[] accounts = accountManager.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
        Account account = getOrCreateAccount(accounts, login);

        if (accounts.length == 0) {
            accountManager.addAccountExplicitly(account, password, null);
        } else {
            accountManager.setPassword(accounts[0], password);
        }
        return account;
    }

    private static Account getOrCreateAccount(Account[] accounts, String login) {
        if (accounts.length == 0) {
            return new Account(login, AccountAuthenticator.ACCOUNT_TYPE);
        }
        if (accounts.length == 1) {
            return accounts[0];
        }
        throw new RuntimeException("More then 1 account for type");
    }
}
