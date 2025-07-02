package com.tom.meeter.context.event.utils;

import static com.tom.meeter.infrastructure.common.CommonHelper.getDoubleOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.getOffsetDateTime;
import static com.tom.meeter.infrastructure.common.CommonHelper.getStringOrNull;

import android.accounts.AccountManager;
import android.util.Log;

import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.event.message.UpdateEventRequest;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.ActivityEventEditableBinding;
import com.tom.meeter.databinding.ActivityEventPublishBinding;

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

    public static UpdateEventRequest createPublishEventRequest(
          EventDTO event, ActivityEventPublishBinding binding) {
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

    public static boolean currentUserIsEventCreator(
          AccountManager am, EventDTO event) {
        return AuthHelper.getUserUuid(am).equals(event.getCreatorId());
    }

    public static void dumpEventDispatcherError(
          String tag, AccountManager am, EventDTO event) {
        Log.e(tag, "System error. EventDispatcher did wrong dispatching. " +
              "Current user is [" + AuthHelper.getUserUuid(am) + "], " +
              "eventId [" + event.getId() + "], eventCreatorId [" + event.getCreatorId() + "]. " +
              "Please, check server code and related entities.");
    }
}
