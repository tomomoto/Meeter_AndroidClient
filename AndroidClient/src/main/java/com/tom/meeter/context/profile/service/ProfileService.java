package com.tom.meeter.context.profile.service;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.user.domain.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ProfileService {
    @GET("/profile")
    Call<User> getProfile(@Header(AUTH_HEADER) String authHeader);

    @GET("/profile/events")
    Call<List<EventDTO>> getProfileEvents(@Header(AUTH_HEADER) String authHeader);
}
