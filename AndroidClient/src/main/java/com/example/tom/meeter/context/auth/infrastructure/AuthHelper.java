package com.example.tom.meeter.context.auth.infrastructure;

import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

public final class AuthHelper {
    private AuthHelper() {
    }

    public static void setupTokenAction(
          AccountManager am, Activity activity, Consumer<String> tokenConsumer) {
        Account[] accounts = am.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
        if (accounts.length == 1) {
            getTokenWithConsumer(
                  am, activity, accounts[0], AccountAuthenticator.JWT_TOKEN,
                  bundle -> bundle.getString(AccountManager.KEY_AUTHTOKEN),
                  tokenConsumer);
        } else {
            //TODO
        }
    }

    private static void getTokenWithConsumer(
          AccountManager am, Activity activity, Account account,
          String authTokenType, Function<Bundle, String> bundleUnWrapper,
          Consumer<String> tokenConsumer) {
        am.getAuthToken(
              account, authTokenType, null, activity, bundleF -> {
                  try {
                      String token = bundleUnWrapper.apply(bundleF.getResult());
                      tokenConsumer.accept(token);
                  } catch (AuthenticatorException | IOException | OperationCanceledException e) {
                      showMessage(activity, e.getMessage());
                      throw new RuntimeException(e);
                  }
              }, null);
    }
}
