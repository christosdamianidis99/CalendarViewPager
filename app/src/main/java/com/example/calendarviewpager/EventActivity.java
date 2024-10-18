package com.example.calendarviewpager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mycalendar.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class EventActivity extends AppCompatActivity {
    private static final int EVENT_ACTIVITY_MENU_ADD_EVENT = Menu.FIRST + 1;
    public static BigDecimal duration;
    DatabaseHelper db;
    public static int eventId;
    private TextView tvDescr;
    private TextView tvStartDate;
    private TextView tvStartTime;
    private TextView tvEndDate;
    private TextView tvEndTime;
    private static TextView tvDuration;
    private TextView tvLocation;
    private TextView tvReminder;
    private TextView tvComment;
    private EditText etDescr, etLocation, etReminder, etComment;
    public static int crmJournalId;
    public static CalendarEvent positionEvent;
    public static boolean updateStartTimeHasBeenCalled;
    public static boolean updateDeliveryOrEndTimeHasBeenCalled;
    private final Calendar startDateTime = Calendar.getInstance();
    private final Calendar endDateTime = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        db = new DatabaseHelper(this);
        initWidgets();
        setToolbar();
        setToolbarTitle();


        // Set listeners to open pickers
        tvStartDate.setOnClickListener(v -> openDatePicker(true));
        tvEndDate.setOnClickListener(v -> openDatePicker(false));
        tvStartTime.setOnClickListener(v -> openTimePicker(true));
        tvEndTime.setOnClickListener(v -> openTimePicker(false));

        if (MainActivity.CRM_SHOW_MODE) {
            showTextViews();
            showEvent();
        } else if (MainActivity.CRM_NEW_MODE) {
            // Set initial values
            setInitialDateTime();
            showEditTexts();
        } else {
            showEditTexts();
        }


    }

    private void showEvent() {
        for (int i=0; i<MainActivity.crmJournalsData.size(); i++)
        {
            if (MainActivity.crmJournalsData.get(i).getId()==crmJournalId)
            {
                positionEvent =  MainActivity.crmJournalsData.get(i);
                break;
            }
        }

        if (positionEvent!=null)
        {
            tvDescr.setText(positionEvent.getDescr());
            tvStartDate.setText(CalendarUtils.extractDate(positionEvent.getStartDate()));
            tvStartTime.setText(CalendarUtils.extractTime(positionEvent.getStartTime()));
            tvEndDate.setText(CalendarUtils.extractDate(positionEvent.getEndDate()));
            tvStartDate.setText(CalendarUtils.extractTime(positionEvent.getEndTime()));

            tvLocation.setText(positionEvent.getLocation());
            tvReminder.setText(positionEvent.getReminder());
            tvComment.setText(positionEvent.getComment());
        }
    }

    private void initWidgets() {
        // Link the TextViews with their respective IDs in the layout
        tvDescr = findViewById(R.id.tv_descr);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndDate = findViewById(R.id.tv_end_date);
        tvEndTime = findViewById(R.id.tv_end_time);
        tvDuration = findViewById(R.id.tv_duration);
        tvLocation = findViewById(R.id.tv_location);
        tvReminder = findViewById(R.id.tv_reminder);
        tvComment = findViewById(R.id.tv_comment);

        //EditTexts
        etDescr = findViewById(R.id.et_descr);
        etLocation = findViewById(R.id.et_location);
        etReminder = findViewById(R.id.et_reminder);
        etComment = findViewById(R.id.et_comment);


    }

    private void showTextViews() {
        tvDescr.setVisibility(View.VISIBLE);
        etDescr.setVisibility(View.GONE);


        tvReminder.setVisibility(View.VISIBLE);
        etReminder.setVisibility(View.GONE);

        tvLocation.setVisibility(View.VISIBLE);
        etLocation.setVisibility(View.GONE);

        tvComment.setVisibility(View.VISIBLE);
        etComment.setVisibility(View.GONE);

    }

    private void showEditTexts() {
        tvDescr.setVisibility(View.GONE);
        etDescr.setVisibility(View.VISIBLE);


        tvReminder.setVisibility(View.GONE);
        etReminder.setVisibility(View.VISIBLE);

        tvLocation.setVisibility(View.GONE);
        etLocation.setVisibility(View.VISIBLE);

        tvComment.setVisibility(View.GONE);
        etComment.setVisibility(View.VISIBLE);
    }

    // Step 1: Initialize the TextViews with current date and time
    private void setInitialDateTime() {
        tvStartDate.setText(dateFormatter.format(startDateTime.getTime()));
        tvStartTime.setText(timeFormatter.format(startDateTime.getTime()));
        tvEndDate.setText(dateFormatter.format(endDateTime.getTime()));
        tvEndTime.setText(timeFormatter.format(endDateTime.getTime()));
        updateDuration();
    }

    // Step 2: Date Picker
    private void openDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateTime : endDateTime;
        new DatePickerDialog(EventActivity.this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if (isStartDate) {
                tvStartDate.setText(dateFormatter.format(calendar.getTime()));
            } else {
                tvEndDate.setText(dateFormatter.format(calendar.getTime()));
            }
            validateDatesAndTimes();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Step 2: Time Picker
    private void openTimePicker(boolean isStartTime) {
        Calendar calendar = isStartTime ? startDateTime : endDateTime;
        new TimePickerDialog(EventActivity.this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            if (isStartTime) {
                tvStartTime.setText(timeFormatter.format(calendar.getTime()));
            } else {
                tvEndTime.setText(timeFormatter.format(calendar.getTime()));
            }
            validateDatesAndTimes();
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    // Step 3: Validation Logic
    private void validateDatesAndTimes() {
        if (endDateTime.before(startDateTime)) {
            // End date is before start date, reset the end date
            endDateTime.setTime(startDateTime.getTime());
            tvEndDate.setText(dateFormatter.format(endDateTime.getTime()));
            tvEndTime.setText(timeFormatter.format(endDateTime.getTime()));
        } else if (startDateTime.get(Calendar.YEAR) == endDateTime.get(Calendar.YEAR) &&
                startDateTime.get(Calendar.DAY_OF_YEAR) == endDateTime.get(Calendar.DAY_OF_YEAR) &&
                endDateTime.before(startDateTime)) {
            // Same day, but end time is before start time
            endDateTime.setTime(startDateTime.getTime());
            tvEndTime.setText(timeFormatter.format(endDateTime.getTime()));
        }

        // Step 4: Update the duration
        updateDuration();
    }

    // Step 4: Calculate the duration in hours
    private void updateDuration() {
        long durationInMillis = endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();

        // Calculate the total hours (including fractional hours)
        double durationInHours = (double) durationInMillis / (1000 * 60 * 60);

        // Set the duration text
        tvDuration.setText(String.format(Locale.getDefault(), "%.2f hours", durationInHours));
    }


    private void populateEventDetails(CalendarEvent event) {
        if (event != null) {
            tvDescr.setText(event.getDescr());
            tvStartDate.setText(event.getStartDate());
            tvStartTime.setText(event.getStartTime());
            tvEndDate.setText(event.getEndDate());
            tvEndTime.setText(event.getEndTime());
            tvDuration.setText(String.valueOf(event.getDuration()));
            tvLocation.setText(event.getLocation());
            tvReminder.setText(String.valueOf(event.getReminder()));
            tvComment.setText(event.getComment());
        }
    }

    public void setToolbar() {

        toolbar = findViewById(R.id.activity_event_toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.pal_blue));
        // change the color of back button to white
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);


    }

    private void setToolbarTitle() {
        if (MainActivity.CRM_NEW_MODE) {
            String actionTitle = "New event";
            Objects.requireNonNull(getSupportActionBar()).setTitle(actionTitle);
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, EVENT_ACTIVITY_MENU_ADD_EVENT, Menu.NONE, "Add event")
                .setIcon(R.drawable.ic_add_event)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //startActivity(new Intent(EventActivity.this,MainActivity.class));
                onBackPressed();

                break;

            case EVENT_ACTIVITY_MENU_ADD_EVENT:
                addOrEditEvent();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void addOrEditEvent() {
        String startDateStr = dateFormatter.format(startDateTime.getTime()); // Calendar to string (yyyy-MM-dd)
        String startTimeStr = timeFormatter.format(startDateTime.getTime()); // Calendar to string (HH:mm)

        String endDateStr = dateFormatter.format(endDateTime.getTime());
        String endTimeStr = timeFormatter.format(endDateTime.getTime());

        // Get the duration text and clean it
        String durationText = tvDuration.getText().toString().trim();

// Remove the " hours" suffix and replace any commas with dots (if needed)
        String cleanedDurationText = durationText.replace(" hours", "").replace(",", ".");

        CalendarEvent event = new CalendarEvent();
        event.setDescr(etDescr.getText().toString().trim());
        event.setStartDate(startDateStr);
        event.setStartTime(startTimeStr);
        event.setEndDate(endDateStr);
        event.setEndTime(endTimeStr);
        try {
            double duration = Double.valueOf(cleanedDurationText);
            event.setDuration(duration);
        } catch (NumberFormatException e) {
            // Handle the exception (e.g., show an error message)
            Log.e("EventActivity", "Invalid duration format: " + cleanedDurationText);
        }
        event.setLocation(etLocation.getText().toString().trim());
        event.setReminder(Integer.valueOf(etReminder.getText().toString()));
        event.setComment(etComment.getText().toString().trim());
        db.insertEvent(event);
        startActivity(new Intent(EventActivity.this, MainActivity.class));
    }
}