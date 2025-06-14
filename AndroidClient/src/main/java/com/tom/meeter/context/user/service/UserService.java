package com.tom.meeter.context.user.service;

import static com.tom.meeter.infrastructure.common.Constants.AUTH_HEADER;

import com.tom.meeter.context.profile.event.domain.Event;
import com.tom.meeter.context.profile.user.domain.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface UserService {
    //This will not even work, since server needs an auth for thi requests.
    @GET("/user/{id}")
    @Deprecated
    Call<User> getUser(@Path("id") String userId);

    @GET("/user/{id}")
    Call<User> getUser(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);

    @GET("/user/{id}/events")
    Call<List<Event>> getUserEvents(@Header(AUTH_HEADER) String authHeader, @Path("id") String userId);
}
