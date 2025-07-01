package com.tom.meeter.context.profile.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingsResponse {

    private String id;
    @JsonProperty(value = "user_id")
    private String userId;
    @JsonProperty(value = "search_area")
    private Integer searchArea;
    @JsonProperty(value = "need_track_user")
    private Boolean needTrackUser;

    public SettingsResponse() {
        //Jackson requires empty c-tor
    }

    public SettingsResponse(String id, String userId, Integer searchArea, Boolean needTrackUser) {
        this.id = id;
        this.userId = userId;
        this.searchArea = searchArea;
        this.needTrackUser = needTrackUser;
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
}
