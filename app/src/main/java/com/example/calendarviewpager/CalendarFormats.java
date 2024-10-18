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

    /*----------------------NewCRM & UPDCRM functions---------------------- */

    public static void showDatePickerDialogEndDate(TextView dateTextView, Context context, String startDate, String startTime, String endTime) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Create a SimpleDateFormat to format the date
                        Locale locale = new Locale("el", "GR");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd/MM/yyyy", locale);

                        // Create a Calendar instance and set it to the selected date
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        // Format the selected date and set it to the TextView
                        String formattedDate = dateFormat.format(selectedDate.getTime());


//                        try {
////                            if (!dateTimeAreTrueParams(startDate, startTime, formattedDate, endTime)) {
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_datepicker.setText(CrmJournalFormats.formattedDateForNewAction());
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_enddatepicker.setText(CrmJournalFormats.formattedDateForNewAction());
////
////                                MineNewCrmJournalActivity.startDate = CrmJournalFormats.formattedDateForNewAction();
////                                MineNewCrmJournalActivity.endDate = CrmJournalFormats.formattedDateForNewAction();
////
////
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_duration.setText("0" + " hours");
////                            } else {
////                                MineNewCrmJournalActivity.startDate = startDate;
////                                MineNewCrmJournalActivity.endDate = formattedDate;
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_duration.setText(calculateTimeDuration(startTime, endTime));
////                                dateTextView.setText(formattedDate);
////
////
////                            }
//                        } catch (ParseException e) {
//                            throw new RuntimeException(e);
//                        }


                    }
                },
                year, month, dayOfMonth
        );

        datePickerDialog.show();


    }

    public static void showTimePickerDialogStartTime(TextView timeTextView, Context context, String startDate, String endDate, String endTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update the TextView with the selected time
                        String selectedTime = hourOfDay + ":" + minute;
                        if (minute < 10) {
                            selectedTime = hourOfDay + ":" + "0" + minute;
                        }


//                        try {
////                            if (!dateTimeAreTrueParams(startDate, selectedTime, endDate, endTime)) {
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_timepicker.setText(setTimeNow());
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_end_timepicker.setText(setTimeNow());
////
////                                MineNewCrmJournalActivity.startTime = setTimeNow();
////                                MineNewCrmJournalActivity.endTime = setTimeNow();
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_duration.setText("0" + " hours");
////
////                            } else {
////                                MineNewCrmJournalActivity.startTime = selectedTime;
////                                MineNewCrmJournalActivity.endTime = endTime;
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_duration.setText(calculateTimeDuration(selectedTime, endTime));
////                                timeTextView.setText(selectedTime);
////
////                            }
//                        } catch (ParseException e) {
//                            throw new RuntimeException(e);
//                        }


                    }

                },
                hour, minute, true

        );


        timePickerDialog.show();


    }

    public static void showTimePickerDialogEndTime(TextView timeTextView, Context context, String startDate, String startTime, String endDate) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update the TextView with the selected time
                        String selectedTime = hourOfDay + ":" + minute;
                        if (minute < 10) {
                            selectedTime = hourOfDay + ":" + "0" + minute;
                        }

//
//                        try {
////                            if (!dateTimeAreTrueParams(startDate, startTime, endDate, selectedTime)) {
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_timepicker.setText(setTimeNow());
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_end_timepicker.setText(setTimeNow());
////
////                                MineNewCrmJournalActivity.startTime = setTimeNow();
////                                MineNewCrmJournalActivity.endTime = setTimeNow();
////
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_duration.setText("0" + " hours");
////
////                            } else {
////                                MineNewCrmJournalActivity.endTime = selectedTime;
////                                MineNewCrmJournalActivity.startTime = startTime;
////                                NewCrmJournalDateTimeParams.view_new_action_time_params_duration.setText(calculateTimeDuration(startTime, selectedTime));
////                                timeTextView.setText(selectedTime);
////
////
////                            }
//                        } catch (ParseException e) {
//                            throw new RuntimeException(e);
//                        }


                    }

                },
                hour, minute, true

        );


        timePickerDialog.show();


    }


    public static String getTime30MinutesBefore(int hourOfDay, int minute) {
        // Create a Calendar instance and set the provided hour and minute
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        // Subtract 30 minutes
        calendar.add(Calendar.MINUTE, -30);

        // Get the updated hour and minute
        int updatedHour = calendar.get(Calendar.HOUR_OF_DAY);
        int updatedMinute = calendar.get(Calendar.MINUTE);

        // Format the result as a string (HH:mm)


        return updatedHour + ":" + updatedMinute;
    }

    public static String calculateTimeDuration(String firstTime, String secondTime) throws ParseException {
        if (firstTime == null || secondTime == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        double result;

        // Parse the time strings into Date objects
        Date date1 = sdf.parse(firstTime);
        Date date2 = sdf.parse(secondTime);

        // Check if parsing was successful
        if (date1 == null || date2 == null) {
            return null; // Return null if parsing failed for any of the times
        }

        // Calculate the duration in milliseconds
        long durationMillis = date2.getTime() - date1.getTime();

        // Convert milliseconds to hours with decimal places
        result = (double) durationMillis / (60 * 60 * 1000);

        DecimalFormat df = new DecimalFormat("0.00");

        return df.format(result) + " hours";
    }

    public static int compareTimes(String time1, String time2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // Parse the time strings into Date objects
        Date date1 = sdf.parse(time1);
        Date date2 = sdf.parse(time2);

        // Compare the Date objects
        assert date1 != null;
        if (date1.before(date2)) {
            return -1; // time1 is earlier than time2
        } else if (date1.after(date2)) {
            return 1; // time1 is later than time2
        } else {
            return 0; // time1 and time2 are equal
        }
    }


    public static boolean dateTimeAreTrueParams(String startDateSTR, String startTimeSTR, String endDateSTR, String endTimeSTR) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Locale locale = new Locale("el", "GR");
        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd/MM/yyyy", locale);
        if (!(startDateSTR.equals("") && endDateSTR.equals(""))) {

            Date startDate = dateFormat.parse(startDateSTR);
            Date endDate = dateFormat.parse(endDateSTR);
            Date startTime = timeFormat.parse(startTimeSTR);
            Date endTime = timeFormat.parse(endTimeSTR);

            assert startDate != null;
            assert endDate != null;
            assert startTime != null;
            assert endTime != null;

            if (startDate.getTime() < endDate.getTime() && startTime.getTime() < endTime.getTime()) {
                return true;
            } else if (startDate.getTime() == endDate.getTime() && startTime.getTime() == endTime.getTime()) {
                return true;
            } else if (startDate.getTime() < endDate.getTime() && startTime.getTime() == endTime.getTime()) {
                return true;
            } else if (startDate.getTime() == endDate.getTime() && startTime.getTime() < endTime.getTime()) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
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
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        screenHeight = (int) displayMetrics.heightPixels;
//        screenWidth = (int) displayMetrics.widthPixels;

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
