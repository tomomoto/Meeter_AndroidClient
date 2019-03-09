package com.example.tom.meeter.context.event.domain;

import com.example.tom.meeter.context.network.EventDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EventService {
    /**
     * @GET declares an HTTP GET request
     * @Path("id") annotation on the eventId parameter marks it as a
     * replacement for the {id} placeholder in the @GET path
     */
    @GET("/event/{id}")
    Call<EventDTO> getEvent(@Path("id") String eventId);

    @GET("/events/createdBy/{id}")
    Call<List<EventDTO>> getEventsByCreatorId(@Path("id") String userId);
}