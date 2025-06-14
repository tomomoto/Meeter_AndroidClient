package com.tom.meeter.context.auth.infrastructure;

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
import com.tom.meeter.context.auth.service.TokenService;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.http.DisconnectLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.io.IOException;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Response;

public final class AuthHelper {

    private static final String TAG = AuthHelper.class.getCanonicalName();

    public static RuntimeException freshNotImplementedError() {
        return new RuntimeException("Multiple accounts are not supported yet.");
    }

    private AuthHelper() {
    }

    public static String peekToken(AccountManager am) {
        return am.peekAuthToken(getSingleAccount(am), AccountAuthenticator.AUTH_TYPE);
    }

    public static void setToken(AccountManager am, String token) {
        am.setAuthToken(getSingleAccount(am), AccountAuthenticator.AUTH_TYPE, token);
    }

    public static Account getSingleAccount(AccountManager am) {
        Account[] accounts = am.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
        if (accounts.length != 1) {
            throw AuthHelper.freshNotImplementedError();
        }
        return accounts[0];
    }

    public static void checkToken(
          Consumer<String> onToken, Runnable onCancelledAuth,
          AccountManager am, Activity activity, TokenService tokenService) {
        Account account = getSingleAccount(am);
        String token = am.peekAuthToken(account, AUTH_TYPE);
        if (token == null) {
            am.getAuthToken(
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
                      onToken.accept(result.getString(AccountManager.KEY_AUTHTOKEN));
                  }, null);
            return;
        }
        tokenService.checkToken(Constants.getAuthHeader(token)).enqueue(
              new DisconnectLogger<>(activity) {
                  @Override
                  public void onResponse(Call<Void> call, Response<Void> response) {
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          invalidateToken(am, activity, onToken, onCancelledAuth);
                      }
                      if (response.code() == HttpCodes.OK) {
                          onToken.accept(token);
                      }
                  }
              });
    }

    public static void invalidateToken(
          AccountManager am, Activity activity, Consumer<String> onToken,
          Runnable onCancelledAuth) {
        Account account = getSingleAccount(am);
        String token = am.peekAuthToken(account, AUTH_TYPE);
        Toast.makeText(activity, R.string.refreshing_the_token, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "AuthHelper invalidating token for: " +
              activity.getComponentName() + " : "
              + activity.getResources().getString(R.string.refreshing_the_token));
        am.invalidateAuthToken(ACCOUNT_TYPE, token);
        am.getAuthToken(
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
                  onToken.accept(result.getString(AccountManager.KEY_AUTHTOKEN));
              }, null);
    }
}
