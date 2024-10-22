package com.example.calendarviewpager;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.mycalendar.R;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class AlarmManager {

    public static final String CHANNEL_ID = "CALENDAR_EVENTS_NOTIFICATION_CHANNEL";

    public static void setAlarm(Context context, int eventId, String title, String startDate, String startTime, String reminderIdx) {
        if (startTime != null && !startTime.isEmpty()) {
            if (Integer.parseInt(reminderIdx) != 0) {
                Calendar reminderCalendar = Calendar.getInstance();
                android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmReceiver.class); // Updated target to AlarmReceiver
                intent.putExtra("comments", title);
                intent.putExtra("eventId", eventId);


                int flags = PendingIntent.FLAG_UPDATE_CURRENT;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    flags |= PendingIntent.FLAG_IMMUTABLE;
                }

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        eventId,
                        intent,
                        flags
                );


                // Parse and set the date and time
                Calendar dateCalendar = EventActivity.parseDateString(startDate);
                Calendar timeCalendar = EventActivity.parseTimeString(startTime);

                // Set the parsed date and time into the reminderCalendar
                reminderCalendar.set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR));
                reminderCalendar.set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH));
                reminderCalendar.set(Calendar.DAY_OF_MONTH, dateCalendar.get(Calendar.DAY_OF_MONTH));
                reminderCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                reminderCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
                reminderCalendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));

                long startTimeInMillis = reminderCalendar.getTimeInMillis();


                long triggerTime = startTimeInMillis - EventActivity.getReminderOffset(Integer.valueOf(reminderIdx));
                Date test2 = new Date(triggerTime);
                Date test = reminderCalendar.getTime();
                if (CalendarUtils.convertCalendarToLocalDateTime(reminderCalendar).isAfter(LocalDateTime.now()) ||
                        CalendarUtils.convertCalendarToLocalDateTime(reminderCalendar).isEqual(LocalDateTime.now())) {
                    if (alarmManager != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (alarmManager.canScheduleExactAlarms()) {
                                alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                                Log.d("MYALARM", "SET " + eventId);
                            }
                        }else{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                            }
                            Log.d("MYALARM", "SET " + eventId);
                        }
                    }
                }
            }
        }
    }

    public static void cancelAlarm(Context context, int eventId) {

        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                eventId,
                intent,
                flags
        );
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("MYALARM", "CANCEL " + eventId);
        }
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.notifications);

            String description = context.getResources().getString(R.string.channel_for_notifications);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    name,
                    importance);

            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
