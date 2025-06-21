package com.tom.meeter.context.profile.settings.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingsCreateOrUpdate {

    @JsonProperty(value = "search_area")
    private Integer searchArea;
    @JsonProperty(value = "need_track_user")
    private Boolean needTrackUser;

    public SettingsCreateOrUpdate(Integer searchArea, Boolean needTrackUser) {
        this.searchArea = searchArea;
        this.needTrackUser = needTrackUser;
    }

    public Integer getSearchArea() {
        return searchArea;
    }

    public Boolean getNeedTrackUser() {
        return needTrackUser;
    }
}
