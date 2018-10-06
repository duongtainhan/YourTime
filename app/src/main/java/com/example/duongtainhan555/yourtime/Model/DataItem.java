package com.example.duongtainhan555.yourtime.Model;

public class DataItem {
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
}
