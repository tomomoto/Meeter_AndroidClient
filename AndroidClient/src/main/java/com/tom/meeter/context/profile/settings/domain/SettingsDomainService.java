package com.tom.meeter.context.profile.settings.domain;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.tom.meeter.infrastructure.common.Constants.APP_PROPERTIES;
import static com.tom.meeter.infrastructure.common.Constants.MAP_EVENTS_AREA_PROPERTY;
import static com.tom.meeter.infrastructure.common.Constants.MAP_TRACK_USER_PROPERTY;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tom.meeter.R;

import java.io.IOException;
import java.util.Properties;

public class SettingsDomainService {

    private static final String TAG = SettingsDomainService.class.getCanonicalName();

    public static boolean isDefaulted(Context ctx) {
        Properties p = new Properties();
        try {
            p.load(ctx.getAssets().open(APP_PROPERTIES));
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        SharedPreferences prefs = getDefaultSharedPreferences(ctx);
        boolean defTrackUser = Boolean.parseBoolean(p.getProperty(MAP_TRACK_USER_PROPERTY));
        boolean trackUser = prefs.getBoolean(ctx.getString(R.string.prefs_need_track_user), defTrackUser);
        int defSearch = Integer.parseInt(p.getProperty(MAP_EVENTS_AREA_PROPERTY));
        int searchArea = prefs.getInt(ctx.getString(R.string.prefs_search_area), defSearch);
        return (trackUser == defTrackUser) && (defSearch == searchArea);
    }
}
