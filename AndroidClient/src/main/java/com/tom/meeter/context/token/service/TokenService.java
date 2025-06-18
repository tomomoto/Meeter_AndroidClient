package com.tom.meeter.context.token.service;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Purpose is to check the token and fail-fast the request ASAP, starting auth process
 * for requesting new token or even new credentials in case of failed authentication.
 */
public interface TokenService {
    @GET("/api/token_check")
    Call<Void> checkToken(@Header(AUTH_HEADER) String authHeader);
}
