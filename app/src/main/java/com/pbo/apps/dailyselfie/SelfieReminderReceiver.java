package com.pbo.apps.dailyselfie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * This class handles doing whatever is needed after receiving a reminder broadcast
 */

public class SelfieReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "I guess this should really be a notification...", Toast.LENGTH_LONG).show();
    }
}
