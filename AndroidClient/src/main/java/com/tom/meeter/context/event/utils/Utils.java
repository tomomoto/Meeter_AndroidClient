package com.tom.meeter.context.event.utils;

import static com.tom.meeter.infrastructure.common.CommonHelper.getDoubleOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.getOffsetDateTime;
import static com.tom.meeter.infrastructure.common.CommonHelper.getStringOrNull;

import com.tom.meeter.context.event.message.UpdateEventRequest;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.ActivityEventEditableBinding;

import java.time.OffsetDateTime;
import java.util.Objects;

public class Utils {

    private Utils() {
    }

    public static UpdateEventRequest createUpdateEventRequest(
          EventDTO event, ActivityEventEditableBinding binding) {
        UpdateEventRequest req = new UpdateEventRequest();

        String eventNameChange = getStringOrNull(binding.name.getText());
        if (!Objects.equals(event.getName(), eventNameChange)) {
            req.setName(eventNameChange);
        }
        String eventDescrChange = getStringOrNull(binding.description.getText());
        if (!Objects.equals(event.getDescription(), eventDescrChange)) {
            req.setDescription(eventDescrChange);
        }
        OffsetDateTime eventStartingChange = getOffsetDateTime(binding.starting.getText());
        if (!Objects.equals(event.getStarting(), eventStartingChange)) {
            req.setStarting(eventStartingChange);
        }
        OffsetDateTime eventEndingChange = getOffsetDateTime(binding.ending.getText());
        if (!Objects.equals(event.getEnding(), eventEndingChange)) {
            req.setEnding(eventEndingChange);
        }
        String eventCityChange = getStringOrNull(binding.city.getText());
        if (!Objects.equals(event.getCity(), eventCityChange)) {
            req.setCity(eventCityChange);
        }
        Double eventLatitudeChange = getDoubleOrNull(binding.latitude.getText());
        if (!Objects.equals(event.getLatitude(), eventLatitudeChange)) {
            req.setLatitude(eventLatitudeChange);
        }
        Double eventLongitudeChange = getDoubleOrNull(binding.longitude.getText());
        if (!Objects.equals(event.getLongitude(), eventLongitudeChange)) {
            req.setLongitude(eventLongitudeChange);
        }
        String photoPathChange = getStringOrNull(binding.photoPath.getText());
        if (!Objects.equals(event.getPhotoPath(), photoPathChange)) {
            req.setPhotoPath(photoPathChange);
        }
        return req;
    }
}
