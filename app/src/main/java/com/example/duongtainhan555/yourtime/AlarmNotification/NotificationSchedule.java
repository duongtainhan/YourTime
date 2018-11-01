package com.example.duongtainhan555.yourtime.AlarmNotification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.duongtainhan555.yourtime.Constant;
import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;

public class NotificationSchedule {
    public static void SetAlarm(Context context, Class<?> cls, DataItem dataItem, int position, String idUser) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        Date date;
        Date time;
        try {
            // cancel already scheduled reminders
            CancelAlarm(context,cls,Integer.valueOf(dataItem.getScheduleItems().get(position).getRequestID()));
            // Enable a receiver
            ComponentName receiver = new ComponentName(context, cls);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //Set time
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
            int monthPlus=month+1;
            intent.putExtra("title",day+"/"+monthPlus+"/"+year+"   "+hour+":"+min);
            intent.putExtra("idUser",idUser);
            intent.putExtra("date",dataItem.getDate());
            intent.putExtra("time",dataItem.getScheduleItems().get(position).getTimeStart());
            intent.putExtra("alarm",dataItem.getScheduleItems().get(position).getAlarm());
            intent.putExtra("status",dataItem.getScheduleItems().get(position).getStatus());
            intent.putExtra("note",dataItem.getScheduleItems().get(position).getNote());
            intent.putExtra("requestID",dataItem.getScheduleItems().get(position).getRequestID());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    Integer.valueOf(dataItem.getScheduleItems().get(position).getRequestID()),
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("ALARM","SET_ALARM");
            Log.d("ALARM","DATE: "+day+"/"+monthPlus+"/"+year+"  "+hour+":"+min);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static void CancelAlarm(Context context,Class<?> cls, int requestID)
    {
        Log.d("ALARM","CANCEL_ALARM");
        //// Disable a receiver
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
    public static void UpdateAlarm(String idUser, String date, String time,
                                   String note, String requestID)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Map<String, Object> docData = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put(Constant.note, note);
        nestedData.put(Constant.status, Constant.missedStatus);
        nestedData.put(Constant.alarm, Constant.offAlarm);
        nestedData.put(Constant.requestID,requestID);

        docData.put(time, nestedData);
        db.collection(idUser).document(date)
                .update(docData);
    }
    public static void ShowNotification(Context context, Class<?> cls,
                                        String idUser, String date, String time, String alarm,
                                        String status, String requestCode, String title, String note)
    {
        int requestID = Integer.valueOf(requestCode);
        //
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent intent = new Intent(context, cls);
        intent.putExtra("idUser",idUser);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        intent.putExtra("alarm",alarm);
        intent.putExtra("status",status);
        intent.putExtra("requestID",requestCode);
        intent.putExtra("note",note);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);
        //
        String id = "id_chanel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if (notificationManager == null) {
            notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("ALARM","1");
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(id);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(id, title, importance);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(notificationChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            builder.setContentTitle(title)
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)
                    .setContentText(note)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setContentIntent(pendingIntent)
                    .setTicker(title)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            Log.d("ALARM","2");
            builder = new NotificationCompat.Builder(context, id);
            builder.setContentTitle(title)
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)
                    .setContentText(note)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(title)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_INSISTENT|Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(requestID, notification);
    }
}
