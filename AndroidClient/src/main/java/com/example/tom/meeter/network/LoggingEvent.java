package com.example.tom.meeter.network;

/**
 * Created by Tom on 14.01.2017.
 */

public class LoggingEvent {
    private String UserLogin;
    private String UserPassword;

    public String getUserLogin() {
        return UserLogin;
    }

    public void setUserLogin(String userLogin) {
        UserLogin = userLogin;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public LoggingEvent(String userLogin, String userPassword) {
        UserLogin = userLogin;
        UserPassword = userPassword;
    }
}
