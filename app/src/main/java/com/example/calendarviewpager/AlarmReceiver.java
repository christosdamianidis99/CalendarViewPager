package com.example.calendarviewpager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mycalendar.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static Ringtone r;

    public static int NOTIFICATION_ID;

    @Override
    public void onReceive(Context context, Intent intent) {


        int eventId = intent.getIntExtra("eventId",0);
        String comments = intent.getStringExtra("comments");
        String alarm_title = context.getString(R.string.app_name);


        NOTIFICATION_ID = eventId;


        // Acquire a wake lock to ensure the device is awake when vibrating
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "AlarmReceiver::WakeLock");
        try {
            wakeLock.acquire(5000); // 5 seconds
            // Your existing code...
        } finally {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }


        Intent stopIntent = new Intent(context, StopReceiver.class);
        stopIntent.putExtra("eventId", eventId);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        snoozeIntent.putExtra("eventId", eventId);
        snoozeIntent.putExtra("comments", comments); // Add additional details as needed
        snoozeIntent.putExtra("currentTimeMillis", String.valueOf(System.currentTimeMillis()));
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 1, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent deleteIntent = new Intent(context, StopReceiver.class);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, 2, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AlarmManager.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(alarm_title)
                .setContentText(comments)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(stopPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(comments))
                .addAction(new NotificationCompat.Action(R.drawable.ic_snooze, context.getResources().getString(R.string.snooze), snoozePendingIntent))
                .addAction(new NotificationCompat.Action(R.drawable.ic_delete, context.getResources().getString(R.string.dismiss), stopPendingIntent)) // Dismiss action
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);



        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

        } else {
            // Permission is already granted, proceed with posting notifications
            try {
                Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                r = RingtoneManager.getRingtone(context, notificationUri);
                if (r != null && !r.isPlaying()) {
                    r.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            notificationManager.notify(NOTIFICATION_ID, builder.build());

        }


    }


    public void stopRingtone() {
        if (r != null && r.isPlaying()) {
            r.stop();
        }
    }

    public void snooze(String alarmId, String currentTimeMillis, Context context, String comment) {

        stopRingtone();
        // Set the snooze time (e.g., 10 minutes)
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int snoozeMinutes = 5;
        long snoozeTime = snoozeMinutes * 60 * 1000;
        long snoozeTimeMillis = Long.parseLong(currentTimeMillis) + snoozeTime;

        // Create an Intent for the alarm receiver class
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("eventId", alarmId);
        intent.putExtra("currentTimeMillis", snoozeTimeMillis);
        intent.putExtra("comments", comment);


        // Create a PendingIntent for the alarm receiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(alarmId), intent, PendingIntent.FLAG_IMMUTABLE);

        // Set the snooze alarm using the AlarmManager
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, snoozeTimeMillis, pendingIntent);
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, snoozeTimeMillis, pendingIntent);
                }
            }
        }
    }


    public static class StopReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.stopRingtone();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }


    public static class SnoozeReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            String alarmId = intent.getStringExtra("eventId");
            String currentTimeMillis = intent.getStringExtra("currentTimeMillis");
            String comments = intent.getStringExtra("comments");

            AlarmReceiver alarmReceiver = new AlarmReceiver();

            assert currentTimeMillis != null;
            alarmReceiver.snooze(alarmId, currentTimeMillis, context, comments);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    public static class DeleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.stopRingtone();
        }
    }



}
