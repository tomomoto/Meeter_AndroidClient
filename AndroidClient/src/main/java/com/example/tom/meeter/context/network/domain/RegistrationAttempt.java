package com.example.tom.meeter.context.network.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationAttempt implements NetworkEvent {

  private String name;
  private String surname;
  private String gender;
  private String login;
  private String password;
  private String info;
  private String birthday;

  public RegistrationAttempt(String name, String surname, String gender, String login, String password, String info,
      String birthday) {
    this.name = name;
    this.surname = surname;
    this.gender = gender;
    this.login = login;
    this.password = password;
    this.info = info;
    this.birthday = birthday;
  }

  @Override
  public JSONObject toJson() throws JSONException {
    return new JSONObject()
        .put("name", name)
        .put("surname", surname)
        .put("gender", gender)
        .put("login", login)
        .put("password", password)
        .put("info", info)
        .put("birthday", birthday);
  }
}
