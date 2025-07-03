package com.tom.meeter.context.auth.infrastructure;

import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.ACCOUNT_TYPE;
import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.AUTH_TYPE;
import static com.tom.meeter.context.auth.infrastructure.AccountAuthenticator.USER_UUID_KEY;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.tom.meeter.R;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
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

    public static String getAuthHeader(AccountManager am) {
        return Globals.getAuthHeader(peekToken(am));
    }

    public static String getUserUuid(AccountManager am) {
        return am.getUserData(getSingleAccount(am), USER_UUID_KEY);
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

    // 10 min of retry.
    private static final int MAX_RETRIES = 60;
    private static final int RETRY_DELAY_MS = 10_000;

    public static void checkToken(
          Consumer<String> onToken, Runnable onCancelledAuth,
          Activity activity, TokenService tokenService) {
        AccountManager am = AccountManager.get(activity);
        Account account = getSingleAccount(am);
        String token = am.peekAuthToken(account, AUTH_TYPE);
        if (token != null) {
            checkTokenWithRetry(
                  tokenService, token, am, activity,
                  onToken, onCancelledAuth, 0);
            return;
        }
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

    private static void simpleCheckToken(
          TokenService tokenService,
          String token,
          AccountManager am,
          Activity activity,
          Consumer<String> onToken,
          Runnable onCancelledAuth) {
        tokenService.checkToken(Globals.getAuthHeader(token)).enqueue(
              new BaseOnNotAuthenticatedCallback<>(
                    activity,
                    () -> invalidateToken(am, activity, onToken, onCancelledAuth)) {
                  @Override
                  public void onResponse(Call<Void> call, Response<Void> response) {
                      super.onResponse(call, response);
                      if (response.code() == HttpCodes.OK) {
                          onToken.accept(token);
                      }
                  }
              });
    }

    private static void checkTokenWithRetry(
          TokenService tokenService,
          String token,
          AccountManager am,
          Activity activity,
          Consumer<String> onToken,
          Runnable onCancelledAuth,
          int attempt) {
        tokenService.checkToken(Globals.getAuthHeader(token))
              .enqueue(new BaseOnNotAuthenticatedCallback<>(
                    activity,
                    () -> invalidateToken(am, activity, onToken, onCancelledAuth)) {
                  @Override
                  public void onResponse(Call<Void> call, Response<Void> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK) {
                          onToken.accept(token);
                          return;
                      }
                  }

                  @Override
                  public void onFailure(Call<Void> call, Throwable t) {
                      super.onFailure(call, t);
                      if (supportedErrorMapping(t)) {
                          retryIfPossible();
                          return;
                      }
                  }

                  private void retryIfPossible() {
                      if (attempt < MAX_RETRIES) {
                          Log.w(TAG, "Retry: " + (attempt + 1));
                          new Handler(Looper.getMainLooper())
                                .postDelayed(
                                      () -> checkTokenWithRetry(
                                            tokenService, token, am, activity, onToken,
                                            onCancelledAuth, attempt + 1),
                                      RETRY_DELAY_MS);
                      } else {
                          Log.e(TAG, "Exceeded retry count.");
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
