package com.example.tom.meeter.context.network.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 14.01.2017.
 */

public class LoginAttempt implements NetworkEvent {

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

    public LoginAttempt(String login, String password) {
        this.login = login;
        this.password = password;
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
                .put("login", login)
                .put("password", password);
    }
}
