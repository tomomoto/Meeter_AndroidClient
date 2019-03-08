package com.example.tom.meeter.context.network.service;

public class NetworkBinder extends android.os.Binder {

    private NetworkService networkService;

    public NetworkBinder(NetworkService networkService) {
        this.networkService = networkService;
    }

    public NetworkService getService() {
        return networkService;
    }
}
