package com.example.tom.meeter.context.auth.message;

public class LoginBody {
    private String login;
    private String password;

    public LoginBody(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
