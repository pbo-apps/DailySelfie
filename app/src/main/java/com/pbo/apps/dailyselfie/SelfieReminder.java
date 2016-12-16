package com.pbo.apps.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import java.util.Calendar;

/**
 * This class handles all interaction with the alarm manager to set up reminders
 */

class SelfieReminder {
    private final PendingIntent mAlarmIntent;
    private AlarmManager mAlarmMgr;
    private SharedPreferences mSharedPref;

    SelfieReminder(Context context) {
        context.getSharedPreferences("", Context.MODE_PRIVATE);
        mAlarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SelfieReminderReceiver.class);
        intent.setAction(MainActivity.EXTERNAL_ACTION_SET_REMINDER);
        mAlarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    void start() {
        Boolean useReminder = mSharedPref.getBoolean(SettingsFragment.PREF_USE_REMINDER, false);
        if (useReminder) {
            long alarmIntervalMillis = getAlarmIntervalMillis();
            Calendar initialTime = getAlarmInitialTime();
            if (alarmIntervalMillis > 0) {
                mAlarmMgr.setInexactRepeating(getAlarmType(),
                        initialTime.getTimeInMillis(),
                        alarmIntervalMillis,
                        mAlarmIntent);
            } else {
                mAlarmMgr.set(getAlarmType(),
                        initialTime.getTimeInMillis(),
                        mAlarmIntent);
            }
        }
    }

    private long getAlarmIntervalMillis() {
        String reminderInterval = mSharedPref.getString(SettingsFragment.PREF_REMINDER_INTERVAL, "0");
        return Long.parseLong(reminderInterval) * 60 * 1000;
    }

    private Calendar getAlarmInitialTime() {
        String reminderInitialTime = mSharedPref.getString(SettingsFragment.PREF_REMINDER_INITIAL_TIME, "12");
        int hourOfDay = Integer.parseInt(reminderInitialTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay <= currentHourOfDay) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        return calendar;
    }

    private int getAlarmType() {
        Boolean reminderWakeUp = mSharedPref.getBoolean(SettingsFragment.PREF_REMINDER_WAKE_DEVICE, false);
        return reminderWakeUp ?
                AlarmManager.ELAPSED_REALTIME_WAKEUP : AlarmManager.ELAPSED_REALTIME;
    }

    void cancel() {
        mAlarmMgr.cancel(mAlarmIntent);
    }
}
