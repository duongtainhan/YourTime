package com.example.duongtainhan555.yourtime.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    private int numOfTabs;
    private Fragment fragmentReport;
    private Fragment fragmentSetTime;
    private Fragment fragmentSetting;


    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return fragmentReport;
            case 1:
                return fragmentSetTime;
            case 2:
                return fragmentSetting;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    public void InitFragment(Fragment fragmentReport, Fragment fragmentSetTime, Fragment fragmentSetting) {
        this.fragmentReport = fragmentReport;
        this.fragmentSetTime = fragmentSetTime;
        this.fragmentSetting = fragmentSetting;
    }

}
