package com.pbo.apps.dailyselfie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
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
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        ((MainActivity) getActivity()).hideCamera();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // Currently we do this for all preferences, because all of them relate to the reminders
        // Cancel the existing reminder because the settings have changed
        ((MainActivity) getActivity()).cancelReminder();

        // Only set the new reminder if the user has enabled it
        Boolean useReminderPref = sharedPreferences.getBoolean(PREF_USE_REMINDER, false);
        if (useReminderPref) {
            ((MainActivity) getActivity()).setReminder();
        }
    }
}