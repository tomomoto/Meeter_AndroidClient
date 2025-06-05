package com.example.tom.meeter.context.auth.service;

import com.example.tom.meeter.context.auth.login.message.LoginBody;
import com.example.tom.meeter.context.auth.login.message.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/sign-in")
    Call<LoginResponse> login(@Body LoginBody loginBody);
}