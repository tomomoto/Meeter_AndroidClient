package com.tom.meeter.context.auth.service;

import static com.tom.meeter.infrastructure.common.GlobalConstants.AUTH_HEADER;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Purpose is to check the token and fail auth in case of failed authorization.
 */
public interface TokenService {
    //TODO change GET path to '/check-token' when backend will be done
    @GET("/profile")
    Call<Void> checkToken(@Header(AUTH_HEADER) String authHeader);
}
