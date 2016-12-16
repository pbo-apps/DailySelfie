package com.pbo.apps.dailyselfie;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * This class handles doing whatever is needed after receiving a reminder broadcast
 */

public class SelfieReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {
            case MainActivity.EXTERNAL_ACTION_SET_REMINDER:
                setRepeatingReminder(context);
                break;
            default:
                break;
        }
    }

    private void setRepeatingReminder(Context context) {
        Intent restartMainActivityIntent = new Intent(context,
                MainActivity.class);
        restartMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent restartMainActivityPendingIntent = PendingIntent.getActivity(context,
                0, restartMainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent takeSelfieIntent = new Intent(context, MainActivity.class);
        takeSelfieIntent.setAction(MainActivity.EXTERNAL_ACTION_TAKE_SELFIE);
        PendingIntent takeSelfiePendingIntent = PendingIntent.getActivity(context, 0, takeSelfieIntent, 0);

        String notificationMessage = context.getString(R.string.notification_text);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(notificationMessage)
                        .setContentIntent(restartMainActivityPendingIntent)
                        .setAutoCancel(true)
        /*
         * Sets the big view "big text" style and supplies the
         * text (the user's reminder message) that will be displayed
         * in the detail area of the expanded notification.
         * These calls are ignored by the support library for
         * pre-4.1 devices.
         */
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notificationMessage))
                        .addAction(android.R.drawable.ic_menu_camera,
                                context.getString(R.string.action_title_take_selfie), takeSelfiePendingIntent);

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(SelfieReminder.TAKE_SELFIE_NOTIFICATION_ID, builder.build());
    }
}
