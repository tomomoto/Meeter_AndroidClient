package com.example.tom.meeter.context.auth.registration.message;

public class RegisterBody {
    private String name;
    private String surname;
    private String gender;
    private String login;
    private String password;
    private String info;
    private String birthday;

    public RegisterBody() {
    }

    public RegisterBody(
          String name, String surname, String gender, String login,
          String password, String info, String birthday) {
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.login = login;
        this.password = password;
        this.info = info;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
