package com.tom.meeter.context.profile.service;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import com.tom.meeter.context.profile.message.SettingsCreateOrUpdate;
import com.tom.meeter.context.profile.message.SettingsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SettingsService {
    @GET("/settings")
    Call<SettingsResponse> getSettings(@Header(AUTH_HEADER) String authHeader);

    @POST("/settings")
    Call<SettingsResponse> createOrUpdateSettings(
          @Body SettingsCreateOrUpdate req, @Header(AUTH_HEADER) String authHeader);
}
