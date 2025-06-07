package com.example.tom.meeter.context.auth.message;

public class RegisterBody {
    private String login;
    private String password;
    private String name;
    private String gender;

    public RegisterBody() {
    }

    public RegisterBody(String login, String password, String name, String gender) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

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
}
