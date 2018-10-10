package com.example.duongtainhan555.yourtime.Activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.duongtainhan555.yourtime.Adapter.PagerAdapter;
import com.example.duongtainhan555.yourtime.Constant;
import com.example.duongtainhan555.yourtime.Interface.SendDataAlarm;
import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.R;
import com.example.duongtainhan555.yourtime.Fragment.ReportFragment;
import com.example.duongtainhan555.yourtime.Fragment.SetTimeFragment;
import com.example.duongtainhan555.yourtime.Fragment.SettingFragment;
import com.example.duongtainhan555.yourtime.Service.SchedulingService;
import com.example.duongtainhan555.yourtime.Utils.AlarmReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SendDataAlarm {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    PagerAdapter pagerAdapter;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String idUser;
    List<DataItem> arrDataAlarm;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init
        Init();
        //InitViewPager
        InitViewPager();
        //SetAlarm
        //SetAlarm();
    }

    private void Init() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        //Init FireBase
        db = FirebaseFirestore.getInstance();
        GetIdUser();
        //
        intent = new Intent(this,AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    private void GetIdUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
            idUser = firebaseUser.getUid();
    }

    private void InitViewPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        pagerAdapter.InitFragment(new ReportFragment(), new SetTimeFragment(), new SettingFragment());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1) {

                } else if (tab.getPosition() == 2) {


                } else {

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }


    @Override
    public void SendData(List<DataItem> arrData) {
        arrDataAlarm = arrData;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        for(int i=0;i<arrData.size();i++)
        {
            Log.d("ALARM","da khoi tao");
            Date getDate = new Date();
            Date getTime = new Date();
            try {
                getDate = formatDate.parse(arrDataAlarm.get(i).getDate());
                getTime = formatTime.parse(arrDataAlarm.get(i).getScheduleItem().getTimeStart());
                SetAlarm(getTime.getHours(),getTime.getMinutes());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressLint("ObsoleteSdkInt")
    private void SetAlarm(int hour, int min)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);
        calendar.set(Calendar.SECOND,0);
        pendingIntent = PendingIntent.getService(
                MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager
                    .setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager
                    .set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
