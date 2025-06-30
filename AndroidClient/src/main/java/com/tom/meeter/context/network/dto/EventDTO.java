package com.tom.meeter.context.network.dto;

import static com.tom.meeter.infrastructure.common.JsonHelper.getDoubleOrNull;
import static com.tom.meeter.infrastructure.common.JsonHelper.getOffsetDateTimeOrNull;
import static com.tom.meeter.infrastructure.common.JsonHelper.getStringOrNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * created by Tom on 10.02.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDTO extends ServerEntityBase {

    private static final String DESCRIPTION_KEY = "description";
    private static final String CREATOR_ID_KEY = "creator_id";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String CREATED_KEY = "created";
    private static final String STARTING_KEY = "starting";
    private static final String ENDING_KEY = "ending";
    private static final String CITY_KEY = "city";

    //Non nullable, cannot be changed
    @JsonProperty(value = CREATOR_ID_KEY)
    private String creatorId;
    private OffsetDateTime created;

    //Nullable
    private String description;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime starting;
    private OffsetDateTime ending;
    private String city;

    public EventDTO() {
        //retrofit...
    }

    public EventDTO(JSONObject json) {
        super(json);
        try {
            //Non nullable.
            creatorId = json.getString(CREATOR_ID_KEY);
            created = OffsetDateTime.parse(json.getString(CREATED_KEY));

            //Nullable.
            description = getStringOrNull(DESCRIPTION_KEY, json);
            latitude = getDoubleOrNull(LATITUDE_KEY, json);
            longitude = getDoubleOrNull(LONGITUDE_KEY, json);
            starting = getOffsetDateTimeOrNull(STARTING_KEY, json);
            ending = getOffsetDateTimeOrNull(ENDING_KEY, json);
            city = getStringOrNull(CITY_KEY, json);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to encode EventDTO from jsonObject: ", e);
        }
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EventDTO eventDTO = (EventDTO) o;
        return Objects.equals(creatorId, eventDTO.creatorId)
              && Objects.equals(created, eventDTO.created)
              && Objects.equals(description, eventDTO.description)
              && Objects.equals(latitude, eventDTO.latitude)
              && Objects.equals(longitude, eventDTO.longitude)
              && Objects.equals(starting, eventDTO.starting)
              && Objects.equals(ending, eventDTO.ending)
              && Objects.equals(city, eventDTO.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
              super.hashCode(), creatorId, created,
              description, latitude, longitude,
              starting, ending, city);
    }
}
