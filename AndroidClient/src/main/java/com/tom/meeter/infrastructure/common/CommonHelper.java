package com.tom.meeter.infrastructure.common;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tom.meeter.R;
import com.tom.meeter.context.network.dto.UserDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class CommonHelper {

    private CommonHelper() {
    }

    public static final DateTimeFormatter UI_DATE_TIME_FORMAT =
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final DateTimeFormatter UI_DATE_FORMAT =
          DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter UI_TIME_FORMAT =
          DateTimeFormatter.ofPattern("HH:mm");

    public static final String EMPTY_STR = "";

    public static String genderResolver(Context ctx, UserDTO.UserGender gender) {
        return switch (gender) {
            case FEMALE -> ctx.getString(R.string.female_gender);
            case MALE -> ctx.getString(R.string.male_gender);
            default -> throw new IllegalArgumentException("#args " + gender);
        };
    }

    @Nullable
    public static CharSequence dateOrNull(OffsetDateTime date) {
        return date == null ? null : UI_DATE_TIME_FORMAT.format(date);
    }

    @Nullable
    public static CharSequence textOrNull(Double val) {
        return val == null ? null : val.toString();
    }

    @Nullable
    public static CharSequence textOrNull(Float val) {
        return val == null ? null : val.toString();
    }

    public static String getStringOrNull(CharSequence input) {
        if (input == null || EMPTY_STR.contentEquals(input)) {
            return null;
        }
        return input.toString();
    }

    public static boolean isEmpty(CharSequence input) {
        return input == null || EMPTY_STR.contentEquals(input);
    }

    public static Float getFloatOrNull(CharSequence input) {
        if (input == null || EMPTY_STR.contentEquals(input)) {
            return null;
        }
        return Float.valueOf(input.toString());
    }

    public static Double getDoubleOrNull(CharSequence input) {
        if (input == null || EMPTY_STR.contentEquals(input)) {
            return null;
        }
        return Double.valueOf(input.toString());
    }

    public static OffsetDateTime getOffsetDateTime(CharSequence input) {
        if (input == null || EMPTY_STR.contentEquals(input)) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(input, UI_DATE_TIME_FORMAT);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.toOffsetDateTime();
    }

    public static LocalDate getLocalDateOrNull(CharSequence input) {
        if (input == null || EMPTY_STR.contentEquals(input)) {
            return null;
        }
        try {
            return LocalDate.parse(input, UI_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            Log.e("DateParser", "Ошибка парсинга даты: " + input, e);
            return null;
        }
    }

    @Nullable
    public static OffsetDateTime getOffsetDateTimeOrNull(
          CharSequence date, CharSequence time) {
        if (isEmpty(date) || isEmpty(time)) {
            return null;
        }
        return OffsetDateTime.of(
              LocalDate.parse(date),
              LocalTime.parse(time),
              OffsetDateTime.now().getOffset());
    }

}
