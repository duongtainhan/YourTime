package com.example.duongtainhan555.yourtime.AlarmNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CountTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("ALARM","onCountTimeReceiver");
        long countTime = Long.valueOf(intent.getStringExtra("countTime"));
        Log.d("ALARM","countTime: "+countTime);

    }
}
