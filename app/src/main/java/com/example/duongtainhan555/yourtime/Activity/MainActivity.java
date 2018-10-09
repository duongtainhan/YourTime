package com.example.duongtainhan555.yourtime.Activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.duongtainhan555.yourtime.Adapter.PagerAdapter;
import com.example.duongtainhan555.yourtime.AlarmManager.AlarmReceiver;
import com.example.duongtainhan555.yourtime.Interface.SendDataAlarm;
import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.R;
import com.example.duongtainhan555.yourtime.Fragment.ReportFragment;
import com.example.duongtainhan555.yourtime.Fragment.SetTimeFragment;
import com.example.duongtainhan555.yourtime.Fragment.SettingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SendDataAlarm {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    PagerAdapter pagerAdapter;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String idUser;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Intent intent;
    List<DataItem> arrDataAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init
        Init();
        //InitViewPager
        InitViewPager();
        //Init Alarm
        InitAlarm();
    }

    private void Init() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        //Init FireBase
        db = FirebaseFirestore.getInstance();
        GetIdUser();
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

    private void InitAlarm() {

        intent = new Intent(MainActivity.this, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 40);
        calendar.set(Calendar.SECOND, 0);
        if (alarmManager != null) {
            Log.d("ALARM_CA", calendar.getTimeInMillis() + "");
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    public void SendData(List<DataItem> arrData) {
        arrDataAlarm = arrData;
        Log.e("DATA", arrData.size() + "");
    }
}
