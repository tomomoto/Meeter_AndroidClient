package com.tom.meeter.context.auth.infrastructure;

import android.accounts.Account;
import android.accounts.AccountManager;

public final class AuthHelper {
    private AuthHelper() {
    }

    public static String peekToken(AccountManager am) {
        Account[] accounts = am.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
        if (accounts.length == 1) {
            return am.peekAuthToken(accounts[0], AccountAuthenticator.AUTH_TYPE);
        } else {
            throw new RuntimeException("Multiple accounts are not supported yet.");
        }
    }
}
