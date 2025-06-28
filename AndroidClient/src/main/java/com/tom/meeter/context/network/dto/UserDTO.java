package com.tom.meeter.context.network.dto;

import static com.tom.meeter.infrastructure.common.JsonHelper.getLocalDateOrNull;
import static com.tom.meeter.infrastructure.common.JsonHelper.getStringOrNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

public class UserDTO implements EntityBase {

    private static final String USER_ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String GENDER_KEY = "gender";
    private static final String SURNAME_KEY = "surname";
    private static final String INFO_KEY = "info";
    private static final String BIRTHDAY_KEY = "birthday";
    private static final String PHOTO_PATH_KEY = "photo_path";

    private String id;
    private String name;
    private UserGender gender;
    private String surname;
    private String info;
    private LocalDate birthday;
    @JsonProperty(PHOTO_PATH_KEY)
    private String photoPath;

    public UserDTO() {
    }

    public static UserDTO encode(JSONObject json) {
        UserDTO result = new UserDTO();
        try {
            //Non nullable.
            result.id = json.getString(USER_ID_KEY);
            result.name = json.getString(NAME_KEY);
            result.gender = UserGender.fromString(json.getString(GENDER_KEY));

            //Nullable.
            result.surname = getStringOrNull(SURNAME_KEY, json);
            result.info = getStringOrNull(INFO_KEY, json);
            result.birthday = getLocalDateOrNull(BIRTHDAY_KEY, json);
            result.photoPath = getStringOrNull(PHOTO_PATH_KEY, json);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to encode UserDTO from jsonObject: ", e);
        }
        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserGender getGender() {
        return gender;
    }

    public String getSurname() {
        return surname;
    }

    public String getInfo() {
        return info;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    private static final String MALE_VALUE = "male";
    private static final String FEMALE_VALUE = "female";

    public enum UserGender {

        @JsonProperty(MALE_VALUE)
        MALE(MALE_VALUE),
        @JsonProperty(FEMALE_VALUE)
        FEMALE(FEMALE_VALUE);

        private final String value;

        UserGender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static UserGender fromString(String text) {
            for (UserGender val : UserGender.values()) {
                if (val.value.equalsIgnoreCase(text)) {
                    return val;
                }
            }
            throw new IllegalArgumentException("No enum constant with string value " + text);
        }
    }
}
