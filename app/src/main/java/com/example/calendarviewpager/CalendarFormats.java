package com.example.calendarviewpager;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarFormats {
    public static double screenYpotenuseInDp;



    public static String concatDate(String date) {

        String[] parts = date.split("T");
        try {
            // Define the input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE dd/MM/yyyy", new Locale("el", "GR"));

            // Parse the input date string
            Date dateTest = inputFormat.parse(parts[0]);

            // Format the date to the desired output format
            String formattedDate = outputFormat.format(dateTest);
            return formattedDate;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static String concatTime(String time) {
        if (time == null) {
            return null;
        }

        String[] parts = time.split("T");
        if (parts.length < 2) {
            return null; // Handle the case when the input time does not contain "T"
        }

        time = parts[1];
        time = time.substring(0, Math.min(time.length(), time.length() - 3)); // Ensure the substring operation is safe
        return time;
    }

    public static String convertDateForDB(String inputDate) throws ParseException {
        String outputFormat = "yyyy-MM-dd'T'00:00:00";

        // Define a date format for parsing the input date
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("E dd/MM/yyyy", new Locale("el", "GR"));

        // Parse the input date string
        Date date = inputDateFormat.parse(inputDate);

        // Define a date format for formatting the output date
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);

        // Format the date to the desired output format
        return outputDateFormat.format(date);
    }

    public static String convertTimeForDB(String inputTime) {
        String outputFormat = "1902-12-30T%s:00:00";

        // Split the input time into hours and minutes
        String[] parts = inputTime.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        // Format the time to the desired output format
        return String.format(outputFormat, String.format("%02d:%02d", hours, minutes));
    }

    public static String setTimeNow() {
        String formattedTime;
        Calendar calendar = Calendar.getInstance();


        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteOfDay = calendar.get(Calendar.MINUTE);

        calendar.set(hourOfDay, minuteOfDay);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        formattedTime = sdf.format(calendar.getTime());

        return formattedTime;

    }

    public static String formattedDateForNewAction() {
        String formattedDate;
        Calendar calendar = Calendar.getInstance();


        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(year, month, dayOfMonth);
        Locale locale = new Locale("el", "GR");
        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd/MM/yyyy", locale);
        formattedDate = dateFormat.format(calendar.getTime());

        return formattedDate;

    }

    public static void reloadActivity(Activity activity){
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(activity.getIntent());
        activity.overridePendingTransition(0, 0);
    }



        //-------------------For getting screen inches to hypotenousa--------------
    public static void getScreenInches(Activity activity){
        // calculate width and height of screen in pixels


        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);


        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT < 17) {
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (NullPointerException exception) {
                exception.printStackTrace();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }else{ //Build.VERSION.SDK_INT >= 17
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            }
            catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels/dm.xdpi,2);
        double y = Math.pow(mHeightPixels/dm.ydpi,2);
        double inches = Math.sqrt(x+y);
        screenYpotenuseInDp = inches * 160;  // 1 dp equals 1/160 of inch
    }
}
