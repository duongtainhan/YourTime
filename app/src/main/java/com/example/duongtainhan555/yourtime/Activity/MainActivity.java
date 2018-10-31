package com.example.duongtainhan555.yourtime.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.duongtainhan555.yourtime.Adapter.PagerAdapter;
import com.example.duongtainhan555.yourtime.R;
import com.example.duongtainhan555.yourtime.Fragment.ReportFragment;
import com.example.duongtainhan555.yourtime.Fragment.SetTimeFragment;
import com.example.duongtainhan555.yourtime.Fragment.SettingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    PagerAdapter pagerAdapter;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init
        Init();
        //InitViewPager
        InitViewPager();
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
                    //Do anything when tab 2 selected.
                } else if (tab.getPosition() == 2) {
                    //Do anything when tab 3 selected.
                } else {
                    //Do anything when tab 1 selected.
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
}
