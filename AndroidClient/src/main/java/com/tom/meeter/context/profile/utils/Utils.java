package com.tom.meeter.context.profile.utils;

import static com.tom.meeter.infrastructure.common.CommonHelper.getLocalDateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.getStringOrNull;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.message.UpdateProfileRequest;
import com.tom.meeter.databinding.FragmentProfileBinding;

import java.time.LocalDate;
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
}
