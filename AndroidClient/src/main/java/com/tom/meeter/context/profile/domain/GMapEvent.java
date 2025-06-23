package com.tom.meeter.context.profile.domain;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.tom.meeter.context.network.dto.EventDTO;

import java.util.Objects;

public class GMapEvent {

    private final EventDTO event;
    private Marker marker;

    public GMapEvent(EventDTO event, Marker marker) {
        validate(event, marker);
        this.event = event;
        this.marker = marker;
    }

    public void removeMarker() {
        marker.remove();
    }

    public EventDTO getEvent() {
        return event;
    }

    public String getName() {
        return event.getName();
    }

    public boolean isInfoWindowShown() {
        return marker.isInfoWindowShown();
    }

    public String getCreatorId() {
        return event.getCreatorId();
    }

    public String getId() {
        return event.getId();
    }

    public double getLatitude() {
        return event.getLatitude();
    }

    public double getLongitude() {
        return event.getLongitude();
    }

    public String getMarkerId() {
        return marker.getId();
    }

    public void updateName(String name) {
        event.setName(name);
        marker.setTitle(name);
    }

    public void updatePosition(Float latitude, Float longitude) {
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        marker.setPosition(new LatLng(latitude, longitude));
    }

    public void replaceMarker(Marker marker) {
        validate(event, marker);
        this.marker = marker;
    }

    private static void validate(EventDTO event, Marker marker) {
        String name = event.getName();
        String title = marker.getTitle();
        if (!name.equals(title)) {
            throw new IllegalArgumentException("Names are not equals " + name + ":" + title);
        }
        LatLng position = marker.getPosition();
        double latitude = event.getLatitude();
        if (latitude != position.latitude) {
            throw new IllegalArgumentException(
                  "Latitudes are not equals " + latitude + ":" + position.latitude);
        }
        double longitude = event.getLongitude();
        if (longitude != position.longitude) {
            throw new IllegalArgumentException(
                  "Longitude are not equals " + longitude + ":" + position.longitude);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GMapEvent gMapEvent = (GMapEvent) o;
        return Objects.equals(event, gMapEvent.event)
              && Objects.equals(marker, gMapEvent.marker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, marker);
    }
}
