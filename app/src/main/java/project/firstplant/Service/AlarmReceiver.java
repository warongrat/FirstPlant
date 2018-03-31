package project.firstplant.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import project.firstplant.Notification.Notification_AFertilization;

/**
 * Created by waron on 10/9/2560.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context,Notification_AFertilization.class);
                    context.startService(serviceIntent);
    }
}

