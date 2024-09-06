package com.example.calendarviewpager;

import static com.example.calendarviewpager.CalendarUtils.selectedDate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CalendarView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static Activity MainActivity;
    public static int position;
    //----------CALENDAR_VIEWS--------------------
    public static final int MONTH_VIEW = 0;
    public static final int WEEK_VIEW = 1;
    public static final int DAY_VIEW = 2;
    public static final int LISTVIEW_VIEW = 3;

    public  static final int CALENDAR_MODE=0;
    public static ArrayList<CalendarEvent> Events = new ArrayList<>();
    public static ArrayList<CalendarEvent> TempEvents = new ArrayList<>();
    //----------CALENDAR_MODES--------------------
    public static boolean CRM_SHOW_MODE = false;
    public static boolean CRM_EDIT_MODE = false;
    public static boolean CRM_NEW_MODE = false;

    //-----------------STATE_TO_OPEN_LISTVIEW------------------
    public static boolean listViewShownFromDayView = false;
    public static boolean listViewShownFromMenu = false;

    public static CalendarEvent selectedCalendarEvent;
    public static int initialPosition;
    public static boolean isRefreshPressed = false;
    public static boolean isCalendarPopUpViewPressed = false;
    private static PopupWindow calendarPopup;

    //----------POPULATE_DAYS_FOR_VIEWS--------------------
    public static ArrayList<YearMonth> monthsInOrder = CalendarUtils.monthsInOrder();
    public static ArrayList<LocalDate> weeksStartingFromMonday = CalendarUtils.weeksStartingFromMonday();
    public static ArrayList<LocalDate> daysStartingFromMonday = CalendarUtils.daysInAllMonths();

    public static boolean monthViewCellClicked;
    public static boolean weekViewCellClicked;
    public static boolean loadListInCrmJournalsHasBeenCalled;
    public static LocalDate tempSelectedDate = selectedDate;
    public static Integer currentSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    //------------Set Views----------------------------
    private static void setMonth() {
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        YearMonth nowYearMonth = YearMonth.from(CalendarUtils.selectedDate);
        initialPosition = CalendarUtils.monthsInOrder().indexOf(nowYearMonth);


        initializeClickBolleans();

        initVisibilitiesCalendarViews();
        startToolbarCalendarView();

        CalendarViewPagerAdapter adapter = new CalendarViewPagerAdapter(Events);
        setViewPager(adapter, initialPosition);
    }

    private static void setWeek() {
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }
        // Check if the calendar mode is set to MONTH_VIEW
        if (CALENDAR_MODE == MONTH_VIEW || CALENDAR_MODE == LISTVIEW_VIEW) {
            // Check if the selected date is in the current month
            if (selectedDate.getMonthValue() == LocalDate.now().getMonthValue()) {
                for (int i = 0; i < weeksStartingFromMonday.size(); i++) {
                    LocalDate weekStart = weeksStartingFromMonday.get(i);
                    LocalDate weekEnd = weekStart.plusDays(6); // Assuming Sunday to Saturday

                    // Check if the current week includes today's date
                    if (weeksStartingFromMonday.get(i).equals(LocalDate.now()) ||
                            (weekStart.isBefore(LocalDate.now()) && (weekEnd.isAfter(LocalDate.now())) || weekEnd.isEqual(LocalDate.now()))) {
                        initialPosition = i;
                    }
                }
            } else {
                // If the selected date is not in the current month, find the corresponding week
                for (int i = 0; i < weeksStartingFromMonday.size(); i++) {
                    LocalDate weekStart = weeksStartingFromMonday.get(i);
                    LocalDate weekEnd = weekStart.plusDays(6); // Assuming Sunday to Saturday

                    // Check if the selected date falls within the current week
                    if (!CalendarUtils.selectedDate.isBefore(weekStart) && !CalendarUtils.selectedDate.isAfter(weekEnd)) {
                        initialPosition = i;
                        break; // We found the week, so we can exit the loop
                    }
                }

            }
        }
        else if (CALENDAR_MODE == WEEK_VIEW ) {
            for (int i = 0; i < weeksStartingFromMonday.size(); i++) {
                LocalDate weekStart = weeksStartingFromMonday.get(i);
                LocalDate weekEnd = weekStart.plusDays(6); // Assuming Sunday to Saturday
                if (!LocalDate.now().isBefore(weekStart) && !LocalDate.now().isAfter(weekEnd)) {
                    initialPosition = i;
                    break; // We found the week, so we can exit the loop
                }
            }
        } else if (CALENDAR_MODE == DAY_VIEW) {
            // If the calendar mode is set to DAY_VIEW, find the corresponding week for the selected date
            for (int i = 0; i < weeksStartingFromMonday.size(); i++) {
                LocalDate weekStart = weeksStartingFromMonday.get(i);
                LocalDate weekEnd = weekStart.plusDays(6); // Assuming Sunday to Saturday

                // Check if the selected date falls within the current week
                if (selectedDate.equals(weekStart) || selectedDate.equals(weekEnd) || (selectedDate.isAfter(weekStart) && selectedDate.isBefore(weekEnd))) {
                    initialPosition = i;
                }

            }
        }
        initVisibilitiesCalendarViews();
        startToolbarCalendarView();
        initializeClickBolleans();

        CalendarViewPagerAdapter adapter = new CalendarViewPagerAdapter(Events);

        setViewPager(adapter, initialPosition);
    }

    private static void setDaily() {
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }
        if (CALENDAR_MODE == MONTH_VIEW && !monthViewCellClicked) { //Check previous calendarMode if Month
            //If the month value of selectedDate is LocalDate.Now then get to LocalDate.now day
            // if not to first of the month of selectedDate
            if (selectedDate.getMonthValue() == LocalDate.now().getMonthValue()) {
                initialPosition = daysStartingFromMonday.indexOf(LocalDate.now());
            } else {
                initialPosition = daysStartingFromMonday.indexOf(selectedDate.withDayOfMonth(1));
            }
        } else if (CALENDAR_MODE == MONTH_VIEW && monthViewCellClicked) {
            initialPosition = daysStartingFromMonday.indexOf(selectedDate);
        } else if (CALENDAR_MODE == WEEK_VIEW && !weekViewCellClicked) {//Check previous calendarMode if Week
            for (int i = 0; i < weeksStartingFromMonday.size(); i++) {
                //Iterate through weeks to check in which week is the selected Date
                //After locate the week check also if the LocalDate.now is in this week
                //If yes then position of dayView is LocalDate.now
                //If not then position of dayView is selectedDate minus 3 days to reach first day of week
                LocalDate weekStart = weeksStartingFromMonday.get(i);
                LocalDate weekEnd = weekStart.plusDays(6);

                if (weekStart.equals(selectedDate) || weekEnd.equals(selectedDate) || (weekStart.isBefore(selectedDate) && weekEnd.isAfter(selectedDate))) {
                    if (weekStart.equals(LocalDate.now()) || weekEnd.equals(LocalDate.now()) || (weekStart.isBefore(LocalDate.now()) && weekEnd.isAfter(LocalDate.now()))) {
                        for (int j = 0; j < daysStartingFromMonday.size(); j++) {
                            if (daysStartingFromMonday.get(j).equals(LocalDate.now())) {
                                initialPosition = j;
                                break;
                            }
                        }
                    } else {
                        for (int j = 0; j < daysStartingFromMonday.size(); j++) {
                            if (daysStartingFromMonday.get(j).equals(selectedDate.minusDays(3))) {
                                initialPosition = j;
                                break;

                            }
                        }
                    }

                }
            }
        } else if (CALENDAR_MODE == WEEK_VIEW && weekViewCellClicked) {
            initialPosition = daysStartingFromMonday.indexOf(selectedDate);
        } else if (CALENDAR_MODE == DAY_VIEW) {
            initialPosition = daysStartingFromMonday.indexOf(LocalDate.now());
        }else {
            initialPosition = daysStartingFromMonday.indexOf(selectedDate);
        }



        initVisibilitiesCalendarViews();
        startToolbarCalendarView();

        CalendarViewPagerAdapter adapter = new CalendarViewPagerAdapter(Events);
        setViewPager(adapter, initialPosition);
    }

    private static void initializeClickBolleans(){
        if (monthViewCellClicked){
            monthViewCellClicked = false;
        }
        if (weekViewCellClicked){
            weekViewCellClicked = false;
        }
    }

    //Hides or show correct layouts for days of months
    private static void initVisibilitiesCalendarViews() {
        listView.setVisibility(View.GONE);
        if (!SyncronizationVars.syncHasStarted) {
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    //Set viewpager adapter, offscreenPageLimit=How many viewpager pages stay in memory
    public static void setViewPager(CalendarViewPagerAdapter adapter, int initialPosition) {
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        setCurrentItemCurrentDayAction(initialPosition);
    }

    //Set and scroll to current place of the calendar based the selected date.
    public static void setCurrentItemCurrentDayAction(int initialPosition) {
        if (isRefreshPressed || isCalendarPopUpViewPressed) {
            if (LocalDateTime.now().toLocalDate().getYear()==tempSelectedDate.getYear()
                    &&LocalDateTime.now().toLocalDate().getMonthValue()==tempSelectedDate.getMonthValue()) {
                viewPager.setCurrentItem(initialPosition, false);
            } else {
                viewPager.setCurrentItem(initialPosition, true);
            }

            isRefreshPressed = false;
        } else {
            viewPager.setCurrentItem(initialPosition, false);
        }
    }

    //Setter of listview
    private static void viewListViewItems() {
        initializeClickBolleans();
        frameLayout.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        changeCalendarModeInDbAndSetTheValueInAppParams(LISTVIEW_VIEW);
        startToolbarCalendarView();
        setToolbarTitleLocal();
        loadCrmJournals(searchText, NO_SORT_PROCESS);
    }

    //----------CalendarView-PopUpWindow-----------------
    private static void startToolbarCalendarView() {
        if (!(appParams.getCalendarMode() == LISTVIEW_VIEW || appParams.getCalendarMode() == MONTH_VIEW ))
        {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCalendarPopup(toolbar, activityBasicActivityExtented);
                }
            });
        }else {
            toolbar.setOnClickListener(null);
        }
    }

    private static void showCalendarPopup(View anchorView, Activity activity) {
        // Inflate the layout for the calendar view dropdown
        @SuppressLint("InflateParams") View contentView = activity.getLayoutInflater().inflate(R.layout.calendar_dropdown, null);
        isCalendarPopUpViewPressed = true;
        setToolbarTitleLocal();
        // Get the CalendarView from the inflated layout
        CalendarView calendarView = contentView.findViewById(R.id.calendarView);
        // First, you need to create a Calendar instance and set it to the desired date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedDate.getYear());
        calendar.set(Calendar.MONTH, selectedDate.getMonthValue()-1); // Month is zero-based (0 for January, 1 for February, etc.)
        calendar.set(Calendar.DAY_OF_MONTH, selectedDate.getDayOfMonth());

// Then, convert the Calendar instance to milliseconds since the epoch
        long milliseconds = calendar.getTimeInMillis();
        calendarView.setDate(milliseconds);

        // Set the OnDateChangeListener for the CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                LocalDate calendarViewSelectedDate = LocalDate.of(year, month + 1, dayOfMonth); // Note: month is zero-based

                // Get the start of the day for the selected date
                LocalDateTime startOfDay = calendarViewSelectedDate.atStartOfDay();
                selectedDate = startOfDay.toLocalDate();

                switch (appParams.getCalendarMode()) {
                    case 1:
                        setWeek();
                        break;
                    case 2:
                        setDaily();
                        break;
                    default:
                        setMonth();
                        break;
                }
//                dismissCalendarPopup();
            }
        });

        // Create the popup window
        calendarPopup = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // Add a listener to wait for the layout to be drawn

        // Set the callback to adjust the layout when the popup window is dismissed
        calendarPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // Adjust the layout when the popup window is dismissed
                adjustLayoutForPopup(false, contentView);
                isCalendarPopUpViewPressed = false;
                setToolbarTitleLocal();
            }
        });

        // Show the popup window below the anchor view
        calendarPopup.showAsDropDown(anchorView);

        // Adjust the layout to make the FrameLayout be below the popup window
        adjustLayoutForPopup(true, contentView);
    }

    private void dismissCalendarPopup() {
        if (calendarPopup != null && calendarPopup.isShowing()) {
            calendarPopup.dismiss();
        }
    }

    private static void adjustLayoutForPopup(boolean popupShown, View contentView) {
        if (frameLayout != null) {
            if (popupShown) {
                ViewTreeObserver vto = contentView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Remove the listener to prevent it from being called multiple times
                        contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // Now the layout is drawn, get the height
                        int popupHeight = contentView.getHeight();

                        // Set layout parameters to move the FrameLayout below the popup window
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) swipeRefreshLayoutForFrame.getLayoutParams();

                        layoutParams.topMargin = contentView.getHeight(); // Adjust the top margin to match the height of the popup window
                        swipeRefreshLayoutForFrame.setLayoutParams(layoutParams);
                        // Use the height as needed
                    }
                });

            } else {
                // Reset layout parameters to their original values
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) swipeRefreshLayoutForFrame.getLayoutParams();
                layoutParams.topMargin = 0;
                swipeRefreshLayoutForFrame.setLayoutParams(layoutParams);
            }
        }
    }

    private static void viewPagerRegister() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (appParams.getCalendarMode()) {
                    case MONTH_VIEW: {
                        // Mode 0: Month view
                        YearMonth positionDate = monthsInOrder.get(position);
                        if (positionDate.getMonthValue() != LocalDate.now().getMonthValue()) {

                            CrmJournalCalendarViews.dateForViewPagerRegister = CrmJournalCalendarUtils.convertLocalDateTimeToDate(positionDate.atDay(1).atStartOfDay());
                        } else {
                            CrmJournalCalendarViews.dateForViewPagerRegister = CrmJournalCalendarUtils.convertLocalDateTimeToDate(LocalDate.now().atStartOfDay());
                        }
                        CrmJournalCalendarViews.dayClickedFromMonthForNewCrmJournal = null;
                        //Check if pageSelected month is equal with now month to set the dateForViewPagerRegister value to first of this month
                        // otherwise we set to dateForViewPagerRegister the value of now because pageSelected is in the same month
                        //then dayClickedFromMonthForNewCrmJournal is set to null
                        selectedDate = positionDate.atDay(1);
                        setToolbarTitleLocal();
                        break;
                    }
                    case WEEK_VIEW: {
                        // Mode 1: Week view
                        LocalDate positionDate = weeksStartingFromMonday.get(position);
                        ArrayList<LocalDate> weekCheckArrayList = new ArrayList<>();
                        for (int i = 0; i < 7; i++) {
                            weekCheckArrayList.add(positionDate.plusDays(i));
                        }
                        if (!weekCheckArrayList.contains(LocalDate.now())) {
                            CrmJournalCalendarViews.dateForViewPagerRegister = CrmJournalCalendarUtils.convertLocalDateTimeToDate(positionDate.atStartOfDay());
                        } else {
                            CrmJournalCalendarViews.dateForViewPagerRegister = CrmJournalCalendarUtils.convertLocalDateTimeToDate(LocalDate.now().atStartOfDay());
                        }
                        CrmJournalCalendarViews.dayClickedFromMonthForNewCrmJournal = null;

                        //We make an ArrayList that store the values of the pageSelected week to check is now date is inside this week
                        //Then if it is not we set to dateForViewPagerRegister the value of the first day of the week
                        //otherwise today date is the to dateForViewPagerRegister and dayClickedFromMonthForNewCrmJournal is set to null
                        selectedDate = positionDate.plusDays(3);
                        setToolbarTitleLocal();
                        break;
                    }
                    case DAY_VIEW: {
                        // Mode 2: Day view
                        LocalDate positionDate = daysStartingFromMonday.get(position);
                        selectedDate = positionDate;

                        CrmJournalCalendarViews.dayClickedFromMonthForNewCrmJournal = CrmJournalCalendarUtils.convertLocalDateTimeToDate(positionDate.atStartOfDay());
                        //set to dayClickedFromMonthForNewCrmJournal the value of the day of the pageSelected
                        CrmJournalCalendarViews.dateForViewPagerRegister = null;
                        setToolbarTitleLocal();
                        break;
                    }
                }

                tempSelectedDate = selectedDate;
                initialPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }
        });
    }

}