package com.tom.meeter.context.profile.service;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import com.tom.meeter.context.profile.event.domain.Event;
import com.tom.meeter.context.profile.user.domain.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ProfileService {
    /**
     * @GET declares an HTTP GET request
     * @Path("user") annotation on the userId parameter marks it as a
     * replacement for the {user} placeholder in the @GET path
     */
    @GET("/user/{id}")
    @Deprecated
    Call<User> getUser(@Path("id") String userId);

    @GET("/profile")
    Call<User> getProfile(@Header(AUTH_HEADER) String authHeader);

    @GET("/profile/events")
    Call<List<Event>> getProfileEvents(@Header(AUTH_HEADER) String authHeader);
}
