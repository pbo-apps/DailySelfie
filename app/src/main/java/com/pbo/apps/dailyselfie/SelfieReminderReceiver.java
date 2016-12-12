package com.pbo.apps.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class handles doing whatever is needed after receiving a reminder broadcast
 */

public class SelfieReminderReceiver extends BroadcastReceiver {
    private static final int TAKE_SELFIE_NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent restartMainActivityIntent = new Intent(context,
                MainActivity.class);
        restartMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent restartMainActivityPendingIntent = PendingIntent.getActivity(context,
                0, restartMainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(restartMainActivityPendingIntent)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setAutoCancel(true);

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(TAKE_SELFIE_NOTIFICATION_ID, notificationBuilder.build());
    }
}
