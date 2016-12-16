package com.pbo.apps.dailyselfie;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String PREF_USE_REMINDER = "pref_reminder";
    public static final String PREF_REMINDER_WAKE_DEVICE = "pref_reminder_wake_device";
    public static final String PREF_REMINDER_INTERVAL = "pref_reminder_interval";
    public static final String PREF_REMINDER_INITIAL_TIME = "pref_reminder_initial_time";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).hideCamera();
    }
}