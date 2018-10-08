package com.example.duongtainhan555.yourtime.Activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.duongtainhan555.yourtime.Adapter.PagerAdapter;
import com.example.duongtainhan555.yourtime.AlarmManager.AlarmReceiver;
import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.Model.ScheduleItem;
import com.example.duongtainhan555.yourtime.R;
import com.example.duongtainhan555.yourtime.Fragment.ReportFragment;
import com.example.duongtainhan555.yourtime.Fragment.SetTimeFragment;
import com.example.duongtainhan555.yourtime.Fragment.SettingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String idUser;

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

    private void SetAlarm() {
        final Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        final String date = formatDate.format(calendar.getTime());
        final List<DataItem> arrData = new ArrayList<>();
        final DocumentReference docRef = db.collection(idUser).document(date);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    arrData.clear();
                    for (Map.Entry<String, Object> entry : Objects.requireNonNull(snapshot.getData()).entrySet()) {
                        DataItem dataItem = new DataItem();
                        ScheduleItem scheduleItem = new ScheduleItem();
                        scheduleItem.setTimeStart(entry.getKey());
                        Map<String, String> nestedData = (Map<String, String>) entry.getValue();
                        scheduleItem.setNote(nestedData.get("Note"));
                        scheduleItem.setStatus(nestedData.get("Status"));
                        dataItem.setScheduleItem(scheduleItem);
                        dataItem.setIdUser(idUser);
                        dataItem.setDate(date);
                        if (Objects.requireNonNull(nestedData.get("Status")).equals("Not Ready")) {
                            arrData.add(dataItem);
                        }
                    }
                    if (!arrData.isEmpty()) {
                        Collections.sort(arrData);
                        //InitAlarm(arrData);
                    }

                } else {
                    //Faild
                }

            }
        });
    }
    private void InitAlarm(){
        final Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,2);
        calendar.set(Calendar.MINUTE,12);
        Log.d("ALARM_CA",calendar.getTimeInMillis()+"");
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

}
