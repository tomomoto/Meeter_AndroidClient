package com.tom.meeter.context.auth.infrastructure;

import android.accounts.Account;
import android.accounts.AccountManager;

public final class AuthHelper {

    public static final RuntimeException NOT_IMPLEMENTED = new RuntimeException("Multiple accounts are not supported yet.");

    private AuthHelper() {
    }

    public static String peekToken(AccountManager am) {
        Account[] accounts = am.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
        if (accounts.length != 1) {
            throw NOT_IMPLEMENTED;
        }
        return am.peekAuthToken(accounts[0], AccountAuthenticator.AUTH_TYPE);
    }

    public static void setToken(AccountManager am, String token) {
        Account[] accounts = am.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
        if (accounts.length != 1) {
            throw NOT_IMPLEMENTED;
        }
        am.setAuthToken(accounts[0], AccountAuthenticator.AUTH_TYPE, token);
    }
}
