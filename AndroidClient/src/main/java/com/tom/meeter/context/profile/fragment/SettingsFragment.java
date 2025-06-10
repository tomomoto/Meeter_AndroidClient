package com.tom.meeter.context.profile.fragment;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.tom.meeter.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = SettingsFragment.class.getCanonicalName();

    public SettingsFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        logMethod(TAG, this);
        //setPreferencesFromResource(R.xml.new_preferences, rootKey);
        setPreferencesFromResource(R.xml.my_preferences, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        logMethod(TAG, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        logMethod(TAG, this);
        super.onViewCreated(view, savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        logMethod(TAG, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        logMethod(TAG, this);
        super.onStart();
    }

    @Override
    public void onStop() {
        logMethod(TAG, this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        logMethod(TAG, this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        logMethod(TAG, this);
        super.onResume();
    }

    @Override
    public void onPause() {
        logMethod(TAG, this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        logMethod(TAG, this);
        super.onDestroy();
    }
}