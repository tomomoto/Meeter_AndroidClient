package com.tom.meeter.context.profile.message;

import static com.tom.meeter.context.profile.message.SettingsResponse.NEED_TRACK_USER_KEY;
import static com.tom.meeter.context.profile.message.SettingsResponse.SEARCH_AREA_KEY;
import static com.tom.meeter.context.profile.message.SettingsResponse.VISIBLE_EVENT_STATUSES_KEY;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tom.meeter.context.network.dto.EventDTO;

import java.util.Optional;
import java.util.Set;

public class SettingsCreateOrUpdate {

    @JsonProperty(value = SEARCH_AREA_KEY)
    private Optional<Integer> searchArea;
    @JsonProperty(value = NEED_TRACK_USER_KEY)
    private Optional<Boolean> needTrackUser;
    @JsonProperty(value = VISIBLE_EVENT_STATUSES_KEY)
    private Optional<Set<EventDTO.EventStatus>> visibleEventStatuses;

    public Optional<Integer> getSearchArea() {
        return searchArea;
    }

    public void setSearchArea(Integer searchArea) {
        this.searchArea = Optional.ofNullable(searchArea);
    }

    public Optional<Boolean> getNeedTrackUser() {
        return needTrackUser;
    }

    public void setNeedTrackUser(Boolean needTrackUser) {
        this.needTrackUser = Optional.ofNullable(needTrackUser);
    }

    public Optional<Set<EventDTO.EventStatus>> getVisibleEventStatuses() {
        return visibleEventStatuses;
    }

    public void setVisibleEventStatuses(Set<EventDTO.EventStatus> visibleEventStatuses) {
        this.visibleEventStatuses = Optional.ofNullable(visibleEventStatuses);
    }

    public boolean isEmpty() {
        return searchArea == null && needTrackUser == null && visibleEventStatuses == null;
    }
}
