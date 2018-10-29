package com.example.duongtainhan555.yourtime.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AlarmActivity extends AppCompatActivity{

    private MediaPlayer mMediaPlayer;
    ImageView imgLog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private String idUser, getDate, getTime, getNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alarm);

        GetIdUser();
        GetInfoAlarm();
        //
        imgLog = findViewById(R.id.imgLogAlarm);
        imgLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffAlarm(getDate,getTime,getNote);
                mMediaPlayer.stop();
                finish();
                System.exit(0);
            }
        });
        playSound(this, getAlarmUri());
    }
    private void GetIdUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
            idUser = firebaseUser.getUid();
    }
    private void GetInfoAlarm()
    {
        getDate = getIntent().getStringExtra("date");
        getTime = getIntent().getStringExtra("time");
        getNote = getIntent().getStringExtra("note");
    }
    private void OffAlarm(String date, String time, String note)
    {
        final Map<String, Object> docData = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put("Note", note);
        nestedData.put("Status", "off");
        nestedData.put("Alarm", "off");

        docData.put(time, nestedData);

        db.collection(idUser).document(date)
                .update(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            Log.d("ALARM","alarm - error");
        }
    }

    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

}