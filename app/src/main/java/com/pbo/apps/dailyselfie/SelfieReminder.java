package com.pbo.apps.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * This class handles all interaction with the alarm manager to set up reminders
 */

class SelfieReminder {
    private final PendingIntent mAlarmIntent;

    private long mAlarmInterval = 15 * 1000;
    private AlarmManager mAlarmMgr;

    SelfieReminder(Context context) {
        mAlarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SelfieReminderReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mAlarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    void start() {
        mAlarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + mAlarmInterval,
        mAlarmInterval,
        mAlarmIntent);
    }

    void cancel() {
        mAlarmMgr.cancel(mAlarmIntent);
    }
}
