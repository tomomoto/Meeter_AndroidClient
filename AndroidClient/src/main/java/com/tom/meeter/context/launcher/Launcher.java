package com.tom.meeter.context.launcher;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.setupTokenAction;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AccountAuthenticator;
import com.tom.meeter.context.profile.activity.ProfileActivity;

import java.io.IOException;
import java.util.function.Consumer;

public class Launcher extends AppCompatActivity {

    private static final String TAG = Launcher.class.getCanonicalName();
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
        setContentView(R.layout.launcher);
        accountManager = AccountManager.get(this);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        logMethod(TAG, this);
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        logMethod(TAG, this);
        return super.onCreateView(name, context, attrs);
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
                      showMessage(this, getString(R.string.account_created));
                      Log.d(TAG, "AddNewAccount Bundle is " + bundle);
                      checkTokenAndStartProfileActivity();
                  });
        } else if (accounts.length == 1) {
            showMessage(this, getString(R.string.check_token));
            checkTokenAndStartProfileActivity();
        } else {
            removeAllAccounts(accounts, accountManager, this);
            addNewAccount(
                  bundle -> {
                      showMessage(this, "Account was created");
                      Log.d(TAG, "AddNewAccount Bundle is " + bundle);
                      checkTokenAndStartProfileActivity();
                  });
        }
    }

    private static void removeAllAccounts(
          Account[] accounts, AccountManager am, Activity activity) {
        for (Account acc : accounts) {
            Log.d(TAG, "Acc :" + acc.toString());
            removeAccount(acc, am, activity);
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

    private static AccountManagerFuture<Bundle> removeAccount(
          Account account, AccountManager am, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return am.removeAccount(
                  account,
                  activity,
                  future -> Log.d(TAG, "Remove" + account.toString() + " succeed."),
                  null);
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
              AccountAuthenticator.AUTH_TYPE,
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
