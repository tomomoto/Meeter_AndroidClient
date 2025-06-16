package com.tom.meeter.infrastructure.common;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class DateHelper {
    private static final String TAG = DateHelper.class.getCanonicalName();
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private DateHelper() {
    }

    public static String getAgeFromDate(String date) {
        if (date == null) {
            return "";
        }

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        try {
            dob.setTime(FORMAT.parse(date));
        } catch (ParseException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return String.valueOf(age);
    }
}
