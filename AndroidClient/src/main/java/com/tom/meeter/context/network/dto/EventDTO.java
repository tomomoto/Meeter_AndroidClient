package com.tom.meeter.context.network.dto;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * created by Tom on 10.02.2017.
 */
public class EventDTO {

    private static final String EVENT_ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String DESCRIPTION_KEY = "description";
    private static final String CREATOR_ID_KEY = "creator_id";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String CREATED_KEY = "created";
    private static final String STARTING_KEY = "starting";
    private static final String ENDING_KEY = "ending";
    private static final String PHOTO_PATH_KEY = "photo_path";
    private static final String CITY_KEY = "city";

    private String id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    @JsonProperty(value = CREATOR_ID_KEY)
    private String creatorId;
    private OffsetDateTime created;
    private OffsetDateTime starting;
    private OffsetDateTime ending;
    private String city;
    @JsonProperty(value = PHOTO_PATH_KEY)
    private String photoPath;

    public static EventDTO encode(JSONObject json) {
        EventDTO result = new EventDTO();
        try {
            //Non nullable.
            result.id = json.getString(EVENT_ID_KEY);
            result.name = json.getString(NAME_KEY);
            result.creatorId = json.getString(CREATOR_ID_KEY);
            result.created = OffsetDateTime.parse(json.getString(CREATED_KEY));

            //Nullable.
            result.description = getStringOrNull(DESCRIPTION_KEY, json);
            result.latitude = getDoubleOrNull(LATITUDE_KEY, json);
            result.longitude = getDoubleOrNull(LONGITUDE_KEY, json);
            result.starting = getOffsetDateTimeOrNull(STARTING_KEY, json);
            result.ending = getOffsetDateTimeOrNull(ENDING_KEY, json);
            result.photoPath = getStringOrNull(PHOTO_PATH_KEY, json);
            result.city = getStringOrNull(CITY_KEY, json);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to encode EventDTO from jsonObject: ", e);
        }
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public void setStarting(OffsetDateTime starting) {
        this.starting = starting;
    }

    public void setEnding(OffsetDateTime ending) {
        this.ending = ending;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public OffsetDateTime getStarting() {
        return starting;
    }

    public OffsetDateTime getEnding() {
        return ending;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventDTO eventDTO = (EventDTO) o;
        return Objects.equals(id, eventDTO.id)
              && Objects.equals(name, eventDTO.name)
              && Objects.equals(description, eventDTO.description)
              && Objects.equals(latitude, eventDTO.latitude)
              && Objects.equals(longitude, eventDTO.longitude)
              && Objects.equals(creatorId, eventDTO.creatorId)
              && Objects.equals(created, eventDTO.created)
              && Objects.equals(starting, eventDTO.starting)
              && Objects.equals(ending, eventDTO.ending)
              && Objects.equals(city, eventDTO.city)
              && Objects.equals(photoPath, eventDTO.photoPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
              id, name, description, latitude, longitude, creatorId,
              created, starting, ending, city, photoPath);
    }

    @Nullable
    private static String getStringOrNull(String key, JSONObject json) throws JSONException {
        return json.isNull(key) ? null : json.getString(key);
    }

    @Nullable
    private static OffsetDateTime getOffsetDateTimeOrNull(
          String key, JSONObject json) throws JSONException {
        return json.isNull(key) ? null : OffsetDateTime.parse(json.getString(key));
    }

    @Nullable
    private static Double getDoubleOrNull(String key, JSONObject json) throws JSONException {
        return json.isNull(key) ? null : json.getDouble(key);
    }
}
