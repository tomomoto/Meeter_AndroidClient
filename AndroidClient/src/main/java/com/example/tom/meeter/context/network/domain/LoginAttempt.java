package com.example.tom.meeter.context.network.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 14.01.2017.
 */

public class LoginAttempt implements NetworkEvent {

    private final String LOGIN_KEY = "login";
    private final String PASSWORD_KEY = "password";

    private final String login;
    private final String password;

    public LoginAttempt(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginAttempt{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(LOGIN_KEY, login)
                .put(PASSWORD_KEY, password);
    }
}
