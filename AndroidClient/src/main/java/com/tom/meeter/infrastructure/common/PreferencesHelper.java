package com.tom.meeter.infrastructure.common;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.tom.meeter.infrastructure.common.Globals.APP_PROPERTIES;
import static com.tom.meeter.infrastructure.common.Globals.MAP_EVENTS_AREA_PROPERTY;
import static com.tom.meeter.infrastructure.common.Globals.MAP_TRACK_USER_PROPERTY;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tom.meeter.R;

import java.io.IOException;
import java.util.Properties;

public class PreferencesHelper {

    private static final String TAG = PreferencesHelper.class.getCanonicalName();

    private static Boolean needTrackUserDefault;

    private static Integer searchAreaDefault;
    public static boolean isDefaulted(Context ctx) {
        return (getNeedTrackUser(ctx) == getDefaultTrackUser(ctx))
              && (getSearchArea(ctx) == getDefaultSearchArea(ctx));
    }

    public static boolean getNeedTrackUser(Context ctx) {
        return getDefaultSharedPreferences(ctx).getBoolean(
              ctx.getString(R.string.prefs_need_track_user), getDefaultTrackUser(ctx));
    }

    public static int getSearchArea(Context ctx) {
        SharedPreferences prefs = getDefaultSharedPreferences(ctx);
        return prefs.getInt(
              ctx.getString(R.string.prefs_search_area), getDefaultSearchArea(ctx));
    }

    private static boolean getDefaultTrackUser(Context ctx) {
        if (needTrackUserDefault != null) {
            return needTrackUserDefault;
        }
        needTrackUserDefault = Boolean.parseBoolean(
              tryGetProps(ctx).getProperty(MAP_TRACK_USER_PROPERTY));
        return needTrackUserDefault;
    }

    private static int getDefaultSearchArea(Context ctx) {
        if (searchAreaDefault != null) {
            return searchAreaDefault;
        }
        searchAreaDefault = Integer.parseInt(
              tryGetProps(ctx).getProperty(MAP_EVENTS_AREA_PROPERTY));
        return searchAreaDefault;
    }

    private static Properties tryGetProps(Context ctx) {
        Properties p = new Properties();
        try {
            p.load(ctx.getAssets().open(APP_PROPERTIES));
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        return p;
    }

    private PreferencesHelper() {
        //
    }
}
