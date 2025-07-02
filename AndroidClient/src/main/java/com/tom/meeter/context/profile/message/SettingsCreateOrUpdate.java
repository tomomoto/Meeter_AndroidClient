package com.tom.meeter.context.profile.message;

import static com.tom.meeter.context.profile.message.SettingsResponse.NEED_TRACK_USER_KEY;
import static com.tom.meeter.context.profile.message.SettingsResponse.SEARCH_AREA_KEY;
import static com.tom.meeter.context.profile.message.SettingsResponse.VISIBLE_EVENT_STATUSES_KEY;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tom.meeter.context.network.dto.EventDTO;

import java.util.Set;

public class SettingsCreateOrUpdate {

    @JsonProperty(value = SEARCH_AREA_KEY)
    private Integer searchArea;
    @JsonProperty(value = NEED_TRACK_USER_KEY)
    private Boolean needTrackUser;
    @JsonProperty(value = VISIBLE_EVENT_STATUSES_KEY)
    private Set<EventDTO.EventStatus> visibleEventStatuses;

    public SettingsCreateOrUpdate(
          Integer searchArea, Boolean needTrackUser) {
        this.searchArea = searchArea;
        this.needTrackUser = needTrackUser;
    }

    public SettingsCreateOrUpdate(
          Set<EventDTO.EventStatus> visibleEventStatuses) {
        this.visibleEventStatuses = visibleEventStatuses;
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
