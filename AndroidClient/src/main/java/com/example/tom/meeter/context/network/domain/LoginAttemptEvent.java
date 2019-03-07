package com.example.tom.meeter.context.network.domain;

/**
 * Created by Tom on 14.01.2017.
 */

public class LoginAttemptEvent {

    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginAttemptEvent(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginAttemptEvent{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
