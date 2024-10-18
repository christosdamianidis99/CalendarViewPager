package com.example.calendarviewpager;


import static com.example.calendarviewpager.CalendarUtils.selectedDate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mycalendar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    //----------MENU_ITEMS--------------------
    private static final int CRM_CALENDAR_ACTIVITY_MENU_REFRESH = Menu.FIRST + 1;

    public static Activity MainActivity;

    //----------CALENDAR_MODES--------------------
    public static boolean CRM_SHOW_MODE = false;
    public static boolean CRM_EDIT_MODE = false;
    public static boolean CRM_NEW_MODE = false;

    //----------CALENDAR_VIEWS--------------------
    public static final int MONTH_VIEW = 0;
    public static final int WEEK_VIEW = 1;
    public static final int DAY_VIEW = 2;
    public static final int LISTVIEW_VIEW = 3;
    public static Integer currentSelectedId;


    //----------POPULATE_DAYS_FOR_VIEWS--------------------
    public static ArrayList<YearMonth> monthsInOrder = CalendarUtils.monthsInOrder();
    public static ArrayList<LocalDate> weeksStartingFromMonday = CalendarUtils.weeksStartingFromMonday();

    public static ArrayList<LocalDate> daysStartingFromMonday = CalendarUtils.daysInAllMonths();
    //----------WIDGETS--------------------
    androidx.appcompat.widget.Toolbar toolbar;
    ListView showListViewRecyclerView;
    FrameLayout frameLayout;

    private ViewPager2 viewPager;

        Button monthViewBtn,weekViewBtn,dayViewBtn,listViewBtn;
    public static int initialPosition;

    public static CalendarEvent selectedCalendarEvent;
    public static int position;
    public static boolean isRefreshPressed = false;
    public static boolean isCalendarPopUpViewPressed = false;

    public static int modeCalendar;

    //-----------------CRM_JOURNALS------------------

    public static ArrayList<CalendarEvent> crmJournalsData = new ArrayList<>();
    public static ArrayList<CalendarEvent> TempEvents = new ArrayList<>();

    //-----------------STATE_TO_OPEN_LISTVIEW------------------
    public static boolean listViewShownFromDayView = false;
    public static boolean listViewShownFromMenu = false;

    private PopupWindow calendarPopup;

    FloatingActionButton primaryFab;
    public static boolean monthViewCellClicked;
    public static boolean weekViewCellClicked;

    public static Activity myActivity;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        setCRMjournal();
        setToolbar();
        setToolbarTitle();
        setFloatingActionButtons();
        myActivity = this;
        db = new DatabaseHelper(this);
        crmJournalsData = db.getAllCalendarEvents();
        if (Objects.equals(modeCalendar, MONTH_VIEW) && !monthViewCellClicked) {
            setMonth();
        } else if (Objects.equals(modeCalendar, WEEK_VIEW) && !weekViewCellClicked) {
            setWeek();
        } else if (Objects.equals(modeCalendar, DAY_VIEW)) {
            setDaily();
        } else if (Objects.equals(modeCalendar, LISTVIEW_VIEW)) {
            viewListViewItems();
        } else if (Objects.equals(modeCalendar, MONTH_VIEW) && monthViewCellClicked) {
            setDaily();
        } else if (Objects.equals(modeCalendar, WEEK_VIEW) && weekViewCellClicked) {
            setDaily();

        }

        monthViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthViewCellClicked = false;
                weekViewCellClicked = false;
                setMonth();
            }
        });
weekViewBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        monthViewCellClicked = false;
        weekViewCellClicked = false;
        setWeek();
    }
});
dayViewBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        monthViewCellClicked = false;
        weekViewCellClicked = false;
        setDaily();
    }
});
listViewBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        monthViewCellClicked = false;
        weekViewCellClicked = false;
        viewListViewItems();
    }
});
        viewPagerRegister();


    }

    //----------CalendarView-PopUpWindow-----------------
    private void startToolbarCalendarView() {
        if (!(modeCalendar == LISTVIEW_VIEW || modeCalendar == MONTH_VIEW)) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCalendarPopup(toolbar);
                }
            });
        } else {
            toolbar.setOnClickListener(null);
        }
    }

    private void showCalendarPopup(View anchorView) {
        // Inflate the layout for the calendar view dropdown
        View contentView = getLayoutInflater().inflate(R.layout.calendar_dropdown, null);
        isCalendarPopUpViewPressed = true;
        setToolbarTitle();
        // Get the CalendarView from the inflated layout
        CalendarView calendarView = contentView.findViewById(R.id.calendarView);


        // Set the OnDateChangeListener for the CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                LocalDate calendarViewSelectedDate = LocalDate.of(year, month + 1, dayOfMonth); // Note: month is zero-based

                // Get the start of the day for the selected date
                LocalDateTime startOfDay = calendarViewSelectedDate.atStartOfDay();
                selectedDate = startOfDay.toLocalDate();

                switch (modeCalendar) {
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
                setToolbarTitle();
            }
        });

        // Show the popup window below the anchor view
        calendarPopup.showAsDropDown(anchorView);

        // Adjust the layout to make the FrameLayout be below the popup window
        adjustLayoutForPopup(true, contentView);


    }

    // Method to check if there are events for a given date
    private boolean hasEventsForDate(LocalDate date) {
        // Check if there are events associated with the given date
        for (CalendarEvent journalData : crmJournalsData) {
            LocalDate eventDate = CalendarUtils.convertStringToLocalDateTime(journalData.getStartDate()).toLocalDate();
            if (eventDate.equals(date)) {
                return true; // Event found for the given date
            }
        }
        return false; // No event found for the given date
    }


    private void adjustLayoutForPopup(boolean popupShown, View contentView) {
//        FrameLayout
//                frameLayout = findViewById(R.id.frameLayout);
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
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();

                        layoutParams.topMargin = contentView.getHeight(); // Adjust the top margin to match the height of the popup window
                        frameLayout.setLayoutParams(layoutParams);
                        // Use the height as needed
                    }
                });

            } else {
                // Reset layout parameters to their original values
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
                layoutParams.topMargin = 0;
                frameLayout.setLayoutParams(layoutParams);
            }
        }
    }

    //Setting the title of supportActionBar
    private void setToolbarTitle() {
        YearMonth nowYearMonth = YearMonth.from(selectedDate);

        String actionTitle = CalendarUtils.monthYearFromDateViewPager(nowYearMonth);
        if (getSupportActionBar() != null) {

            if (isCalendarPopUpViewPressed) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(actionTitle + " ▲");
            } else {
                Objects.requireNonNull(getSupportActionBar()).setTitle(actionTitle + " ▼");
            }

            if (modeCalendar == MONTH_VIEW) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(actionTitle);
            }
            if (modeCalendar == LISTVIEW_VIEW) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Calendar");
            }
        }
    }

    public void initWidgets() {
        monthViewBtn=findViewById(R.id.month_button);
        weekViewBtn=findViewById(R.id.week_button);
        dayViewBtn=findViewById(R.id.day_button);
        listViewBtn=findViewById(R.id.list_button);

        viewPager = findViewById(R.id.viewPager);
        showListViewRecyclerView = findViewById(R.id.showListViewRecyclerView);
        frameLayout = findViewById(R.id.mainCalendarViewsRelativeLayout);
        primaryFab = findViewById(R.id.activity_basic_primary_fab);
    }


    public void setToolbar() {

        toolbar = findViewById(R.id.crm_calendar_toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.pal_blue));
        // change the color of back button to white
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        // change the color of overflow three dot button to white
        Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);


    }


    public void viewPagerRegister() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (modeCalendar) {
                    case MONTH_VIEW: {
                        // Mode 0: Month view
                        YearMonth positionDate = monthsInOrder.get(position);


//                        updateActionBarTitle(CalendarUtils.monthYearFromDateViewPager(positionDate));
                        selectedDate = positionDate.atDay(1);
                        setToolbarTitle();
                        break;
                    }
                    case WEEK_VIEW: {
                        // Mode 1: Week view
                        LocalDate positionDate = weeksStartingFromMonday.get(position);
//                        updateActionBarTitle(CalendarUtils.localDateFromDateViewPager(positionDate));
                        selectedDate = positionDate;
                        setToolbarTitle();

                        break;
                    }
                    case DAY_VIEW: {
                        // Mode 2: Day view
                        LocalDate positionDate = daysStartingFromMonday.get(position);
//                        updateActionBarTitle(CalendarUtils.localDateFromDateViewPager(positionDate));
                        selectedDate = positionDate;
                        setToolbarTitle();
                        break;
                    }
                }

                initialPosition = position;


            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }
        });


    }

    private void setViewPagerTransformer() {
        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                final float normalizedPosition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedPosition / 2 + 0.5f);
                page.setScaleY(normalizedPosition / 2 + 0.5f);
                page.setAlpha(normalizedPosition);
            }
        });

//-------------------Different--------------
        //Effect page per page
//        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
//            private static final float MIN_SCALE = 0.85f;
//            private static final float MIN_ALPHA = 0.5f;
//            @Override
//            public void transformPage(@NonNull View page, float position) {
//                if (position < -1 || position > 1) {
//                    page.setAlpha(0f); // Hide the page
//                } else {
//                    // Rotate the page
//                    page.setRotationY(position * -30);
//
//                    // Scale the page
//                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position / 2));
//                    page.setScaleX(scaleFactor);
//                    page.setScaleY(scaleFactor);
//
//                    // Adjust alpha for a fading effect
//                    float alpha = Math.max(MIN_ALPHA, 1 - Math.abs(position / 2));
//                    page.setAlpha(alpha);
//                }
//            }
//        });
    }

    private void updateActionBarTitle(String actionTitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionTitle);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, CRM_CALENDAR_ACTIVITY_MENU_REFRESH, Menu.NONE, "Today")
                .setIcon(R.drawable.calendar_today)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showExitConfirmationDialog();
                return true;
            case CRM_CALENDAR_ACTIVITY_MENU_REFRESH:
                returnToCurrectDateMenuButton();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finish(); // Close the app
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss(); // Do nothing
                })
                .show();
    }

    //---For the Button in optionMenu that bring the calendar to current Today Date
    private void returnToCurrectDateMenuButton() {
        isRefreshPressed = true;
        selectedDate = LocalDate.now();
        switch (modeCalendar) {
            case MONTH_VIEW:
            case LISTVIEW_VIEW:
                setMonth();
                break;
            case WEEK_VIEW:
                setWeek();
                break;
            case DAY_VIEW:
                setDaily();
                break;
        }

    }

    //------------Set Views----------------------------
    private void setMonth() {


// Get the LayoutParams of each floating action button
        CoordinatorLayout.LayoutParams primaryLayoutParams = (CoordinatorLayout.LayoutParams) primaryFab.getLayoutParams();

// Set the anchor view for each floating action button to the ViewPager
        primaryLayoutParams.setAnchorId(viewPager.getId());

// Update the LayoutParams for each floating action button
        primaryFab.setLayoutParams(primaryLayoutParams);

        modeCalendar = MONTH_VIEW;
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        initVisibilitiesCalendarViews();
        startToolbarCalendarView();
        YearMonth nowYearMonth = YearMonth.from(selectedDate);
        initialPosition = CalendarUtils.monthsInOrder().indexOf(nowYearMonth);
        CalendarViewPagerAdapter adapter = new CalendarViewPagerAdapter(crmJournalsData);
        setViewPager(adapter, initialPosition);
    }

    private void setWeek() {
// Get the LayoutParams of each floating action button
        CoordinatorLayout.LayoutParams primaryLayoutParams = (CoordinatorLayout.LayoutParams) primaryFab.getLayoutParams();

// Set the anchor view for each floating action button to the ViewPager
        primaryLayoutParams.setAnchorId(viewPager.getId());

// Update the LayoutParams for each floating action button
        primaryFab.setLayoutParams(primaryLayoutParams);
        modeCalendar = WEEK_VIEW;
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        initVisibilitiesCalendarViews();
        startToolbarCalendarView();

        for (int i = 0; i < weeksStartingFromMonday.size(); i++) {
            LocalDate weekStart = weeksStartingFromMonday.get(i);
            LocalDate weekEnd = weekStart.plusDays(6); // Assuming Sunday to Saturday
            if (!selectedDate.isBefore(weekStart) && !selectedDate.isAfter(weekEnd)) {
                initialPosition = i;
                break; // We found the week, so we can exit the loop
            }
        }


        CalendarViewPagerAdapter adapter = new CalendarViewPagerAdapter(crmJournalsData);
        setViewPager(adapter, initialPosition);
    }

    public void setDaily() {
// Get the LayoutParams of each floating action button
        CoordinatorLayout.LayoutParams primaryLayoutParams = (CoordinatorLayout.LayoutParams) primaryFab.getLayoutParams();

// Set the anchor view for each floating action button to the ViewPager
        primaryLayoutParams.setAnchorId(viewPager.getId());

// Update the LayoutParams for each floating action button
        primaryFab.setLayoutParams(primaryLayoutParams);
        modeCalendar = DAY_VIEW;
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        initVisibilitiesCalendarViews();
        startToolbarCalendarView();

        initialPosition = daysStartingFromMonday.indexOf(selectedDate);

        CalendarViewPagerAdapter adapter = new CalendarViewPagerAdapter(crmJournalsData);
        setViewPager(adapter, initialPosition);


    }

    //Hides or show correct layouts for days of months
    private void initVisibilitiesCalendarViews() {
        if (Objects.equals(modeCalendar, WEEK_VIEW) || Objects.equals(modeCalendar, DAY_VIEW)) {
            showListViewRecyclerView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showListViewRecyclerView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
        }

    }

    //Set viewpager adapter, offscreenPageLimit=How many viewpager pages stay in memory
    private void setViewPager(CalendarViewPagerAdapter adapter, int initialPosition) {
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);


        setCurrentItemCurrentDayAction(initialPosition);

    }

    //Set and scroll to current place of the calendar based the selected date.
    private void setCurrentItemCurrentDayAction(int initialPosition) {
        if (isRefreshPressed || isCalendarPopUpViewPressed) {
            viewPager.setCurrentItem(initialPosition, true);
            isRefreshPressed = false;
        } else {
            viewPager.setCurrentItem(initialPosition, false);
        }

    }
//Ask to the future if back of day,week and listview return first to month view and then to menu

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (monthViewCellClicked) {

            setMonth();
            monthViewCellClicked = false;

        } else if (weekViewCellClicked) {
            setWeek();
            weekViewCellClicked = false;
        } else {
            navigateToMenuActivity();
        }
//        if (modeCalendar == WEEK_VIEW || modeCalendar == DAY_VIEW || modeCalendar == LISTVIEW_VIEW) {
//            setMonth();
//        } else {
//            navigateToMenuActivity();
//        }
    }

    private void navigateToMenuActivity() {
        startActivity(getIntent());
    }

    //Setter of listview
    private void viewListViewItems() {
        frameLayout.setVisibility(View.GONE);
        showListViewRecyclerView.setVisibility(View.VISIBLE);


// Get the LayoutParams of each floating action button
        CoordinatorLayout.LayoutParams primaryLayoutParams = (CoordinatorLayout.LayoutParams) primaryFab.getLayoutParams();

// Set the anchor view for each floating action button to the ListView
        primaryLayoutParams.setAnchorId(showListViewRecyclerView.getId());

// Update the LayoutParams for each floating action button
        primaryFab.setLayoutParams(primaryLayoutParams);

        modeCalendar = LISTVIEW_VIEW;
        startToolbarCalendarView();
        setToolbarTitle();


        if (!crmJournalsData.isEmpty()) {
            CrmJournalActionItemAdapterListView listView = new CrmJournalActionItemAdapterListView(getApplicationContext(), crmJournalsData, true);
            showListViewRecyclerView.setAdapter(listView);
        }

        //TempJournal is the journal that is selected from the listview, it is saved so when the user choose an event from dayview to stay on screen as selection
        if (!(CalendarViews.tempJournal == null)) {
            for (int i = 0; i < crmJournalsData.size(); i++) {
                if (Objects.equals(crmJournalsData.get(i).getId(), CalendarViews.tempJournal.getId())) {
                    showListViewRecyclerView.setSelection(i);
                }
            }
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void setFloatingActionButtons() {


        primaryFab.setEnabled(true);
        primaryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CRM_NEW_MODE = true;
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);
            }
        });
    }

    //Setter CRMJournal
    public static void setCRMjournal() {
        CalendarUtils.setIfAnEventHasTemps(crmJournalsData);
        TempEvents = checkForTemps();
    }

    public static ArrayList<CalendarEvent> checkForTemps() {
        return CalendarUtils.tempEvents(crmJournalsData);
    }
}