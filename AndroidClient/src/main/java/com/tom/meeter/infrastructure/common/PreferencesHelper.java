package com.tom.meeter.infrastructure.common;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.tom.meeter.infrastructure.common.Globals.getDefaultSearchArea;
import static com.tom.meeter.infrastructure.common.Globals.getDefaultTrackUser;

import android.content.Context;

import com.tom.meeter.R;

public class PreferencesHelper {

    public static boolean isDefaulted(Context ctx) {
        return (getNeedTrackUser(ctx) == getDefaultTrackUser(ctx))
              && (getSearchArea(ctx) == getDefaultSearchArea(ctx));
    }

    public static boolean getNeedTrackUser(Context ctx) {
        return getDefaultSharedPreferences(ctx)
              .getBoolean(ctx.getString(R.string.prefs_need_track_user), getDefaultTrackUser(ctx));
    }

    public static int getSearchArea(Context ctx) {
        return getDefaultSharedPreferences(ctx)
              .getInt(ctx.getString(R.string.prefs_search_area), getDefaultSearchArea(ctx));
    }

    private PreferencesHelper() {
        //
    }
}
