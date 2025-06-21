package com.tom.meeter.context.event.utils;

import static com.tom.meeter.infrastructure.common.CommonHelper.getFloatOrNull;
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
          EventDTO event, ActivityEventEditableBinding eBinding) {
        UpdateEventRequest req = new UpdateEventRequest();

        String eventNameChange = getStringOrNull(eBinding.eventName.getText());
        if (!Objects.equals(event.getName(), eventNameChange)) {
            req.setName(eventNameChange);
        }
        String eventDescrChange = getStringOrNull(eBinding.eventDescription.getText());
        if (!Objects.equals(event.getDescription(), eventDescrChange)) {
            req.setDescription(eventDescrChange);
        }
        OffsetDateTime eventStartingChange = getOffsetDateTime(eBinding.eventStarting.getText());
        if (!Objects.equals(event.getStarting(), eventStartingChange)) {
            req.setStarting(eventStartingChange);
        }
        OffsetDateTime eventEndingChange = getOffsetDateTime(eBinding.eventEnding.getText());
        if (!Objects.equals(event.getEnding(), eventEndingChange)) {
            req.setEnding(eventEndingChange);
        }
        String eventCityChange = getStringOrNull(eBinding.eventCity.getText());
        if (!Objects.equals(event.getCity(), eventCityChange)) {
            req.setCity(eventCityChange);
        }
        //TODO FLOAT -> DOUBLE
        Float eventLatitudeChange = getFloatOrNull(eBinding.eventLatitude.getText());
        if (!Objects.equals(event.getLatitude(), eventLatitudeChange)) {
            req.setLatitude(eventLatitudeChange);
        }
        //TODO FLOAT -> DOUBLE
        Float eventLongitudeChange = getFloatOrNull(eBinding.eventLongitude.getText());
        if (!Objects.equals(event.getLongitude(), eventLongitudeChange)) {
            req.setLongitude(eventLongitudeChange);
        }
        //TODO: eventCache.getPhotoPath();
        return req;
    }
}
