package com.example.tom.meeter.context.auth.infrastructure;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.tom.meeter.App;
import com.example.tom.meeter.context.auth.login.activity.LoginActivity;
import com.example.tom.meeter.context.auth.login.message.LoginBody;
import com.example.tom.meeter.context.auth.login.message.LoginResponse;
import com.example.tom.meeter.context.auth.service.AuthService;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "com.example.tom.meeter.account";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "Meeter";

    public static final String JWT_TOKEN = "jwt-auth";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "is-adding-new-account";
    public static final String PARAM_USER_PASS = "user-password";
    public static final String ARG_AUTH_TYPE = "arg-auth-type";
    public static final String ARG_ACCOUNT_TYPE = "arg-account-type";

    private Context context;
    private AccountManager accountManager;

    @Inject
    AuthService authService;

    public AccountAuthenticator(Context context) {
        super(context);
        this.context = context;
        ((App) context.getApplicationContext()).getComponent().inject(this);
        accountManager = AccountManager.get(context);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(
          AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(
          AccountAuthenticatorResponse response, Account account, Bundle options) {
        return null;
    }

    @Override
    public Bundle getAuthToken(
          AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        String authToken = accountManager.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = accountManager.getPassword(account);
            if (password != null) {
                try {
                    Response<LoginResponse> execute = authService.login(new LoginBody(account.name, password))
                          .execute();
                    authToken = execute.body().getToken();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our LoginActivity.
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(ARG_AUTH_TYPE, authTokenType);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return "";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
