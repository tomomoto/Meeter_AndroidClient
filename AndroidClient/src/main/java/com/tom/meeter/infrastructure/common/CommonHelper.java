package com.tom.meeter.infrastructure.common;

import android.content.Context;

import com.tom.meeter.R;

public final class CommonHelper {
    private CommonHelper() {
    }

    public static String genderResolver(Context ctx, String gender) {
        return switch (gender.toLowerCase()) {
            case "female" -> ctx.getString(R.string.female_gender);
            case "male" -> ctx.getString(R.string.male_gender);
            default -> throw new IllegalArgumentException("#args " + gender);
        };
    }
}
