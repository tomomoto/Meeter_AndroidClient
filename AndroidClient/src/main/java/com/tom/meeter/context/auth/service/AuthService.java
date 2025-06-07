package com.tom.meeter.context.auth.service;

import com.tom.meeter.context.auth.message.LoginBody;
import com.tom.meeter.context.auth.message.RegisterBody;
import com.tom.meeter.context.auth.message.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/sign-in")
    Call<TokenResponse> login(@Body LoginBody login);

    @POST("/auth/sign-up")
    Call<TokenResponse> register(@Body RegisterBody register);
}