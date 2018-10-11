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
import com.example.duongtainhan555.yourtime.Interface.SendDataAlarm;
import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.Model.UserItem;
import com.example.duongtainhan555.yourtime.R;
import com.example.duongtainhan555.yourtime.Fragment.ReportFragment;
import com.example.duongtainhan555.yourtime.Fragment.SetTimeFragment;
import com.example.duongtainhan555.yourtime.Fragment.SettingFragment;
import com.example.duongtainhan555.yourtime.Utils.AlarmReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SendDataAlarm {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    PagerAdapter pagerAdapter;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String idUser;
    List<DataItem> arrDataAlarm;
    AlarmManager alarmManager;
    List<Integer> listMomery;
    PendingIntent pendingIntent;
    boolean status = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init
        Init();
        //InitViewPager
        InitViewPager();
        //SetAlarm
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    //
    private void Init() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        //Init FireBase
        db = FirebaseFirestore.getInstance();
        GetIdUser();
        //
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



    private void SetAlarm(UserItem userItem) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        Date date;
        Date time;
        Log.d("ALARM","DATA: "+userItem.getDataItems().get(0).getDate()+" "+userItem.getDataItems().get(0).getScheduleItems().get(0).getTimeStart());
        try {
            date = formatDate.parse(userItem.getDataItems().get(0).getDate());
            time = formatTime.parse(userItem.getDataItems().get(0).getScheduleItems().get(0).getTimeStart());
            int hour = time.getHours();
            int min = time.getMinutes();
            int day = date.getDate();
            @SuppressLint("SimpleDateFormat") String formatYear = new SimpleDateFormat("yyyy").format(date);
            int month = date.getMonth();
            int year = Integer.parseInt(formatYear);
            //Log.d("DAYYYY",hour+":"+min+"  "+day+"/"+month+"/"+year);
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            pendingIntent = PendingIntent.getBroadcast(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void SendData(UserItem userItem) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(status)
        {
            SetAlarm(userItem);
            status = false;
        }
        else
        {
            Objects.requireNonNull(alarmManager).cancel(pendingIntent);
            SetAlarm(userItem);
        }
    }
}
