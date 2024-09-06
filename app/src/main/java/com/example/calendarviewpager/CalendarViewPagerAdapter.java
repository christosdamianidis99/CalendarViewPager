package com.example.calendarviewpager;

import static com.example.calendarviewpager.CalendarUtils.daysInMonthArrayInOrder;
import static com.example.calendarviewpager.CalendarUtils.daysInWeekArray;
import static com.example.calendarviewpager.MainActivity.DAY_VIEW;
import static com.example.calendarviewpager.MainActivity.MONTH_VIEW;
import static com.example.calendarviewpager.MainActivity.WEEK_VIEW;
import static com.example.calendarviewpager.MainActivity.daysStartingFromMonday;
import static com.example.calendarviewpager.MainActivity.monthsInOrder;
import static com.example.calendarviewpager.MainActivity.weeksStartingFromMonday;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;

public class CalendarViewPagerAdapter extends RecyclerView.Adapter<CalendarViewPagerAdapter.CalendarViewHolder> {

    private ArrayList<CalendarEvent> events;

    ArrayList<LocalDateTime> nowMonth = new ArrayList<>();
    ArrayList<LocalDateTime> nowWeek = new ArrayList<>();


    public CalendarViewPagerAdapter(ArrayList<CalendarEvent> events) {

        this.events = events;
    }


    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_views_layout, parent, false);


        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        switch (MainActivity.CALENDAR_MODE) {
            case MONTH_VIEW: {
                YearMonth positionDate = monthsInOrder.get(position);
                nowMonth = daysInMonthArrayInOrder(positionDate);
                holder.bindMonth(nowMonth, events);
                break;
            }
            case WEEK_VIEW: {

                LocalDate weekDate = weeksStartingFromMonday.get(position);
                nowWeek = daysInWeekArray(weekDate);
                holder.bindWeek(nowWeek, events);
                break;
            }
            case DAY_VIEW: {
                LocalDate dayDate = daysStartingFromMonday.get(position);
                holder.bindDay(dayDate.atStartOfDay(), events);
                break;
            }
        }


    }



    @Override
    public int getItemCount() {
        switch (MainActivity.CALENDAR_MODE) {
            case MONTH_VIEW:
                return monthsInOrder.size();
            case WEEK_VIEW:
                return weeksStartingFromMonday.size();
            case DAY_VIEW:
                return daysStartingFromMonday.size();
            default:
                return 0;
        }
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        private final CalendarViews calendarView;
        private final CalendarViews.DaysOfWeekViews daysOfWeekViews;
        private final CalendarViews.StableHours stableHours;


        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            calendarView = itemView.findViewById(R.id.CalendarViews);
            daysOfWeekViews = itemView.findViewById(R.id.daysOfWeekViews);
            stableHours = itemView.findViewById(R.id.stableHours);
        }

        public void bindMonth(ArrayList<LocalDateTime> month, ArrayList<CalendarEvent> events) {

            stableHours.setVisibility(View.GONE);

            daysOfWeekViews.setMonthForViewPagerDaysOfWeek();
            calendarView.setMonthForViewPager(month, events);


        }

        public void bindWeek(ArrayList<LocalDateTime> week, ArrayList<CalendarEvent> events) {

            stableHours.setVisibility(View.VISIBLE);


            daysOfWeekViews.setWeekForViewPagerDaysOfWeek(week, setWeekCrmJournals(week, events));
            calendarView.setWeekForViewPager(setWeekCrmJournals(week, events), week);


        }

            public void bindDay(LocalDateTime day, ArrayList<CalendarEvent> crmJournalsData) {

            stableHours.setVisibility(View.VISIBLE);


            daysOfWeekViews.setDayForViewPagerDaysOfWeek(day, setDayCrmJournals(crmJournalsData, day));
            calendarView.setDayFromViewPagerAdapter(day, setDayCrmJournals(crmJournalsData, day));

        }

        private ArrayList<CalendarEvent> setWeekCrmJournals(ArrayList<LocalDateTime> week, ArrayList<CalendarEvent> crmJournalsData) {
            ArrayList<CalendarEvent> weekJournals = new ArrayList<>();

            for (int i = 0; i < crmJournalsData.size(); i++) {
                for (int j = 0; j < week.size(); j++) {
                    if (CalendarUtils.convertStringToLocalDateTime(crmJournalsData.get(i).getStartDate()).toLocalDate().equals(week.get(j).toLocalDate())) {
                        weekJournals.add(crmJournalsData.get(i));
                    }
                }
            }

            for (int i = 0; i < MainActivity.TempEvents.size(); i++) {
                for (int j = 0; j < week.size(); j++) {
                    if (CalendarUtils.convertStringToLocalDateTime(MainActivity.TempEvents.get(i).getStartDate()).toLocalDate().equals(week.get(j).toLocalDate())&& MainActivity.TempEvents.get(i).isTemp()) {
                        weekJournals.add(MainActivity.TempEvents.get(i));
                    }
                }
            }

            return weekJournals;

        }

        private ArrayList<CalendarEvent> setDayCrmJournals(ArrayList<CalendarEvent> crmJournalsData, LocalDateTime day) {
            ArrayList<CalendarEvent> dayJournals = new ArrayList<>();


            for (int i = 0; i < crmJournalsData.size(); i++) {

                if (CalendarUtils.convertStringToLocalDateTime(crmJournalsData.get(i).getStartDate()).toLocalDate().equals(day.toLocalDate())) {
                    dayJournals.add(crmJournalsData.get(i));
                }

            }


            for (int i = 0; i < MainActivity.TempEvents.size(); i++) {

                if (CalendarUtils.convertStringToLocalDateTime(MainActivity.TempEvents.get(i).getStartDate()).toLocalDate().equals(day.toLocalDate()) &&  MainActivity.TempEvents.get(i).isTemp()) {
                    dayJournals.add(MainActivity.TempEvents.get(i));
                }

            }
            return dayJournals;
        }
    }
}
