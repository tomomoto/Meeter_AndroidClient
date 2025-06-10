package com.tom.meeter.context.profile.settings.message;

import com.google.gson.annotations.SerializedName;

public class SettingsResponse {

    private final String id;
    @SerializedName(value = "user_id")
    private final String userId;
    @SerializedName(value = "search_area")
    private final Integer searchArea;
    @SerializedName(value = "need_track_user")
    private final Boolean needTrackUser;

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
