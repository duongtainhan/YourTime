package com.example.duongtainhan555.yourtime.AlarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context,RingtoneService.class);
        //ContextCompat.startForegroundService(context, intentService );
        Log.d("ALARM","OK ALARM");
    }
}
