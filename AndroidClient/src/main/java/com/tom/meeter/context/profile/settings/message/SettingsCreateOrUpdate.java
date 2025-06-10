package com.tom.meeter.context.profile.settings.message;

import com.google.gson.annotations.SerializedName;

public class SettingsCreateOrUpdate {

    @SerializedName(value = "search_area")
    private Integer searchArea;
    @SerializedName(value = "need_track_user")
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
