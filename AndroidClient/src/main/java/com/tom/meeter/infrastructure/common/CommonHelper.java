package com.tom.meeter.infrastructure.common;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tom.meeter.R;
import com.tom.meeter.context.network.dto.EventDTO;
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

    public static String eventStatusResolver(Context ctx, EventDTO.EventStatus status) {
        return switch (status) {
            case CREATED -> ctx.getString(R.string.created_status);
            case PUBLISHED -> ctx.getString(R.string.published_status);
            case UNPUBLISHED -> ctx.getString(R.string.unpublished_status);
            case SCHEDULED -> ctx.getString(R.string.scheduled_status);
            case STARTED -> ctx.getString(R.string.started_status);
            case PAUSED -> ctx.getString(R.string.paused_status);
            case RESUMED -> ctx.getString(R.string.resumed_status);
            case FINISHED -> ctx.getString(R.string.finished_status);
            case CANCELLED -> ctx.getString(R.string.cancelled_status);
            case ARCHIVED -> ctx.getString(R.string.archived_status);
            default -> throw new IllegalArgumentException("#args " + status);
        };
    }

    public static void setStatusColor(TextView statusView, int colorRes) {
        statusView.setBackgroundResource(colorRes);
    }

    public static int getStatusColor(Context ctx, EventDTO.EventStatus status) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            throw new IllegalArgumentException("#Unable to get status color " + status);
        }
        return switch (status) {
            case CREATED -> ctx.getColor(R.color.created_status);
            case PUBLISHED -> ctx.getColor(R.color.published_status);
            case UNPUBLISHED -> ctx.getColor(R.color.unpublished_status);
            case SCHEDULED -> ctx.getColor(R.color.scheduled_status);
            case STARTED -> ctx.getColor(R.color.started_status);
            case PAUSED -> ctx.getColor(R.color.paused_status);
            case RESUMED -> ctx.getColor(R.color.resumed_status);
            case FINISHED -> ctx.getColor(R.color.finished_status);
            case CANCELLED -> ctx.getColor(R.color.cancelled_status);
            case ARCHIVED -> ctx.getColor(R.color.archived_status);
            default -> throw new IllegalArgumentException("#args " + status);
        };
    }

    public static void handleEventStatus(
          Context ctx, TextView view, EventDTO.EventStatus status) {
        view.setText(eventStatusResolver(ctx, status));
        setRoundedBackground(view, getStatusColor(ctx, status), 6f);
    }

    public static void setRoundedBackground(
          TextView view, int backgroundColor, float cornerRadiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(backgroundColor);
        float radiusPx = TypedValue.applyDimension(
              TypedValue.COMPLEX_UNIT_DIP,
              cornerRadiusDp,
              view.getResources().getDisplayMetrics()
        );
        drawable.setCornerRadius(radiusPx);
        view.setBackground(drawable);
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

    public static int getAppLogo() {
        return R.drawable.meeter_new_logo_512x512;
    }

    public static int getSmallAppLogo() {
        return R.drawable.meeter_new_logo_64x64;
    }
}
