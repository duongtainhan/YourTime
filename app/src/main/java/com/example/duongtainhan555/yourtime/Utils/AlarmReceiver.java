package com.example.duongtainhan555.yourtime.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.duongtainhan555.yourtime.Activity.AlarmActivity;
import com.example.duongtainhan555.yourtime.Service.RingtoneService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ALARM","I'm Alarm");

        String state = intent.getExtras().getString("extra");
        Intent intentService = new Intent(context,RingtoneService.class);
        intentService.putExtra("extra", state);
        context.startService(intentService);
        //
    }
}
