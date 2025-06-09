package com.tom.meeter.context.profile.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.tom.meeter.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //setPreferencesFromResource(R.xml.new_preferences, rootKey);
        setPreferencesFromResource(R.xml.my_preferences, rootKey);
    }
}