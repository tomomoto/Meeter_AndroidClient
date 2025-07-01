package com.tom.meeter.context.profile.service;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.message.CreateEventRequest;
import com.tom.meeter.context.profile.message.UpdateProfileRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProfileService {

    @GET("/profile")
    Call<UserDTO> getProfile(@Header(AUTH_HEADER) String authHeader);

    @PATCH("/profile/update")
    Call<UserDTO> updateProfile(
          @Header(AUTH_HEADER) String authHeader, @Body UpdateProfileRequest req);

    @GET("/profile/events")
    Call<List<EventDTO>> getProfileEvents(@Header(AUTH_HEADER) String authHeader);

    @GET("/profile/subscribers")
    Call<List<UserDTO>> getMySubscribers(@Header(AUTH_HEADER) String authHeader);

    @GET("/profile/subscriptions")
    Call<List<UserDTO>> getMySubscriptions(@Header(AUTH_HEADER) String authHeader);

    @GET("/user/{id}/subscribe")
    Call<Void> subscribe(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);

    @GET("/user/{id}/unsubscribe")
    Call<Void> unsubscribe(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);

    @POST("/event")
    Call<EventDTO> createEvent(@Header(AUTH_HEADER) String authHeader, @Body CreateEventRequest req);


    @Deprecated
    @GET("/user/{id}")
    Call<UserDTO> getUser(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);
/* soon...
  @GET("/publish")
    Call<EventDTO> publishEvent(
          @Header(AUTH_HEADER) String authHeader, @Body CreateEventRequest req);*/

}
