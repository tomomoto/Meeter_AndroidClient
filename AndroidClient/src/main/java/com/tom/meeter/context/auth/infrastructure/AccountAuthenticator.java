package com.tom.meeter.context.auth.infrastructure;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.activity.LoginActivity;
import com.tom.meeter.context.auth.message.LoginBody;
import com.tom.meeter.context.auth.message.TokenResponse;
import com.tom.meeter.context.auth.service.AuthService;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.io.IOException;
import java.net.ConnectException;

import javax.inject.Inject;

import retrofit2.Response;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private static final String TAG = AccountAuthenticator.class.getCanonicalName();

    public static final String ACCOUNT_TYPE_KEY = "account-type";
    public static final String ACCOUNT_TYPE = "com.tom.meeter.account";
    public static final String AUTH_TYPE_KEY = "auth-type";
    public static final String AUTH_TYPE = "jwt_auth";
    public static final String IS_ADDING_NEW_ACCOUNT_KEY = "is-adding-new-account";
    public static final String USER_PASS_KEY = "the-password";
    private static final String LABEL = " label";
    //public static final String ACCOUNT_NAME = "Meeter";

    private final Context context;
    private final AccountManager accountManager;

    @Inject
    AuthService authService;

    public AccountAuthenticator(Context context) {
        super(context);
        this.context = context;
        accountManager = AccountManager.get(context);
        ((App) context.getApplicationContext()).getComponent().inject(this);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(
          AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(ACCOUNT_TYPE_KEY, accountType);
        intent.putExtra(AUTH_TYPE_KEY, authTokenType);
        intent.putExtra(IS_ADDING_NEW_ACCOUNT_KEY, true);
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
                Response<TokenResponse> resp;
                try {
                    resp = authService.login(new LoginBody(account.name, password)).execute();
                } catch (ConnectException e) {
                    Log.d(TAG, "AccountAuthenticator: "
                          + context.getResources().getString(R.string.server_is_unreachable));
                    return getBundleForFailedSignIn(response, account, authTokenType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (resp.code() == HttpCodes.OK) {
                    authToken = resp.body().getToken();
                } else if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
                    Log.d(TAG, "AccountAuthenticator: "
                          + context.getResources().getString(R.string.wrong_credentials));
                }
            }
        }

        if (TextUtils.isEmpty(authToken)) {
            // If we get here, then we couldn't access the user's password - so we
            // need to re-prompt them for their credentials. We do that by creating
            // an intent to display our LoginActivity.
            return getBundleForFailedSignIn(response, account, authTokenType);
        }
        // If we get an authToken - we return it
        final Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        return result;
    }

    private Bundle getBundleForFailedSignIn(
          AccountAuthenticatorResponse response, Account account, String authTokenType) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(ACCOUNT_TYPE_KEY, account.type);
        intent.putExtra(AUTH_TYPE_KEY, authTokenType);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return authTokenType + LABEL;
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
