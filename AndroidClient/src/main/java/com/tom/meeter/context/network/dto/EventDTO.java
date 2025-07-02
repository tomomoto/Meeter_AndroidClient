package com.tom.meeter.context.network.dto;

import static com.tom.meeter.infrastructure.common.JsonHelper.getDoubleOrNull;
import static com.tom.meeter.infrastructure.common.JsonHelper.getOffsetDateTimeOrNull;
import static com.tom.meeter.infrastructure.common.JsonHelper.getStringOrNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private static final String STATUS_KEY = "status";

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
    private EventStatus status;

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
            status = EventStatus.fromString(json.getString(STATUS_KEY));
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

    public void setStatus(EventStatus eventStatus) {
        this.status = eventStatus;
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

    public EventStatus getStatus() {
        return status;
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
              && Objects.equals(city, eventDTO.city)
              && Objects.equals(status, eventDTO.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
              super.hashCode(), creatorId, created,
              description, latitude, longitude,
              starting, ending, city, status);
    }


    private static final String CREATED_VALUE = "CREATED";
    private static final String PUBLISHED_VALUE = "PUBLISHED";
    private static final String UNPUBLISHED_VALUE = "UNPUBLISHED";
    private static final String SCHEDULED_VALUE = "SCHEDULED";
    private static final String STARTED_VALUE = "STARTED";
    private static final String PAUSED_VALUE = "PAUSED";
    private static final String RESUMED_VALUE = "RESUMED";
    private static final String FINISHED_VALUE = "FINISHED";
    private static final String CANCELLED_VALUE = "CANCELLED";
    private static final String ARCHIVED_VALUE = "ARCHIVED";

    public enum EventStatus {

        @JsonProperty(CREATED_VALUE)
        CREATED(CREATED_VALUE),
        @JsonProperty(PUBLISHED_VALUE)
        PUBLISHED(PUBLISHED_VALUE),
        @JsonProperty(UNPUBLISHED_VALUE)
        UNPUBLISHED(UNPUBLISHED_VALUE),
        @JsonProperty(SCHEDULED_VALUE)
        SCHEDULED(SCHEDULED_VALUE),
        @JsonProperty(STARTED_VALUE)
        STARTED(STARTED_VALUE),
        @JsonProperty(PAUSED_VALUE)
        PAUSED(PAUSED_VALUE),
        @JsonProperty(RESUMED_VALUE)
        RESUMED(RESUMED_VALUE),
        @JsonProperty(FINISHED_VALUE)
        FINISHED(FINISHED_VALUE),
        @JsonProperty(CANCELLED_VALUE)
        CANCELLED(CANCELLED_VALUE),
        @JsonProperty(ARCHIVED_VALUE)
        ARCHIVED(ARCHIVED_VALUE);

        private final String value;

        EventStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static EventStatus fromString(String text) {
            for (EventStatus val : EventStatus.values()) {
                if (val.value.equalsIgnoreCase(text)) {
                    return val;
                }
            }
            throw new IllegalArgumentException("No enum constant with string value " + text);
        }

        public static Set<String> transformToStrings(
              Set<EventDTO.EventStatus> statuses) {
            Set<String> result = new HashSet<>(statuses.size());
            for (EventDTO.EventStatus status : statuses) {
                result.add(status.getValue());
            }
            return result;
        }

        public static Set<EventDTO.EventStatus> transformToEnums(
              Set<String> statuses) {
            Set<EventDTO.EventStatus> result = new HashSet<>(statuses.size());
            for (String status : statuses) {
                result.add(EventDTO.EventStatus.fromString(status));
            }
            return result;
        }
    }
}
