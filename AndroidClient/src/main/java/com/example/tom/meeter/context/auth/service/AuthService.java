package com.example.tom.meeter.context.auth.service;

import com.example.tom.meeter.context.auth.login.message.LoginBody;
import com.example.tom.meeter.context.auth.message.TokenResponse;
import com.example.tom.meeter.context.auth.registration.message.RegisterBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/sign-in")
    Call<TokenResponse> login(@Body LoginBody login);

    @POST("/auth/sign-up")
    Call<TokenResponse> register(@Body RegisterBody register);
}