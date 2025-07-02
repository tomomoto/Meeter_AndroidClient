package com.tom.meeter.context.event.service;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import com.tom.meeter.context.event.message.ScheduleEventRequest;
import com.tom.meeter.context.event.message.UpdateEventRequest;
import com.tom.meeter.context.network.dto.EventDTO;

import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventService {

    @GET("/event/{id}")
    Call<EventDTO> getEvent(@Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @PATCH("/event/{id}")
    Call<EventDTO> updateEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId,
          @Body UpdateEventRequest req);

    @DELETE("/event/{id}")
    Call<Void> deleteEvent(@Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @GET("/event/{id}/am_i_creator")
    Call<Boolean> amICreator(@Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @GET("/event/{id}/available-transitions")
    Call<Set<EventDTO.EventStatus>> availableTransitions(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @POST("/event/{id}/publish")
    Call<EventDTO> publishEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId,
          @Body UpdateEventRequest req);

    @POST("/event/{id}/schedule")
    Call<EventDTO> scheduleEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId,
          @Body ScheduleEventRequest req);

    @POST("/event/{id}/unpublish")
    Call<EventDTO> unpublishEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @POST("/event/{id}/start")
    Call<EventDTO> startEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @POST("/event/{id}/pause")
    Call<EventDTO> pauseEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @POST("/event/{id}/resume")
    Call<EventDTO> resumeEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @POST("/event/{id}/finish")
    Call<EventDTO> finishEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @POST("/event/{id}/cancel")
    Call<EventDTO> cancelEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

    @POST("/event/{id}/archive")
    Call<EventDTO> archiveEvent(
          @Header(AUTH_HEADER) String authHeader, @Path("id") String eventId);

}
