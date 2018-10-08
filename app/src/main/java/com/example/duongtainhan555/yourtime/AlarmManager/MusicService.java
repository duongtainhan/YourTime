package com.example.duongtainhan555.yourtime.AlarmManager;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.duongtainhan555.yourtime.R;


public class MusicService extends Service {
    MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer = MediaPlayer.create(this, R.raw.nhac_chuong);
        mediaPlayer.start();
        return START_NOT_STICKY;
    }
}
