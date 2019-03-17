package com.example.tom.meeter.context.network.domain;

public class RegistrationSuccess {

  private String userId;

  public RegistrationSuccess(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
