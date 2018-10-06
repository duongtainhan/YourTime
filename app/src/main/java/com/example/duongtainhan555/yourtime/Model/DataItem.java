package com.example.duongtainhan555.yourtime.Model;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataItem implements Comparable<DataItem> {
    private String idUser;
    private String date;
    private ScheduleItem scheduleItem;

    public ScheduleItem getScheduleItem() {
        return scheduleItem;
    }

    public void setScheduleItem(ScheduleItem scheduleItem) {
        this.scheduleItem = scheduleItem;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(@NonNull DataItem o) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        Date date1 = new Date();
        Date date2 = new Date();
        try {
            date1 = formatTime.parse(getScheduleItem().getTimeStart());
            date2 = formatTime.parse(o.getScheduleItem().getTimeStart());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.compareTo(date2);
    }
}
