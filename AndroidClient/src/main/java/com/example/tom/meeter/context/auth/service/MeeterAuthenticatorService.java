package com.example.tom.meeter.context.auth.service;

import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.tom.meeter.context.auth.infrastructure.AccountAuthenticator;

public class MeeterAuthenticatorService extends Service {
    private static final String TAG = MeeterAuthenticatorService.class.getCanonicalName();

    public MeeterAuthenticatorService() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logMethod(TAG, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logMethod(TAG, this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        logMethod(TAG, this);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        logMethod(TAG, this);
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        logMethod(TAG, this);
        AccountAuthenticator authenticator = new AccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}