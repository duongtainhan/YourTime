package com.example.duongtainhan555.yourtime.AlarmNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.duongtainhan555.yourtime.Activity.AlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int requestID = Integer.valueOf(intent.getStringExtra("requestID"));
        String title = intent.getStringExtra("title");
        String note = intent.getStringExtra("note");
        Log.d("ALARM", "onReceive: ");
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                NotificationSchedule.CancelAlarm(context,AlarmReceiver.class,requestID);
                return;
            }
        }
        NotificationSchedule.ShowNotification(context, AlarmActivity.class, requestID, title, note);
    }
}
