package com.example.calendarviewpager;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mycalendar.R;

import java.util.ArrayList;
import java.util.Objects;


public class CalendarEventActionItemAdapterListView extends ArrayAdapter<CalendarEvent> {
    Context context = this.getContext();
    ArrayList<CalendarEvent> calendarEvents;
    boolean weHaveToUseTheWholeList;


    public CalendarEventActionItemAdapterListView(@NonNull Context context, ArrayList<CalendarEvent> calendarEvents, boolean weHaveToUseTheWholeList) {
        super(context, R.layout.activity_event, calendarEvents);
        this.calendarEvents = calendarEvents;
        this.weHaveToUseTheWholeList = weHaveToUseTheWholeList;
    }

    @Override
    public int getCount() {
        return calendarEvents.size();
    }

    @Nullable
    @Override
    public CalendarEvent getItem(int position) {
        return calendarEvents.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("ViewHolder")
        View row = inflater.inflate(R.layout.item_listview_main_activity, parent, false);
        CalendarEvent positionCalendarEvent = getItem(position);

        final TextView calendar_event_action_title = row.findViewById(R.id.action_descr);
        final TextView calendar_event_action_startdate = row.findViewById(R.id.action_startdate);

        final TextView calendar_event_action_enddate = row.findViewById(R.id.action_enddate);

        final TextView calendar_event_action_starttime = row.findViewById(R.id.action_starttime);

        final TextView calendar_event_action_endtime = row.findViewById(R.id.action_endtime);
        final TextView calendar_event_action_location = row.findViewById(R.id.action_location);
        final LinearLayout calendar_event_location_layout = row.findViewById(R.id.event_details_layout);

        final ConstraintLayout mainLayCalendarEventListView = row.findViewById(R.id.itemViewListLay);

        assert positionCalendarEvent != null;

        calendar_event_action_title.setText(positionCalendarEvent.getDescr());

        if (positionCalendarEvent.getLocation() != null && !positionCalendarEvent.getLocation().isEmpty())
        {
            calendar_event_action_location.setText(positionCalendarEvent.getLocation());
        }else
        {
            calendar_event_location_layout.setVisibility(View.GONE);
        }


        calendar_event_action_startdate.setText(CalendarUtils.extractDate(positionCalendarEvent.getStartDate()));
        calendar_event_action_enddate.setText(CalendarUtils.extractDate(positionCalendarEvent.getEndDate()));

        calendar_event_action_starttime.setText(CalendarUtils.extractTime(positionCalendarEvent.getStartTime()));
        calendar_event_action_endtime.setText(CalendarUtils.extractTime(positionCalendarEvent.getEndTime()));



        if (weHaveToUseTheWholeList && MainActivity.currentSelectedId != null && calendarEvents != null){
            if (Objects.equals(calendarEvents.get(position).getId(), MainActivity.currentSelectedId)){
                mainLayCalendarEventListView.setBackgroundColor(Color.LTGRAY);
            }
        }

        row.setOnClickListener(v -> {
            MainActivity.SHOW_MODE = true;
            MainActivity.EDIT_MODE =false;
            MainActivity.NEW_MODE = false;
            MainActivity.listViewShownFromMenu = true;
            MainActivity.listViewShownFromDayView = false;
            MainActivity.selectedCalendarEvent = positionCalendarEvent;
            MainActivity.position = position;
            EventActivity.eventId = positionCalendarEvent.getId();

            Intent i = new Intent(context, EventActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line to set the FLAG_ACTIVITY_NEW_TASK flag
            context.startActivity(i);
        });

        row.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mainLayCalendarEventListView.setBackgroundColor(Color.LTGRAY);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mainLayCalendarEventListView.setBackgroundColor(Color.TRANSPARENT); // Reset to original color
                    break;
            }
            return false; // Return false to allow other events to be handled
        });
        return row;
    }
}
