package com.example.duongtainhan555.yourtime.AlarmNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.duongtainhan555.yourtime.Activity.AlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ALARM", "onReceive: ");

        //getIntent
        String idUser = intent.getStringExtra("idUser");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String note = intent.getStringExtra("note");
        String requestCode = intent.getStringExtra("requestID");
        int requestID = Integer.valueOf(requestCode);
        String title = intent.getStringExtra("title");

        /*
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                NotificationSchedule.CancelAlarm(context,AlarmReceiver.class,requestID);
                return;
            }
        }
        */
        NotificationSchedule.UpdateAlarm(idUser,date,time,note,requestCode);
        NotificationSchedule.CancelAlarm(context,AlarmReceiver.class,requestID);
        NotificationSchedule.ShowNotification(context, AlarmActivity.class, requestID, title, note);
    }
}
