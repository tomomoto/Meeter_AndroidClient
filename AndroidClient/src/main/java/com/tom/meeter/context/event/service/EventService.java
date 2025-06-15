package com.tom.meeter.context.event.service;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import com.tom.meeter.context.network.dto.EventDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface EventService {
    @GET("/event/{id}")
    Call<EventDTO> getEvent(@Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);
}
