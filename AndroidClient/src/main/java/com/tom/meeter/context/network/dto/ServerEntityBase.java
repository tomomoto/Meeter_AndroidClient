package com.tom.meeter.context.network.dto;

import static com.tom.meeter.infrastructure.common.JsonHelper.getStringOrNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public abstract class ServerEntityBase implements ServerEntity {

    protected String id;
    protected String name;
    @JsonProperty(PHOTO_PATH_KEY)
    protected String photoPath;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPhotoPath() {
        return photoPath;
    }

    public ServerEntityBase() {
        //retrofit...
    }

    public ServerEntityBase(JSONObject json) {
        try {
            //Non nullable.
            id = json.getString(ID_KEY);
            name = json.getString(NAME_KEY);

            //Nullable.
            photoPath = getStringOrNull(PHOTO_PATH_KEY, json);
        } catch (JSONException e) {
            throw new RuntimeException(
                  "Unable to create ServerEntityBase from jsonObject: ", e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServerEntityBase that = (ServerEntityBase) o;
        return Objects.equals(id, that.id)
              && Objects.equals(name, that.name)
              && Objects.equals(photoPath, that.photoPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, photoPath);
    }
}
