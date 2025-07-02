package com.tom.meeter.context.profile.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tom.meeter.context.network.dto.EventDTO;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingsResponse {

    public static final String ID_KEY = "id";
    public static final String USER_ID_KEY = "user_id";
    public static final String SEARCH_AREA_KEY = "search_area";
    public static final String NEED_TRACK_USER_KEY = "need_track_user";
    public static final String VISIBLE_EVENT_STATUSES_KEY = "visible_event_statuses";

    private final String id;
    private final String userId;
    private final Integer searchArea;
    private final Boolean needTrackUser;
    private final Set<EventDTO.EventStatus> visibleEventStatuses;

    @JsonCreator
    public SettingsResponse(
          @JsonProperty(ID_KEY) String id,
          @JsonProperty(USER_ID_KEY) String userId,
          @JsonProperty(SEARCH_AREA_KEY) Integer searchArea,
          @JsonProperty(NEED_TRACK_USER_KEY) Boolean needTrackUser,
          @JsonProperty(VISIBLE_EVENT_STATUSES_KEY) Set<EventDTO.EventStatus> visibleEventStatuses) {
        this.id = id;
        this.userId = userId;
        this.searchArea = searchArea;
        this.needTrackUser = needTrackUser;
        this.visibleEventStatuses = visibleEventStatuses;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getSearchArea() {
        return searchArea;
    }


    public Boolean getNeedTrackUser() {
        return needTrackUser;
    }

    public Set<EventDTO.EventStatus> getVisibleEventStatuses() {
        return visibleEventStatuses;
    }
}
