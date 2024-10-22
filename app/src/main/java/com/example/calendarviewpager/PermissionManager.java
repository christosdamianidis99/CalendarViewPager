package com.example.calendarviewpager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionManager {

    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    // List of permissions to be requested
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.POST_NOTIFICATIONS, // For Android 13+
            Manifest.permission.WAKE_LOCK,          // General permission
            Manifest.permission.SCHEDULE_EXACT_ALARM, // Android 12+
            Manifest.permission.USE_EXACT_ALARM       // Android 12+
    };

    // Check if all necessary permissions are granted
    public static boolean areAllPermissionsGranted(Context context) {
        // Check POST_NOTIFICATIONS for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        // Other permissions can be directly checked
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        // Additionally, check for the ability to schedule exact alarms for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                return false;
            }
        }

        return true; // All permissions granted
    }

    // Request all permissions if not already granted
    public static void requestAllPermissionsIfNeeded(AppCompatActivity activity) {
        if (!areAllPermissionsGranted(activity)) {
            // Request POST_NOTIFICATIONS for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            } else {
                // Request other permissions for older versions
                ActivityCompat.requestPermissions(activity, new String[] {
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.SCHEDULE_EXACT_ALARM,
                        Manifest.permission.USE_EXACT_ALARM
                }, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            // All permissions are already granted, proceed to create the notification channel
            AlarmManager.createNotificationChannel(activity.getApplicationContext());
        }
    }

    // Handle the result of the permission request
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, AppCompatActivity activity) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            // Additionally, check if the user can schedule exact alarms for Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                android.app.AlarmManager alarmManager = (android.app.AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                if (!alarmManager.canScheduleExactAlarms()) {
                    allGranted = false;
                }
            }

            if (allGranted) {
                // If all permissions are granted, proceed to create the notification channel
                AlarmManager.createNotificationChannel(activity.getApplicationContext());
            } else {
                // Handle the case when some permissions are not granted (optional: notify the user)
                // Show an explanation to the user about why the permissions are necessary.
            }
        }
    }
}
