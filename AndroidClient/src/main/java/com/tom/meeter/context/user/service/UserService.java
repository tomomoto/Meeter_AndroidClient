package com.tom.meeter.context.user.service;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface UserService {

    @GET("/user/{id}")
    Call<UserDTO> getUser(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);

    @GET("/user/{id}/events")
    Call<List<EventDTO>> getUserEvents(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);

    @GET("/user/{id}/am_i_subscribed")
    Call<Boolean> amISubscribed(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);

    @GET("/user/{id}/subscribe")
    Call<Void> subscribe(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);

    @GET("/user/{id}/unsubscribe")
    Call<Void> unsubscribe(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);

    @GET("/user/{id}/subscribers")
    Call<List<UserDTO>> getSubscribers(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String userId);

    @GET("/user/{id}/subscriptions")
    Call<List<UserDTO>> getSubscriptions(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String userId);
}
