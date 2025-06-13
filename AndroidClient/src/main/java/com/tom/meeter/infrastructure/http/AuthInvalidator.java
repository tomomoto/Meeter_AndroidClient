package com.tom.meeter.infrastructure.http;

import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.ACCOUNT_TYPE;
import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.AUTH_TYPE;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tom.meeter.R;

import java.io.IOException;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Response;

public class AuthInvalidator<T> extends DisconnectLogger<T> {
    private static final String TAG = AuthInvalidator.class.getCanonicalName();
    private final AccountManager accountManager;
    private final Consumer<String> afterTokenSetup;
    private final Runnable onCancelledAuth;

    public AuthInvalidator(
          Activity activity, AccountManager accountManager,
          Consumer<String> afterTokenSetup, Runnable onCancelledAuth) {
        super(activity);
        this.accountManager = accountManager;
        this.afterTokenSetup = afterTokenSetup;
        this.onCancelledAuth = onCancelledAuth;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length != 1) {
            throw new RuntimeException("Multiple accounts are not implemented yet.");
        }
        Account account = accounts[0];
        String token = accountManager.peekAuthToken(account, AUTH_TYPE);
        Toast.makeText(activity, R.string.refreshing_the_token, Toast.LENGTH_SHORT)
              .show();
        Log.d(TAG, "AuthInvalidator: " + activity.getComponentName()
              + ": " + activity.getResources().getString(R.string.refreshing_the_token));
        accountManager.invalidateAuthToken(ACCOUNT_TYPE, token);
        accountManager.getAuthToken(
              account, AUTH_TYPE, null, activity,
              future -> {
                  Bundle result;
                  try {
                      result = future.getResult();
                  } catch (AuthenticatorException e) {
                      throw new RuntimeException(e);
                  } catch (IOException e) {
                      throw new RuntimeException(e);
                  } catch (OperationCanceledException e) {
                      onCancelledAuth.run();
                      return;
                  }
                  afterTokenSetup.accept(result.getString(AccountManager.KEY_AUTHTOKEN));
              }, null);
    }
}
