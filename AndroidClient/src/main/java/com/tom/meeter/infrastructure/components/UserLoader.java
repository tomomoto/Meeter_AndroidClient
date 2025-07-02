package com.tom.meeter.infrastructure.components;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import com.tom.meeter.context.network.dto.UserDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface UserLoader {
    @GET("/user/{id}")
    Call<UserDTO> getUser(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);
}
