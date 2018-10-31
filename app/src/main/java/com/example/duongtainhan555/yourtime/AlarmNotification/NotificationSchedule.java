package com.example.duongtainhan555.yourtime.AlarmNotification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class NotificationSchedule {
    public static void SetAlarm(Context context, Class<?> cls, DataItem dataItem, int position, int requestID) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        Date date;
        Date time;
        //Log.d("ALARM", "DATA: " + userItem.getDataItems().get(0).getDate() + " " + userItem.getDataItems().get(0).getScheduleItems().get(0).getTimeStart());
        try {
            String getTime = dataItem.getScheduleItems().get(position).getTimeStart();
            String getDate = dataItem.getDate();
            date = formatDate.parse(getDate);
            time = formatTime.parse(getTime);
            int hour = time.getHours();
            int min = time.getMinutes();
            int day = date.getDate();
            @SuppressLint("SimpleDateFormat") String formatYear = new SimpleDateFormat("yyyy").format(date);
            int month = date.getMonth();
            int year = Integer.parseInt(formatYear);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            Intent intent = new Intent(context, cls);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static void CancelAlarm(Context context,Class<?> cls, int requestID)
    {
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
    public static void ShowNotification(Context context,Class<?> cls,int requestID,String title,String note)
    {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle(title)
                .setContentText(note)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();
        notification.flags = Notification.FLAG_INSISTENT;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestID, notification);
    }
}
