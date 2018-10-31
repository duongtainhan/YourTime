package com.example.duongtainhan555.yourtime.AlarmNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.duongtainhan555.yourtime.Activity.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("ALARM", "onReceive: ");

        int requestID = Integer.valueOf(intent.getStringExtra("requestID"));
        String title = intent.getStringExtra("title");
        String note = intent.getStringExtra("note");
        NotificationSchedule.ShowNotification(context, MainActivity.class, requestID,
                title, note);
    }
}
