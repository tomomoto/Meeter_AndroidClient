package com.tom.meeter.infrastructure.common;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.invalidateToken;
import static com.tom.meeter.context.network.dto.EventDTO.EventStatus.transformToStrings;
import static com.tom.meeter.infrastructure.common.Globals.getDefaultSearchArea;
import static com.tom.meeter.infrastructure.common.Globals.getDefaultTrackUser;
import static com.tom.meeter.infrastructure.common.Globals.getDefaultVisibleEventsStatuses;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.launcher.Launcher;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.message.SettingsCreateOrUpdate;
import com.tom.meeter.context.profile.message.SettingsResponse;
import com.tom.meeter.context.profile.service.SettingsService;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class PreferencesHelper {

    private static final String TAG = PreferencesHelper.class.getCanonicalName();

    private static Set<String> defaultStringEventStatuses;

    public static void cleanLocalPrefs(Context ctx) {
        getDefaultSharedPreferences(ctx).edit().clear().apply();
    }

    public static void updateLocalPrefs(Context ctx, SettingsResponse resp) {
        SharedPreferences.Editor edit = getDefaultSharedPreferences(ctx).edit();

        String searchAreaPref = ctx.getString(R.string.prefs_search_area);
        Integer searchArea = resp.getSearchArea();
        if (searchArea != null) {
            edit.putInt(searchAreaPref, searchArea);
        } else {
            edit.remove(searchAreaPref);
        }

        String needTrackUserPref = ctx.getString(R.string.prefs_need_track_user);
        Boolean needTrackUser = resp.getNeedTrackUser();
        if (needTrackUser != null) {
            edit.putBoolean(needTrackUserPref, needTrackUser);
        } else {
            edit.remove(needTrackUserPref);
        }

        String visibleStatusesPref = ctx.getString(R.string.prefs_visible_event_statuses);
        Set<EventDTO.EventStatus> statuses = resp.getVisibleEventStatuses();
        if (statuses != null) {
            edit.putStringSet(visibleStatusesPref, transformToStrings(statuses));
        } else {
            edit.remove(visibleStatusesPref);
        }

        edit.apply();
    }

    public static void savePrefsToServer(
          Activity activity, SettingsService service,
          SettingsCreateOrUpdate req, Runnable afterSave) {
        AccountManager am = AccountManager.get(activity);
        service.createOrUpdateSettings(req, AuthHelper.getAuthHeader(am))
              .enqueue(new HttpErrorLogger<>(activity) {
                  @Override
                  public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
                          invalidateToken(
                                am, activity,
                                (freshToken) -> sendSavePrefsRetry(
                                      ctx, service, freshToken, req, afterSave),
                                () -> {
                                    Log.d(TAG, "Canceled auth.");
                                    ctx.startActivity(new Intent(ctx, Launcher.class));
                                });
                          return;
                      }
                      if (resp.code() == HttpCodes.OK || resp.code() == HttpCodes.CREATED) {
                          Log.d(TAG, "created/updated server settings.");
                          updateLocalPrefs(activity, resp.body());
                          afterSave.run();
                          return;
                      }
                  }
              });
    }

    public static void savePrefsToServer(
          Activity activity, SettingsService service,
          SettingsCreateOrUpdate req) {
        savePrefsToServer(activity, service, req, () -> {
        });
    }

    private static void sendSavePrefsRetry(
          Context ctx, SettingsService service,
          String token, SettingsCreateOrUpdate req, Runnable afterSave) {
        service.createOrUpdateSettings(req, Globals.getAuthHeader(token))
              .enqueue(new HttpErrorLogger<>(ctx) {
                  @Override
                  public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK || resp.code() == HttpCodes.CREATED) {
                          Log.d(TAG, "Created/updated server settings on retry.");
                          updateLocalPrefs(ctx, resp.body());
                          afterSave.run();
                          return;
                      }
                  }
              });
    }

    public static boolean getNeedTrackUser(Context ctx) {
        return getDefaultSharedPreferences(ctx)
              .getBoolean(ctx.getString(R.string.prefs_need_track_user), getDefaultTrackUser(ctx));
    }

    public static int getSearchArea(Context ctx) {
        return getDefaultSharedPreferences(ctx)
              .getInt(ctx.getString(R.string.prefs_search_area), getDefaultSearchArea(ctx));
    }

    public static Set<EventDTO.EventStatus> getVisibleEventsStatuses(
          Context ctx) {
        return EventDTO.EventStatus.transformToEnums(
              getDefaultSharedPreferences(ctx)
                    .getStringSet(ctx.getString(R.string.prefs_visible_event_statuses),
                          getDefaultStringEventStatuses(ctx)));
    }

    private static Set<String> getDefaultStringEventStatuses(
          Context ctx) {
        if (defaultStringEventStatuses != null) {
            return defaultStringEventStatuses;
        }
        defaultStringEventStatuses = EventDTO.EventStatus.transformToStrings(
              getDefaultVisibleEventsStatuses(ctx));
        return defaultStringEventStatuses;
    }

    private PreferencesHelper() {
        //
    }
}
