package com.example.duongtainhan555.yourtime.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.duongtainhan555.yourtime.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity {

    private TextView txtDate, txtTime, txtNote, txtCountTime;
    private String idUser, date, time, alarm, status, note, requestID;
    private RelativeLayout relativeGoMenu;
    private ImageView imgLog, imgStartStop;

    //Set Count Time
    long startTime = 0;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            startTime++;
            long hours = startTime/3600;
            long minutes = startTime%3600/60;
            long seconds = startTime%3600%60;

            txtCountTime.setText(String.format("%02d:%02d:%02d",hours, minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        //GetIntent
        GetIntent();
        //InitView
        InitView();
        //Event
        EventObject();
    }

    private void GetIntent() {
        idUser = getIntent().getStringExtra("idUser");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        alarm = getIntent().getStringExtra("alarm");
        status = getIntent().getStringExtra("status");
        note = getIntent().getStringExtra("note");
        requestID = getIntent().getStringExtra("requestID");
        Log.d("ALARM_PASS_DATE", date);
    }

    private void InitView() {
        txtCountTime = findViewById(R.id.txtCountTime);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtNote = findViewById(R.id.txtNote);
        relativeGoMenu = findViewById(R.id.relativeGoMenu);
        imgLog = findViewById(R.id.imgLog);
        imgStartStop = findViewById(R.id.imgStartStop);
        //
        txtTime.setText(time);
        txtNote.setText(note);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDateCalendar = new SimpleDateFormat("d MMMM yyyy");
        try {
            Date calendarFormatDate = formatDate.parse(date);
            String dateCalendar = formatDateCalendar.format(calendarFormatDate);
            txtDate.setText(dateCalendar);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void EventObject() {
        EventClickGoMenu();
        EventClickLogOut();
        EventClickStartStop();
    }

    private void EventClickGoMenu() {
        relativeGoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGoMenu = new Intent(AlarmActivity.this, MainActivity.class);
                startActivity(intentGoMenu);
            }
        });
    }

    private void EventClickLogOut() {
        imgLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finishAndRemoveTask();
            }
        });
    }

    private void EventClickStartStop() {
        imgStartStop.setOnClickListener(new View.OnClickListener() {
            boolean start = false;
            @Override
            public void onClick(View v) {
                txtCountTime.setVisibility(View.VISIBLE);
                if (!start) {
                    //Start: On
                    timerHandler.postDelayed(timerRunnable, 0);
                    imgStartStop.setImageResource(R.drawable.stop);
                    start = true;
                } else {
                    //Start: Off - OnStop
                    timerHandler.removeCallbacks(timerRunnable);
                    imgStartStop.setImageResource(R.drawable.ic_start);
                    start = false;
                }
            }
        });
    }
    @Override
    protected void onPause() {
        Log.d("ALARM","onPause");
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        imgStartStop.setImageResource(R.drawable.ic_start);
    }

    @Override
    protected void onStop() {
        Log.d("ALARM","onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("ALARM","onDestroy");
        super.onDestroy();
    }
}
