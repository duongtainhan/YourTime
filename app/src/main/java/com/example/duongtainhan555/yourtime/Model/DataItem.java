package com.example.duongtainhan555.yourtime.Model;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataItem implements Comparable<DataItem> {
    private String date;
    private List<ScheduleItem> scheduleItems;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ScheduleItem> getScheduleItems() {
        return scheduleItems;
    }

    public void setScheduleItems(List<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    @Override
    public int compareTo(@NonNull DataItem o) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("dd-MM-yyyy");
        Date date1 = new Date();
        Date date2 = new Date();
        try {
            date1 = formatTime.parse(getDate());
            date2 = formatTime.parse(o.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.compareTo(date2);
    }
}
