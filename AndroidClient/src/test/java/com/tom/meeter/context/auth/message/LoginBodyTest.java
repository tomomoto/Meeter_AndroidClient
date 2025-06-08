package com.tom.meeter.context.auth.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LoginBodyTest {
    @Test
    public void canReturnLoginAndPassword() {
        LoginBody target = new LoginBody("login", "password");
        assertEquals("login", target.getLogin());
        assertEquals("password", target.getPassword());
    }
}