package com.tom.meeter.context.profile.component.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

import com.tom.meeter.R;

public class SettingsFragmentOld extends PreferenceFragment {

    private static final String TAG = SettingsFragmentOld.class.getCanonicalName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // below line is used to add preference
        // fragment from our xml folder.
        addPreferencesFromResource(R.xml.preferences);
    }

}
