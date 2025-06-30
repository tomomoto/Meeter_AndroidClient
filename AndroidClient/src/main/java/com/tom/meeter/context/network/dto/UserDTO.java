package com.tom.meeter.context.network.dto;

import static com.tom.meeter.infrastructure.common.JsonHelper.getLocalDateOrNull;
import static com.tom.meeter.infrastructure.common.JsonHelper.getStringOrNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO extends ServerEntityBase {

    private static final String GENDER_KEY = "gender";
    private static final String SURNAME_KEY = "surname";
    private static final String INFO_KEY = "info";
    private static final String BIRTHDAY_KEY = "birthday";

    private UserGender gender;
    private String surname;
    private String info;
    private LocalDate birthday;

    public UserDTO() {
        //retrofit...
    }

    public UserDTO(JSONObject json) {
        super(json);
        try {
            //Non nullable.
            gender = UserGender.fromString(json.getString(GENDER_KEY));

            //Nullable.
            surname = getStringOrNull(SURNAME_KEY, json);
            info = getStringOrNull(INFO_KEY, json);
            birthday = getLocalDateOrNull(BIRTHDAY_KEY, json);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to encode UserDTO from jsonObject: ", e);
        }
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserDTO userDTO = (UserDTO) o;
        return gender == userDTO.gender
              && Objects.equals(surname, userDTO.surname)
              && Objects.equals(info, userDTO.info)
              && Objects.equals(birthday, userDTO.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
              super.hashCode(), gender, surname, info, birthday);
    }
}
