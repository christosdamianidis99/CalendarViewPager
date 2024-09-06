package com.example.calendarviewpager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EventActivity extends AppCompatActivity {
    public static int eventId;
    private TextView tvDescr, tvStartDate, tvStartTime, tvEndDate, tvEndTime, tvDuration, tvLocation, tvReminder, tvComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event);


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

        // Now you can set the values for each field from a CalendarEvent object
        CalendarEvent event = getEventFromIntent();  // Assume this method gets the event object
        populateEventDetails(event);
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

    // Dummy method to simulate getting a CalendarEvent object
    private CalendarEvent getEventFromIntent() {
        // Retrieve CalendarEvent from Intent or database
        return new CalendarEvent();
    }
}