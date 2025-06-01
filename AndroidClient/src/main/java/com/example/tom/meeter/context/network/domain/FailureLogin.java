package com.example.tom.meeter.context.network.domain;

/**
 * Created by Tom on 13.01.2017.
 */
public class FailureLogin {

    private final String message;

    public FailureLogin(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "FailureLogin{" +
                "message='" + message + '\'' +
                '}';
    }
}
