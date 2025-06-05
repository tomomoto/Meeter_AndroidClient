package com.example.tom.meeter.context.auth.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.tom.meeter.context.auth.infrastructure.AccountAuthenticator;

public class MeeterAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator authenticator = new AccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}