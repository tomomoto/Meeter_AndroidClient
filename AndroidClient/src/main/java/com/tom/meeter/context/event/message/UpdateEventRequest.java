package com.tom.meeter.context.event.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Optional;

public class UpdateEventRequest {
    private final String PHOTO_PATH_KEY = "photo_path";

    private Optional<String> name;
    private Optional<String> description;
    private Optional<OffsetDateTime> starting;
    private Optional<OffsetDateTime> ending;
    private Optional<String> city;
    private Optional<Float> latitude;
    private Optional<Float> longitude;
    @JsonProperty(value = PHOTO_PATH_KEY)
    private Optional<String> photoPath;

    public UpdateEventRequest() {
    }

    public Optional<String> getName() {
        return name;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Optional<OffsetDateTime> getStarting() {
        return starting;
    }

    public Optional<OffsetDateTime> getEnding() {
        return ending;
    }

    public Optional<String> getCity() {
        return city;
    }

    public Optional<Float> getLatitude() {
        return latitude;
    }

    public Optional<Float> getLongitude() {
        return longitude;
    }

    public Optional<String> getPhotoPath() {
        return photoPath;
    }

    public void setName(String name) {
        this.name = Optional.ofNullable(name);
    }

    public void setDescription(String description) {
        this.description = Optional.ofNullable(description);
    }

    public void setStarting(OffsetDateTime starting) {
        this.starting = Optional.ofNullable(starting);
    }

    public void setEnding(OffsetDateTime ending) {
        this.ending = Optional.ofNullable(ending);
    }

    public void setCity(String city) {
        this.city = Optional.ofNullable(city);
    }

    public void setLatitude(Float latitude) {
        this.latitude = Optional.ofNullable(latitude);
    }

    public void setLongitude(Float longitude) {
        this.longitude = Optional.ofNullable(longitude);
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = Optional.ofNullable(photoPath);
    }

    public boolean isEmpty() {
        return name == null && description == null && starting == null && ending == null
              && city == null && latitude == null && longitude == null && photoPath == null;
    }

/*


    private final String NAME_KEY = "name";
    private final String DESCR_KEY = "description";
    private final String STARTING_KEY = "starting";
    private final String ENDING_KEY = "ending";
    private final String CITY_KEY = "city";
    private final String LATITUDE_KEY = "latitude";
    private final String LONGITUDE_KEY = "longitude";


    public Map<String, Object> toUpdateMap() {
        Map<String, Object> result = new HashMap<>();
        putIfNotNull(name, NAME_KEY, result);
        putIfNotNull(description, DESCR_KEY, result);
        putIfNotNull(starting, STARTING_KEY, result);
        putIfNotNull(ending, ENDING_KEY, result);
        putIfNotNull(city, CITY_KEY, result);
        putIfNotNull(latitude, LATITUDE_KEY, result);
        putIfNotNull(longitude, LONGITUDE_KEY, result);
        putIfNotNull(photoPath, PHOTO_PATH_KEY, result);
        return result;
    }

    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        putIfNotNull(name, NAME_KEY, result);
        putIfNotNull(description, DESCR_KEY, result);
        putIfNotNull(starting, STARTING_KEY, result);
        putIfNotNull(ending, ENDING_KEY, result);
        putIfNotNull(city, CITY_KEY, result);
        putIfNotNull(latitude, LATITUDE_KEY, result);
        putIfNotNull(longitude, LONGITUDE_KEY, result);
        putIfNotNull(photoPath, PHOTO_PATH_KEY, result);
        return result;
    }

    private void putIfNotNull(Optional<?> field, String key, Map<String, Object> result) {
        if (field != null) {
            result.put(key, field.orElse(null));
        }
    }

    private void putIfNotNull(Optional<?> field, String key, JSONObject result) {
        if (field != null) {
            if (field.isPresent()) {
                try {
                    result.put(key, field.get());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    result.put(key, JSONObject.NULL);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }*/
}
