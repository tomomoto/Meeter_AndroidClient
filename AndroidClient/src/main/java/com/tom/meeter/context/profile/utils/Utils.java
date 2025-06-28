package com.tom.meeter.context.profile.utils;

import static com.tom.meeter.infrastructure.common.CommonHelper.getDoubleOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.getLocalDateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.getStringOrNull;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.message.CreateEventRequest;
import com.tom.meeter.context.profile.message.UpdateProfileRequest;
import com.tom.meeter.databinding.FragmentCreateEventBinding;
import com.tom.meeter.databinding.FragmentProfileBinding;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class Utils {
    private Utils() {
    }

    public static UpdateProfileRequest createUpdateProfileRequest(
          FragmentProfileBinding binding, UserDTO user) {
        UpdateProfileRequest req = new UpdateProfileRequest();

        String nameChange = getStringOrNull(binding.name.getText());
        if (!Objects.equals(user.getName(), nameChange)) {
            req.setName(nameChange);
        }
        String surnameChange = getStringOrNull(binding.surname.getText());
        if (!Objects.equals(user.getSurname(), surnameChange)) {
            req.setSurname(surnameChange);
        }
        LocalDate birthdayChange = getLocalDateOrNull(binding.birthday.getText());
        if (!Objects.equals(user.getBirthday(), birthdayChange)) {
            req.setBirthday(birthdayChange);
        }
        String infoChange = getStringOrNull(binding.info.getText());
        if (!Objects.equals(user.getInfo(), infoChange)) {
            req.setInfo(infoChange);
        }
        String photoPathChange = getStringOrNull(binding.photoPath.getText());
        if (!Objects.equals(user.getPhotoPath(), photoPathChange)) {
            req.setPhotoPath(photoPathChange);
        }
        return req;
    }

    public static CreateEventRequest createEventRequest(
          FragmentCreateEventBinding binding) {
        //TODO in case of nulls...
        String startDate = binding.startsDate.getText().toString();
        String startTime = binding.startsTime.getText().toString();
        LocalDate localStartDate = LocalDate.parse(startDate);
        LocalTime localStartTime = LocalTime.parse(startTime);

        String endDate = binding.endsDate.getText().toString();
        String endTime = binding.endsTime.getText().toString();
        LocalDate localEndDate = LocalDate.parse(endDate);
        LocalTime localEndTime = LocalTime.parse(endTime);

        ZoneOffset offset = OffsetDateTime.now().getOffset();
        OffsetDateTime starts = OffsetDateTime.of(localStartDate, localStartTime, offset);
        OffsetDateTime ends = OffsetDateTime.of(localEndDate, localEndTime, offset);

        return new CreateEventRequest(
              getStringOrNull(binding.name.getText()),
              getStringOrNull(binding.newEventDescriptionEditText.getText()),
              starts,
              ends,
              getStringOrNull(binding.city.getText()),
              getDoubleOrNull(binding.latitude.getText()),
              getDoubleOrNull(binding.longitude.getText()),
              null//getStringOrNull(binding.photoPath.getText())
        );
    }
}
