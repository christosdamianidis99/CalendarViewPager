package com.example.calendarviewpager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mycalendar.R;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EventActivity extends AppCompatActivity {
    private static final int EVENT_ACTIVITY_MENU_ADD_EVENT = Menu.FIRST + 1;
    private static final int EVENT_ACTIVITY_MENU_EDIT_EVENT = Menu.FIRST + 2;
    private static final int EVENT_ACTIVITY_MENU_DELETE_EVENT = Menu.FIRST + 3;

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
    private TextView tvComment;
    private EditText etDescr, etLocation, etComment;
    Spinner reminderSpinner;
    Spinner colorSpinner;
    public static CalendarEvent positionEvent;
    public static boolean updateStartTimeHasBeenCalled;
    public static boolean updateDeliveryOrEndTimeHasBeenCalled;
    private Calendar startDateTime = Calendar.getInstance();
    private Calendar endDateTime = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm", Locale.getDefault());
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm", Locale.getDefault());
    androidx.appcompat.widget.Toolbar toolbar;
    private int reminderChoice;
    private int colorChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        db = new DatabaseHelper(this);
        initWidgets();
        setToolbar();
        setToolbarTitle();

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> reminderAdapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_options, android.R.layout.simple_spinner_item);
        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSpinner.setAdapter(reminderAdapter);

      String[]  colorNames = getResources().getStringArray(R.array.color_array);
        int[] colorValues =  new int[]{
                getResources().getColor(R.color.pal_blue),  // Default
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.orange),
                getResources().getColor(R.color.pink),
                getResources().getColor(R.color.yellow),
                getResources().getColor(R.color.green)
        };

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_color, colorNames) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // Check if convertView is null
                if (convertView == null) {
                    // Inflate a new view if it is null
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_color, parent, false);
                }

                // Get references to the TextView and View
                TextView colorName = convertView.findViewById(R.id.color_name);
                View colorSphere = convertView.findViewById(R.id.color_sphere);

                // Set the text and background color
                colorName.setText(getItem(position));
                colorSphere.setBackgroundColor(colorValues[position]);

                return convertView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Check if convertView is null
                if (convertView == null) {
                    // Inflate a new view if it is null
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_color, parent, false);
                }

                // Get references to the TextView and View
                TextView colorName = convertView.findViewById(R.id.color_name);
                View colorSphere = convertView.findViewById(R.id.color_sphere);

                // Set the text and background color
                colorName.setText(getItem(position));
                colorSphere.setBackgroundColor(colorValues[position]);

                return convertView;
            }
        };

// Set the adapter to the Spinner
        colorSpinner.setAdapter(colorAdapter);


        // Set listeners to open pickers
        tvStartDate.setOnClickListener(v -> openDatePicker(true));
        tvEndDate.setOnClickListener(v -> openDatePicker(false));
        tvStartTime.setOnClickListener(v -> openTimePicker(true));
        tvEndTime.setOnClickListener(v -> openTimePicker(false));

        if (MainActivity.SHOW_MODE) {
            showTextViews();
            if (tvLocation.getText() != null || tvLocation.getText() != "") {
                tvLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openMaps(tvLocation.getText().toString().trim());
                    }
                });
            }

            showEvent();
        } else if (MainActivity.NEW_MODE) {
            // Set initial values
            setInitialDateTime();
            showEditTexts();
            reminderChoice();
            colorChoice();
        } else {
            showEditTexts();
            reminderChoice();
            showEventForEdit();
            colorChoice();
        }


    }

    private void showEvent() {
        for (int i = 0; i < MainActivity.EventsData.size(); i++) {
            if (MainActivity.EventsData.get(i).getId() == eventId) {
                positionEvent = MainActivity.EventsData.get(i);
                break;
            }
        }

        if (positionEvent != null) {
            tvDescr.setText(positionEvent.getDescr());
            tvStartDate.setText(CalendarUtils.extractDate(positionEvent.getStartDate()));
            tvStartTime.setText(CalendarUtils.extractTime(positionEvent.getStartTime()));
            tvEndDate.setText(CalendarUtils.extractDate(positionEvent.getEndDate()));
            tvEndTime.setText(CalendarUtils.extractTime(positionEvent.getEndTime()));
            startDateTime = CalendarUtils.getCalendarFromString(positionEvent.getStartDate());
            endDateTime = CalendarUtils.getCalendarFromString(positionEvent.getEndDate());
            updateDuration();
            tvLocation.setText(positionEvent.getLocation());
            reminderSpinner.setSelection(positionEvent.getReminder());
            colorSpinner.setSelection(positionEvent.getColor());
            tvComment.setText(positionEvent.getComment());
        }
    }

    private void showEventForEdit() {
        for (int i = 0; i < MainActivity.EventsData.size(); i++) {
            if (MainActivity.EventsData.get(i).getId() == eventId) {
                positionEvent = MainActivity.EventsData.get(i);
                break;
            }
        }

        if (positionEvent != null) {
            etDescr.setText(positionEvent.getDescr());
            tvStartDate.setText(CalendarUtils.extractDate(positionEvent.getStartDate()));
            tvStartTime.setText(CalendarUtils.extractTime(positionEvent.getStartTime()));
            tvEndDate.setText(CalendarUtils.extractDate(positionEvent.getEndDate()));
            tvEndTime.setText(CalendarUtils.extractTime(positionEvent.getEndTime()));
            startDateTime = CalendarUtils.getCalendarFromString(positionEvent.getStartDate());
            endDateTime = CalendarUtils.getCalendarFromString(positionEvent.getEndDate());
            updateDuration();
            etLocation.setText(positionEvent.getLocation());
            reminderSpinner.setSelection(positionEvent.getReminder());
            reminderSpinner.setEnabled(true);

            colorSpinner.setSelection(positionEvent.getColor());
            colorSpinner.setEnabled(true);
            etComment.setText(positionEvent.getComment());
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
        tvComment = findViewById(R.id.tv_comment);

        //EditTexts
        etDescr = findViewById(R.id.et_descr);
        etLocation = findViewById(R.id.et_location);
        reminderSpinner = findViewById(R.id.reminder_spinner);
        colorSpinner = findViewById(R.id.spinner_color);
        etComment = findViewById(R.id.et_comment);


    }

    private void showTextViews() {
        tvDescr.setVisibility(View.VISIBLE);
        etDescr.setVisibility(View.GONE);


        tvStartDate.setClickable(false);
        tvStartTime.setClickable(false);

        tvEndDate.setClickable(false);
        tvEndTime.setClickable(false);

        reminderSpinner.setEnabled(false);
        colorSpinner.setEnabled(false);

        tvLocation.setVisibility(View.VISIBLE);
        etLocation.setVisibility(View.GONE);

        tvComment.setVisibility(View.VISIBLE);
        etComment.setVisibility(View.GONE);

    }

    private void showEditTexts() {
        tvDescr.setVisibility(View.GONE);
        etDescr.setVisibility(View.VISIBLE);

        reminderSpinner.setVisibility(View.VISIBLE);
        colorSpinner.setVisibility(View.VISIBLE);

        tvLocation.setVisibility(View.GONE);
        etLocation.setVisibility(View.VISIBLE);

        tvComment.setVisibility(View.GONE);
        etComment.setVisibility(View.VISIBLE);
    }

    private void openMaps(String location) {
        // Create a Uri from the location String
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));

        // Create an Intent to launch Google Maps
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Specify the Google Maps package

        // Check if there is an app that can handle the intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent); // Start the activity
        } else {
            Toast.makeText(this, "Google Maps app is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    // Step 1: Initialize the TextViews with current date and time
    private void setInitialDateTime() {
        tvStartDate.setText(CalendarUtils.extractDate(dateFormatter.format(startDateTime.getTime())));
        tvStartTime.setText(CalendarUtils.extractTime(timeFormatter.format(startDateTime.getTime())));
        tvEndDate.setText(CalendarUtils.extractDate(dateFormatter.format(endDateTime.getTime())));
        tvEndTime.setText(CalendarUtils.extractTime(timeFormatter.format(endDateTime.getTime())));
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
                tvStartDate.setText(CalendarUtils.extractDate(dateFormatter.format(calendar.getTime())));
            } else {
                tvEndDate.setText(CalendarUtils.extractDate(dateFormatter.format(calendar.getTime())));
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
                tvStartTime.setText(CalendarUtils.extractTime(timeFormatter.format(calendar.getTime())));
            } else {
                tvEndTime.setText(CalendarUtils.extractTime(timeFormatter.format(calendar.getTime())));
            }
            validateDatesAndTimes();
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    // Step 3: Validation Logic
    private void validateDatesAndTimes() {
        if (endDateTime.before(startDateTime)) {
            // End date is before start date, reset the end date
            endDateTime.setTime(startDateTime.getTime());
            tvEndDate.setText(CalendarUtils.extractDate(dateFormatter.format(endDateTime.getTime())));
            tvEndTime.setText(CalendarUtils.extractTime(timeFormatter.format(endDateTime.getTime())));
        } else if (startDateTime.get(Calendar.YEAR) == endDateTime.get(Calendar.YEAR) &&
                startDateTime.get(Calendar.DAY_OF_YEAR) == endDateTime.get(Calendar.DAY_OF_YEAR) &&
                endDateTime.before(startDateTime)) {
            // Same day, but end time is before start time
            endDateTime.setTime(startDateTime.getTime());
            tvEndTime.setText(CalendarUtils.extractTime(timeFormatter.format(endDateTime.getTime())));
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

    private void reminderChoice() {
        reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item, e.g., get reminder time
                reminderChoice = position;
                // Do something with the selected reminder (e.g., save it or display it)
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something when no item is selected
            }
        });

    }

    private void colorChoice() {
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item, e.g., get reminder time
                colorChoice = position;
                // Do something with the selected reminder (e.g., save it or display it)
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something when no item is selected
            }
        });

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
        if (MainActivity.NEW_MODE) {
            String actionTitle = "New event";
            Objects.requireNonNull(getSupportActionBar()).setTitle(actionTitle);
        }


    }

    public static long getReminderOffset(Integer idx) {
        // Define time offsets in milliseconds
        final long FIVE_MINUTES = 5 * 60 * 1000;    // 5 minutes in milliseconds
        final long TEN_MINUTES = 10 * 60 * 1000;    // 10 minutes in milliseconds
        final long THIRTY_MINUTES = 30 * 60 * 1000; // 30 minutes in milliseconds
        final long ONE_HOUR = 60 * 60 * 1000;       // 1 hour in milliseconds
        final long TWO_HOURS = 2 * 60 * 60 * 1000;  // 2 hours in milliseconds

        // Switch based on reminder index
        switch (idx) {
            case 0:
                return 0L;  // "Nothing" -> no reminder
            case 1:
                return 0L;  // "Entry" -> trigger at event start time (no offset)
            case 2:
                return FIVE_MINUTES;  // 5 minutes before
            case 3:
                return TEN_MINUTES;   // 10 minutes before
            case 4:
                return THIRTY_MINUTES; // 30 minutes before
            case 5:
                return ONE_HOUR;      // 1 hour before
            case 6:
                return TWO_HOURS;     // 2 hours before
            default:
                return 0L;            // Default: no offset (just in case)
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (MainActivity.NEW_MODE || MainActivity.EDIT_MODE) {
            menu.add(0, EVENT_ACTIVITY_MENU_ADD_EVENT, Menu.NONE, "Add event")
                    .setIcon(R.drawable.ic_add_event)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        } else {
            menu.add(0, EVENT_ACTIVITY_MENU_EDIT_EVENT, Menu.NONE, "Edit event")
                    .setIcon(R.drawable.ic_edit_event)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            menu.add(0, EVENT_ACTIVITY_MENU_DELETE_EVENT, Menu.NONE, "Edit event")
                    .setIcon(R.drawable.ic_delete)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }


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
            case EVENT_ACTIVITY_MENU_EDIT_EVENT:
                MainActivity.EDIT_MODE = true;
                MainActivity.NEW_MODE = false;
                MainActivity.SHOW_MODE = false;
                CalendarFormats.reloadActivity(this);
                break;
            case EVENT_ACTIVITY_MENU_DELETE_EVENT:
                deleteEvent();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEvent() {

        new AlertDialog.Builder(EventActivity.this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete " + positionEvent.getDescr() + ".")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.deleteEvent(eventId);
                    startActivity(new Intent(EventActivity.this, MainActivity.class));
                })
                .setNegativeButton("No", null)
                .show();

    }

    public static int setColorOfCalendarEvent(CalendarEvent event) {
        switch (event.getColor()) {
            case 0:
                return MainActivity.myActivity.getApplicationContext().getResources().getColor(R.color.pal_blue);

            case 1:
                return MainActivity.myActivity.getApplicationContext().getResources().getColor(R.color.red);

            case 2:
                return MainActivity.myActivity.getApplicationContext().getResources().getColor(R.color.orange);

            case 3:
                return MainActivity.myActivity.getApplicationContext().getResources().getColor(R.color.pink);

            case 4:
                return MainActivity.myActivity.getApplicationContext().getResources().getColor(R.color.yellow);

            case 5:
                return MainActivity.myActivity.getApplicationContext().getResources().getColor(R.color.green);

        }
        return MainActivity.myActivity.getApplicationContext().getResources().getColor(R.color.pal_blue);
    }

    public static Calendar parseDateString(String dateString) {
        // Split the dateString by the "T" character
        String[] parts = dateString.split("T");

        // Take only the part before "T"
        String datePart = parts[0];

        // Create a SimpleDateFormat object to parse the string
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            // Parse the string into a Date object
            Date date = sdf.parse(datePart);

            // Set the Calendar instance's time to the parsed date
            calendar.setTime(date);
        } catch (ParseException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace(); // Handle parsing errors here
        }
        return calendar;
    }

    public static Calendar parseTimeString(String dateString) {
        // Split the dateString by the "T" character
        String[] parts = dateString.split("T");

        // Take only the part before "T"
        String datePart = parts[1];

        // Create a SimpleDateFormat object to parse the string
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        try {
            // Parse the string into a Date object
            Date date = sdf.parse(datePart);

            // Set the Calendar instance's time to the parsed date
            calendar.setTime(date);
        } catch (ParseException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace(); // Handle parsing errors here
        }
        return calendar;
    }


    private void addOrEditEvent() {
        if ((etDescr.getText() == null || etDescr.getText().toString().equals(""))) {
            etDescr.setText(R.string.no_title);
        }
        if (MainActivity.NEW_MODE) {
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
            event.setReminder(reminderChoice);
            event.setColor(colorChoice);
            event.setComment(etComment.getText().toString().trim());
            int newEventId = (int) db.insertEvent(event);

            AlarmManager.setAlarm(this, newEventId, event.getDescr(), event.getStartDate(), event.getStartTime(), String.valueOf(event.getReminder()));
            startActivity(new Intent(EventActivity.this, MainActivity.class));
        } else {

            CalendarEvent updateEvent = db.getEventById(eventId);
            String startDateStr = dateFormatter.format(startDateTime.getTime()); // Calendar to string (yyyy-MM-dd)
            String startTimeStr = timeFormatter.format(startDateTime.getTime()); // Calendar to string (HH:mm)

            String endDateStr = dateFormatter.format(endDateTime.getTime());
            String endTimeStr = timeFormatter.format(endDateTime.getTime());

            // Get the duration text and clean it
            String durationText = tvDuration.getText().toString().trim();

// Remove the " hours" suffix and replace any commas with dots (if needed)
            String cleanedDurationText = durationText.replace(" hours", "").replace(",", ".");

            updateEvent.setDescr(etDescr.getText().toString().trim());
            updateEvent.setStartDate(startDateStr);
            updateEvent.setStartTime(startTimeStr);
            updateEvent.setEndDate(endDateStr);
            updateEvent.setEndTime(endTimeStr);
            try {
                double duration = Double.valueOf(cleanedDurationText);
                updateEvent.setDuration(duration);
            } catch (NumberFormatException e) {
                // Handle the exception (e.g., show an error message)
                Log.e("EventActivity", "Invalid duration format: " + cleanedDurationText);
            }
            updateEvent.setLocation(etLocation.getText().toString().trim());
            updateEvent.setReminder(reminderChoice);
            updateEvent.setColor(colorChoice);
            updateEvent.setComment(etComment.getText().toString().trim());

            int updateId = db.updateEvent(updateEvent);
            AlarmManager.cancelAlarm(this, updateId);
            AlarmManager.setAlarm(this, updateId, updateEvent.getDescr(), updateEvent.getStartDate(), updateEvent.getStartTime(), String.valueOf(updateEvent.getReminder()));

            MainActivity.SHOW_MODE = true;
            MainActivity.EDIT_MODE = false;
            MainActivity.NEW_MODE = false;
            positionEvent = db.getEventById(updateId);
            MainActivity.EventsData = db.getAllCalendarEvents();
            CalendarFormats.reloadActivity(this);
        }


    }

    @Override
    public void onBackPressed() {
        if (MainActivity.SHOW_MODE || MainActivity.NEW_MODE) {
            startActivity(new Intent(EventActivity.this, MainActivity.class));
        } else {
            MainActivity.SHOW_MODE = true;
            MainActivity.EDIT_MODE = false;
            MainActivity.NEW_MODE = false;
            CalendarFormats.reloadActivity(this);
            //super.onBackPressed();
        }

    }
}