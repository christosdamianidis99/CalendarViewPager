package com.example.calendarviewpager;


import static com.example.calendarviewpager.CalendarUtils.INPUT_PATTERN_FULL_DATE;
import static com.example.calendarviewpager.CalendarUtils.SHORT_DATE_PATTERN;
import static com.example.calendarviewpager.CalendarUtils.TIME_PATTERN;
import static com.example.calendarviewpager.CalendarUtils.datesAreEqual;
import static com.example.calendarviewpager.CalendarUtils.parseDateToAnOutputPattern;
import static com.example.calendarviewpager.CalendarUtils.replaceTLetterInDatesWithSpaceOrEnter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mycalendar.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


public class CrmJournalActionItemAdapterListView extends ArrayAdapter<CalendarEvent> {
    Context context = this.getContext();
    ArrayList<CalendarEvent> crmJournals;
    boolean weHaveToUseTheWholeList;


    public CrmJournalActionItemAdapterListView(@NonNull Context context, ArrayList<CalendarEvent> crm_journals, boolean weHaveToUseTheWholeList) {
        super(context, R.layout.activity_event, crm_journals);
        this.crmJournals = crm_journals;
        this.weHaveToUseTheWholeList = weHaveToUseTheWholeList;
    }

    @Override
    public int getCount() {
        return crmJournals.size();
    }

    @Nullable
    @Override
    public CalendarEvent getItem(int position) {
        return crmJournals.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("ViewHolder")
        View row = inflater.inflate(R.layout.item_listview_main_activity, parent, false);
        CalendarEvent positionCrmJournal = getItem(position);

        final TextView crm_action_title = row.findViewById(R.id.crm_action_descr);
        final TextView crm_action_startdate = row.findViewById(R.id.crm_action_startdate);

        final TextView crm_action_enddate = row.findViewById(R.id.crm_action_enddate);

        final TextView crm_action_starttime = row.findViewById(R.id.crm_action_starttime);

        final TextView crm_action_endtime = row.findViewById(R.id.crm_action_endtime);
        final TextView action_location = row.findViewById(R.id.crm_action_location);
        final ConstraintLayout mainLayCRMListView = row.findViewById(R.id.itemViewListLay);

        assert positionCrmJournal != null;

        crm_action_title.setText(positionCrmJournal.getDescr());
        action_location.setText(positionCrmJournal.getLocation());

        crm_action_startdate.setText(CalendarUtils.extractDate(positionCrmJournal.getStartDate()));
        crm_action_enddate.setText(CalendarUtils.extractDate(positionCrmJournal.getEndDate()));

        crm_action_starttime.setText(CalendarUtils.extractTime(positionCrmJournal.getStartTime()));
        crm_action_endtime.setText(CalendarUtils.extractTime(positionCrmJournal.getEndTime()));


//        String myDate = "";
//        myDate = parseDateToAnOutputPattern(replaceTLetterInDatesWithSpaceOrEnter(positionCrmJournal.getStartDate(), true), SHORT_DATE_PATTERN, INPUT_PATTERN_FULL_DATE) + " ";
//        if (datesAreEqual(positionCrmJournal.getStartDate(), positionCrmJournal.getEndDate())){
//            if (positionCrmJournal.getStartTime() != null){
//                myDate += parseDateToAnOutputPattern(replaceTLetterInDatesWithSpaceOrEnter(positionCrmJournal.getStartTime(), true), TIME_PATTERN, INPUT_PATTERN_FULL_DATE);
//                if (positionCrmJournal.getEndTime() != null){
//                    myDate += " - " + parseDateToAnOutputPattern(replaceTLetterInDatesWithSpaceOrEnter(positionCrmJournal.getEndTime(), true), TIME_PATTERN, INPUT_PATTERN_FULL_DATE);
//                }
//            }else if (positionCrmJournal.getEndTime() != null){
//                myDate += parseDateToAnOutputPattern(replaceTLetterInDatesWithSpaceOrEnter(positionCrmJournal.getEndTime(), true), TIME_PATTERN, INPUT_PATTERN_FULL_DATE);
//            }
//        }else{ // dates are not equal
//            if (positionCrmJournal.getStartTime() != null){
//                myDate += parseDateToAnOutputPattern(replaceTLetterInDatesWithSpaceOrEnter(positionCrmJournal.getStartTime(), true), TIME_PATTERN, INPUT_PATTERN_FULL_DATE) + " - " + parseDateToAnOutputPattern(replaceTLetterInDatesWithSpaceOrEnter(positionCrmJournal.getEndDate(), true), SHORT_DATE_PATTERN, INPUT_PATTERN_FULL_DATE) + " ";
//            }else{ // start time is null
//                myDate += "- " + parseDateToAnOutputPattern(replaceTLetterInDatesWithSpaceOrEnter(positionCrmJournal.getEndDate(), true), SHORT_DATE_PATTERN, INPUT_PATTERN_FULL_DATE) + " ";
//            }
//            if (positionCrmJournal.getEndTime() != null){
//                myDate += parseDateToAnOutputPattern(replaceTLetterInDatesWithSpaceOrEnter(positionCrmJournal.getEndTime(), true), TIME_PATTERN, INPUT_PATTERN_FULL_DATE);
//            }
//        }
//        crm_action_startdate.setText(myDate);

        if (weHaveToUseTheWholeList && MainActivity.currentSelectedId != null && crmJournals != null){
            if (Objects.equals(crmJournals.get(position).getId(), MainActivity.currentSelectedId)){
                mainLayCRMListView.setBackgroundColor(Color.LTGRAY);
            }
        }

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.CRM_SHOW_MODE = true;
                MainActivity.listViewShownFromMenu = true;
                MainActivity.listViewShownFromDayView = false;
                MainActivity.selectedCalendarEvent = positionCrmJournal;
                MainActivity.position = position;
                EventActivity.crmJournalId = positionCrmJournal.getId();
                Intent i = new Intent(getContext(), EventActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line to set the FLAG_ACTIVITY_NEW_TASK flag
                getContext().startActivity(i);
            }
        });
        return row;
    }
}
