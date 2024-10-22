package com.example.calendarviewpager;
import static com.example.calendarviewpager.CalendarUtils.selectedDate;
import static com.example.calendarviewpager.MainActivity.DAY_VIEW;
import static com.example.calendarviewpager.MainActivity.MONTH_VIEW;
import static com.example.calendarviewpager.MainActivity.WEEK_VIEW;
import static com.example.calendarviewpager.MainActivity.modeCalendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mycalendar.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;




public class CalendarViews extends View {


    private static final int NUM_DAYS_IN_WEEK = 7;
    private static final int NUM_DAYS_MONTH_HORIZONTAL = 6;

    private Paint currentDayOfMonthColor;
    private Paint weekAndDailyEvents;
    private Paint daysOfMonthPaint;
    private Paint dayPaint;
    private Paint eventPaint;
    private Paint hourBackgroundPaint;
    private Paint textPaint;

    private Paint linePaint;
    private Paint realTimePaint;
    private Paint threeDotsPaint;
    private Paint blackOutlinePaint;

    private static final int NUM_HOURS_IN_DAY = 24;
    private static final int HOURS_IN_DAY = 24;

    public LocalDateTime dayNow;
    public static Date dayClickedFromMonthForNewEvent; //This variable is to store the Date value of the user clicked day from month or from week.

    public static Date dateForViewPagerRegister;// This variable is to store the Date value of the pageSelected the user is based every view.

    private ArrayList<LocalDateTime> weekNow;
    private ArrayList<LocalDateTime> monthNow;

    private static final String LINE_COLOR = "#dadada";

    ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();
    ArrayList<EventCoordinates> touchEventsForMonthView = new ArrayList<>();
    ArrayList<EventCoordinates> touchEventsForWeekView = new ArrayList<>();
    ArrayList<EventCoordinates> touchEventsDayView = new ArrayList<>();

    public static CalendarEvent tempCalendarEvent;
    public static boolean weAreInTablet;
    public static boolean monthCounterOneInDayCell = false;


    public CalendarViews(Context context, AttributeSet attrs) {
        super(context, attrs);

        CalendarFormats.getScreenInches(MainActivity.myActivity);
        weAreInTablet = CalendarFormats.screenYpotenuseInDp >= 1120;
    }


    private void initialize() {
        // Initialize paints common to all modes
        initializePaints();


        // Common properties for all modes
        linePaint.setColor(Color.parseColor(LINE_COLOR));
        linePaint.setStrokeWidth(1);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);

        blackOutlinePaint.setColor(Color.BLACK);
        blackOutlinePaint.setStyle(Paint.Style.STROKE); // Set the style to STROKE
        blackOutlinePaint.setDither(true);
        blackOutlinePaint.setAntiAlias(true);
        blackOutlinePaint.setStrokeWidth(2); // Set the stroke width (adjust as needed)
        // Additional properties based on modeCalendar
        switch (modeCalendar) {
            case MONTH_VIEW:
                initializePaintsForMonth();
                break;
            case WEEK_VIEW:
                initializePaintsForWeek();
                break;
            case DAY_VIEW:
                initializePaintsForDay();
                break;
            default:
                // Handle unknown mode
        }
    }

    private void initializePaints() {

        //the lines between the days
        linePaint = new Paint();

        //events text
        textPaint = new Paint();

        weekAndDailyEvents = new Paint();
        currentDayOfMonthColor = new Paint();
        dayPaint = new Paint();
        eventPaint = new Paint();
        hourBackgroundPaint = new Paint();
        realTimePaint = new Paint();
        daysOfMonthPaint = new Paint();
        threeDotsPaint = new Paint();
        blackOutlinePaint = new Paint();

    }

    public static Typeface getTypeFaceFont(Context context){

            return Typeface.DEFAULT;

    }

    private void initializePaintsForMonth() {
        Typeface appTypeface = getTypeFaceFont(getContext());

        daysOfMonthPaint.setColor(getPrimaryColor());
        daysOfMonthPaint.setTextSize(getResources().getDimension(R.dimen.text_size_title) - dpToPx(5));
        daysOfMonthPaint.setAntiAlias(true); // Enable anti-aliasing
        if (appTypeface != null) {
            daysOfMonthPaint.setTypeface(appTypeface);
        }
        currentDayOfMonthColor.setColor(Color.WHITE);
        currentDayOfMonthColor.setTextSize(getResources().getDimension(R.dimen.text_size_title)  - dpToPx(5));
        currentDayOfMonthColor.setAntiAlias(true); // Enable anti-aliasing
        currentDayOfMonthColor.setDither(true);
        if (appTypeface != null) {
            currentDayOfMonthColor.setTypeface(appTypeface);
        }

        setTextPaint(getResources().getColor(R.color.pal_blue), getResources().getDimension(R.dimen.text_size_title)  - dpToPx((int) 7f),appTypeface);


        setweekAndDailyEventsPaint(getPrimaryColor(),appTypeface);
        setDayPaint(getPrimaryColor(),appTypeface);
        setEventPaint(getPrimaryColor(),appTypeface);
        setHourBackgroundPaint(Color.WHITE);

        setRealTimePaint();

        setThreeDotsPaint(getPrimaryColor());
        setThreeDotsPaint(Color.BLACK);

    }

    private void initializePaintsForWeek() {
        Typeface appTypeface = getTypeFaceFont(getContext());

        if (weAreInTablet) {
            setTextPaint(Color.BLACK, getResources().getDimension(R.dimen.text_size_title) ,appTypeface);
        } else {
            setTextPaint(Color.BLACK, getResources().getDimension(R.dimen.text_size_title)  - dpToPx((int) 5f),appTypeface);
        }
        setweekAndDailyEventsPaint(getPrimaryColor(),appTypeface);
        setDayPaint(Color.WHITE,appTypeface);
        setEventPaint(getPrimaryColor(),appTypeface);
        setHourBackgroundPaint(Color.WHITE);
        setRealTimePaint();
    }

    private void initializePaintsForDay() {
        Typeface appTypeface = getTypeFaceFont(getContext());

        setTextPaint(Color.WHITE, getResources().getDimension(R.dimen.text_size_title) ,appTypeface);
        setweekAndDailyEventsPaint(getPrimaryColor(),appTypeface);
        setDayPaint(Color.WHITE,appTypeface);
        setEventPaint(getPrimaryColor(),appTypeface);
        setHourBackgroundPaint(Color.WHITE);
        setRealTimePaint();

    }

    private void setTextPaint(int color, float textSize, Typeface appTypeface) {


        textPaint.setColor(color);
        textPaint.setDither(true);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        if (appTypeface != null) {
            textPaint.setTypeface(appTypeface);
        }

    }

    private void setweekAndDailyEventsPaint(int color, Typeface appTypeface) {

        weekAndDailyEvents.setColor(color);
        weekAndDailyEvents.setAntiAlias(true); // Enable anti-aliasing
        weekAndDailyEvents.setDither(true);
        if (appTypeface != null) {
            weekAndDailyEvents.setTypeface(appTypeface);
        }
    }

    private void setDayPaint(int color, Typeface appTypeface) {

        dayPaint.setColor(color);
        dayPaint.setTextSize((float) 40.0);
        dayPaint.setAntiAlias(true); // Enable anti-aliasing
        dayPaint.setDither(true);
        if (appTypeface != null) {
            dayPaint.setTypeface(appTypeface);
        }
    }

    private void setEventPaint(int color, Typeface appTypeface) {

        eventPaint.setColor(color);
        eventPaint.setAntiAlias(true); // Enable anti-aliasing
        eventPaint.setDither(true);
        if (appTypeface != null) {
            eventPaint.setTypeface(appTypeface);
        }

    }

    private void setHourBackgroundPaint(int color) {
        hourBackgroundPaint.setColor(color);
        hourBackgroundPaint.setAntiAlias(true); // Enable anti-aliasing
        hourBackgroundPaint.setDither(true);

    }

    private void setRealTimePaint() {
        realTimePaint.setColor(Color.BLACK);
        realTimePaint.setAntiAlias(true); // Enable anti-aliasing
        realTimePaint.setDither(true);
    }

    private void setThreeDotsPaint(int color) {
        threeDotsPaint.setColor(color);
        threeDotsPaint.setAntiAlias(true); // Enable anti-aliasing
        if (weAreInTablet) {
            threeDotsPaint.setTextSize(40f);
        } else {
            threeDotsPaint.setTextSize(80f);
        }

        threeDotsPaint.setDither(true);
    }

    private int getPrimaryColor() {
        return getResources().getColor(R.color.pal_blue);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int weekDayHeight;
        int monthHeight;
        int dayHeight;
        if (weAreInTablet) {
            monthHeight = dpToPx(1070);    // Example height for tablets in month view
            weekDayHeight = dpToPx(2000);  // Example height for tablets in week view
            dayHeight = dpToPx(2000);

        } else {
            monthHeight = dpToPx(650);    // Example height for phones in month view
            weekDayHeight = dpToPx(1500);  // Example height for phones in week view
            dayHeight = dpToPx(1500);
        }
        if (modeCalendar == MONTH_VIEW) {
            setMeasuredDimension(widthMeasureSpec, monthHeight);
        } else if (modeCalendar == WEEK_VIEW) {
            setMeasuredDimension(widthMeasureSpec, weekDayHeight);
        } else {
            setMeasuredDimension(widthMeasureSpec, dayHeight);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    //SETTERS
    public void setWeekForViewPager(ArrayList<CalendarEvent> events, ArrayList<LocalDateTime> week) {
        this.calendarEvents = events;
        this.weekNow = week;
        initialize();
        invalidate();
        requestLayout();
    }

    public void setMonthForViewPager(ArrayList<LocalDateTime> months, ArrayList<CalendarEvent> events) {
        this.calendarEvents = events;
        this.monthNow = months;
        initialize();
        invalidate();
        requestLayout();
    }

    public void setDayFromViewPagerAdapter(LocalDateTime day, ArrayList<CalendarEvent> events) {
        this.calendarEvents = events;
        this.dayNow = day;
        initialize();
        invalidate();
        requestLayout();
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initialize();
        if (modeCalendar == MONTH_VIEW) {
            drawMonthHourBackground(canvas);
            if (weAreInTablet) {
                drawMonthBoxesTablet(canvas);
            } else {
                drawMonthBoxes(canvas);
            }
        } else if (modeCalendar == WEEK_VIEW) {
            // Draw the hour background
            drawWeekHourBackground(canvas);
            drawWeekHoursBoxes(canvas);
        } else {
            // Draw the hour background
            drawDailyHourBackground(canvas);
            drawDailyHourBoxes(canvas);
        }
    }

    //----------------------------MONTH--------------------------------//
    private void drawMonthHourBackground(Canvas canvas) {
        for (int i = 0; i < NUM_DAYS_IN_WEEK; i++) {
            for (int j = 0; j < NUM_DAYS_MONTH_HORIZONTAL; j++) {
                float startX = i * getWidth() / NUM_DAYS_IN_WEEK;
                float endX = (i + 1) * getWidth() / NUM_DAYS_IN_WEEK + dpToPx((int) 200f);
                float endY = (j + 1) * getHeight() / NUM_DAYS_MONTH_HORIZONTAL + dpToPx((int) 150f);

                canvas.drawRect(startX, 0, endX, endY, hourBackgroundPaint);
                if (i == 5 | i == 6) {
                    hourBackgroundPaint.setColor(getResources().getColor(R.color.color_LightGray));
                    canvas.drawRect(startX, 0, endX, endY, hourBackgroundPaint);
                }
            }
        }
    }

    private void drawMonthBoxes(Canvas canvas) {

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        LocalDateTime firstDayOfMonth = monthNow.get(0);
        // Loop through each day in the month
        for (int i = 0; i < 42; i++) { // Assuming 42 slots per month
            float y = i / 7 * (viewHeight / 6);
            float x = (i % 7) * (viewWidth / 7);
            LocalDateTime currentTime = firstDayOfMonth.plusDays(i);
            int plusWidth;

            if (currentTime.getDayOfMonth() < 10) {
                plusWidth = dpToPx(25);
            } else {
                plusWidth = dpToPx(20);
            }
            if (currentTime.toLocalDate().equals(LocalDateTime.now().toLocalDate())) {

                if (LocalDateTime.now().getDayOfMonth() > 9) {
                    drawCircleCurrentDate(canvas, x, y + dpToPx((int) 6f));
                } else {
                    drawCircleCurrentDate(canvas, x, y + dpToPx((int) 6f));
                }

                canvas.drawText(String.valueOf(currentTime.getDayOfMonth()), x + plusWidth, y + dpToPx((int) 12f), currentDayOfMonthColor);
            } else {
                canvas.drawText(String.valueOf(currentTime.getDayOfMonth()), x + plusWidth, y + dpToPx((int) 12f), daysOfMonthPaint);
            }


            canvas.drawLine(0, y, viewWidth, y, linePaint);

            // Draw the day number


            // Draw events for the current day
            drawMonthEvents(canvas, currentTime, x, x + viewWidth / 7, y);
        }

        // Draw vertical lines for each day
        for (int i = 0; i < 7; i++) {
            float x = i * (viewWidth / 7);
            canvas.drawLine(x, 0, x, viewHeight, linePaint);
        }
    }

    private void drawMonthBoxesTablet(Canvas canvas) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        LocalDateTime firstDayOfMonth = monthNow.get(0);

        // Loop through each day in the month
        for (int i = 0; i < 42; i++) { // Assuming 42 slots per month
            float y = i / 7 * (viewHeight / 6);
            float x = (i % 7) * (viewWidth / 7);
            LocalDateTime currentTime = firstDayOfMonth.plusDays(i);

            if (currentTime.toLocalDate().equals(LocalDateTime.now().toLocalDate())) {

                if (LocalDateTime.now().getDayOfMonth() > 9) {
                    drawCircleCurrentDate(canvas, x, y);
                } else {
                    drawCircleCurrentDate(canvas, x - dpToPx(3), y);
                }

                canvas.drawText(String.valueOf(currentTime.getDayOfMonth()), x + dpToPx(52), y + dpToPx((int) 15f), currentDayOfMonthColor);
            } else {
                canvas.drawText(String.valueOf(currentTime.getDayOfMonth()), x + dpToPx(52), y + dpToPx((int) 15f), daysOfMonthPaint);
            }

            canvas.drawLine(0, y, viewWidth, y, linePaint);

            // Draw events for the current day
            drawMonthEvents(canvas, currentTime, x, x + viewWidth / 7, y);
        }

        // Draw vertical lines for each day
        for (int i = 0; i < 7; i++) {
            float x = i * (viewWidth / 7);
            canvas.drawLine(x, 0, x, viewHeight, linePaint);
        }
    }

    private void drawMonthEvents(Canvas canvas, LocalDateTime dayTime, float startX, float endX, float startY) {
        if (!calendarEvents.isEmpty()) {
            // Use a fixed height for each event rectangle
            float eventHeight;
            if (weAreInTablet) {
                eventHeight = dpToPx((int) 28f);
            } else {
                eventHeight = dpToPx((int) 16f);
            }

            float eventSpacing = dpToPx((int) 1f); // Add spacing between events

            // Calculate the coordinates for the first event
            float eventStartY;
            if (weAreInTablet) {
                eventStartY = startY + dpToPx((int) 22f); // Adjusted value to move events a little more below the day of the month
            } else {
                eventStartY = startY + dpToPx((int) 16f); // Adjusted value to move events a little more below the day of the month
            }

            float eventEndY = eventStartY + eventHeight;

            // Maximum number of events to display
            int maxEvents;

            maxEvents = 5;
            int eventsDrawn = 0;
            float lastEventEndY = eventStartY; // To keep track of the last event's end Y coordinate
            if (!MainActivity.TempEvents.isEmpty()) {

                // Loop through tempEvents to find events on the current day
                for (CalendarEvent calendarEvent : MainActivity.TempEvents) {
                    LocalDateTime calendarEventStartDate = CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate());
                    String eventTitle = calendarEvent.getDescr();
                    if (eventTitle.length() > 9) {
                        eventTitle = eventTitle.substring(0, 9);
                    }
                    if (dayTime.toLocalDate().equals(calendarEventStartDate.toLocalDate())) {

                        if (eventsDrawn < maxEvents) {
                            // Draw a rectangle for the event
                            RectF rectF = new RectF(startX, eventStartY, endX, eventEndY);
                            float cornerRadius = dpToPx((int) 10f);
                            eventPaint.setColor(getResources().getColor(R.color.pal_blue));
                            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, eventPaint);
                            EventCoordinates eventCoordinates = new EventCoordinates(startX, endX, eventStartY, eventEndY, calendarEvent);
                            touchEventsForMonthView.add(eventCoordinates);


                                textPaint.setColor(Color.WHITE);



                            // Draw the event title centered within the rectangle
                            float textX = startX + dpToPx(5);
                            float textY;
                            if (weAreInTablet) {
                                textY = (eventStartY + (eventHeight + textPaint.getTextSize()) / 2) - dpToPx(3); // Adjusted text position

                            } else {
                                textY = (eventStartY + (eventHeight + textPaint.getTextSize()) / 2) - dpToPx(3); // Adjusted text position

                            }
                            canvas.drawText(eventTitle, textX, textY, textPaint);

                            // Update the starting Y coordinate for the next event
                            eventStartY = eventEndY + eventSpacing; // Add spacing between events
                            eventEndY = eventStartY + eventHeight;
                            lastEventEndY = eventEndY; // Update the last event's end Y coordinate


                        } else {
                            break; // Break the loop as we've drawn the ellipsis
                        }

                        eventsDrawn++;
                    }
                }
            }

            // Loop through events to find events on the current day
            for (CalendarEvent calendarEvent : calendarEvents) {
                LocalDateTime calendarEventStartDate = CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate());
                String eventTitle = calendarEvent.getDescr();
                if (eventTitle.length() > 9) {
                    eventTitle = eventTitle.substring(0, 9);
                }
                if (dayTime.toLocalDate().equals(calendarEventStartDate.toLocalDate()) && !calendarEvent.isHasTemp()) {

                    if (eventsDrawn < maxEvents) {
                        // Draw a rectangle for the event
                        RectF rectF = new RectF(startX, eventStartY, endX, eventEndY);
                        float cornerRadius = dpToPx((int) 10f);
                        eventPaint.setColor(getResources().getColor(R.color.pal_blue));
                        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, eventPaint);
                        EventCoordinates eventCoordinates = new EventCoordinates(startX, endX, eventStartY, eventEndY, calendarEvent);
                        touchEventsForMonthView.add(eventCoordinates);



                            textPaint.setColor(Color.WHITE);


                        // Draw the event title centered within the rectangle
                        float textX = startX + dpToPx(5);
                        float textY;
                        if (weAreInTablet) {
                            textY = (eventStartY + (eventHeight + textPaint.getTextSize()) / 2) - dpToPx(3); // Adjusted text position

                        } else {
                            textY = (eventStartY + (eventHeight + textPaint.getTextSize()) / 2) - dpToPx(3); // Adjusted text position

                        }
                        canvas.drawText(eventTitle, textX, textY, textPaint);

                        // Update the starting Y coordinate for the next event
                        eventStartY = eventEndY + eventSpacing; // Add spacing between events
                        eventEndY = eventStartY + eventHeight;
                        lastEventEndY = eventEndY; // Update the last event's end Y coordinate


                    } else {
                        break; // Break the loop as we've drawn the ellipsis
                    }

                    eventsDrawn++;
                }
            }

            int eventsInDayCounter = 0;
            for (CalendarEvent calendarEvent : calendarEvents) {
                LocalDateTime calendarEventStartDate = CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate());

                if (calendarEventStartDate.toLocalDate().equals(dayTime.toLocalDate())) {
                    eventsInDayCounter++;
                }

            }

            for (CalendarEvent calendarEvent : MainActivity.TempEvents) {
                LocalDateTime calendarEventStartDate = CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate());

                if (calendarEventStartDate.toLocalDate().equals(dayTime.toLocalDate())) {
                    eventsInDayCounter++;
                }

            }
            // If there are more events than maxEvents, draw the ellipsis (...) below the last event
            if (eventsInDayCounter > maxEvents) {
                float ellipsisX;
                if (weAreInTablet) {
                    ellipsisX = startX + (endX - startX) / 4 + dpToPx(15); // Move slightly to the left to center horizontally

                } else {
                    ellipsisX = startX + (endX - startX) / 4; // Move slightly to the left to center horizontally

                }
                float ellipsisY;
                if (weAreInTablet) {
                    ellipsisY = (lastEventEndY - eventHeight / 2 - dpToPx(7)); // Directly below the last event
                } else {
                    ellipsisY = (lastEventEndY - eventHeight / 2) - dpToPx(5); // Directly below the last event
                }

                canvas.drawText("...", ellipsisX, ellipsisY, threeDotsPaint);
            }
        }
    }

    private void drawCircleCurrentDate(Canvas canvas, float x, float y) {

        // Retrieve the system font scale
        float fontScale = getSystemFontScale();

        // Define base radii for tablet and non-tablet
        float baseTabletRadius = 6f;
        float baseNonTabletRadius = 3f;

        // Adjust the base radii by the font scale
        float adjustedTabletRadius = dpToPx((int) baseTabletRadius) * fontScale;
        float adjustedNonTabletRadius = dpToPx((int) baseNonTabletRadius) * fontScale;

        if (weAreInTablet) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                // Here we are in Android version 8 or lower
                canvas.drawCircle(x + dpToPx(60), y + dpToPx(11), adjustedTabletRadius + 5, daysOfMonthPaint);
            } else {
                canvas.drawCircle(x + dpToPx(60), y + dpToPx(11), adjustedTabletRadius, daysOfMonthPaint);
            }

        } else {
            if (LocalDateTime.now().getDayOfMonth()>9)
            {
                canvas.drawCircle(x + dpToPx(26), y + dpToPx(3), adjustedNonTabletRadius, daysOfMonthPaint);

            }else
            {
                canvas.drawCircle(x + dpToPx(28), y + dpToPx(3), adjustedNonTabletRadius, daysOfMonthPaint);

            }
        }
    }

    private float getSystemFontScale() {
        return getResources().getDisplayMetrics().scaledDensity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (Objects.equals(MainActivity.modeCalendar, MONTH_VIEW)) {
            float x = event.getX();
            float y = event.getY();

            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Check if the touch event occurs within the bounds of any day's block
                    if (weAreInTablet) {
                        selectedDate = LocalDate.from(getSelectedDateMonth(x, y));
                        monthCounterOneInDayCell = false;
                        int eventCounter = 0;
                        for (CalendarEvent calendarEvent : MainActivity.EventsData) {
                            if (CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate()).toLocalDate().equals(selectedDate)) {
                                eventCounter++;
                                tempCalendarEvent = calendarEvent;
                            }
                        }

                        for (CalendarEvent calendarEvent : MainActivity.TempEvents) {
                            if (CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate()).toLocalDate().equals(selectedDate)) {
                                eventCounter++;
                                tempCalendarEvent = calendarEvent;
                            }
                        }

                        if (eventCounter == 1) {
                            monthCounterOneInDayCell = true;
                            break;
                        } else {

                            boolean eventHandled = false; // Flag to track if any event is handled
                            // Check if the touch event occurs within the bounds of any day's block
                            for (int i = 0; i < touchEventsForMonthView.size(); i++) {
                                if ((x >= touchEventsForMonthView.get(i).getStartX() && x <= touchEventsForMonthView.get(i).getEndX()) &&
                                        (y >= touchEventsForMonthView.get(i).getStartY() && y <= touchEventsForMonthView.get(i).getEndY())) {
                                    tempCalendarEvent = getSelectedCalendarEventMonthView(x, y);
                                    break; // Exit the loop since the event is handled
                                } else {
                                    tempCalendarEvent = null;
                                }
                            }

                            break;
                        }

                    } else {
                        selectedDate = LocalDate.from(getSelectedDateMonth(x, y));
                    }


                    break;
                case MotionEvent.ACTION_UP:
                    // Perform action for the selected date if a valid selection was made
                    if (selectedDate != null) {
                        // Check if the touch event occurs within any event's inner rectangle
                        performClick();
                    }
                    break;
                default:
                    // For other touch actions, delegate to super implementation
                    return super.onTouchEvent(event);
            }
            return true; // Consume the event
        }
        else if (Objects.equals(modeCalendar, WEEK_VIEW)) {
            float x = event.getX();
            float y = event.getY();
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    boolean eventHandled = false; // Flag to track if any event is handled
                    // Check if the touch event occurs within the bounds of any day's block
                    for (int i = 0; i < touchEventsForWeekView.size(); i++) {
                        if ((x >= touchEventsForWeekView.get(i).getStartX() && x <= touchEventsForWeekView.get(i).getEndX()) &&
                                (y >= touchEventsForWeekView.get(i).getStartY() && y <= touchEventsForWeekView.get(i).getEndY())) {
                            tempCalendarEvent = getSelectedCalendarEventWeekView(x, y);
                            eventHandled = true; // Mark the event as handled
                            break; // Exit the loop since the event is handled
                        }
                    }
                    // If no event is handled, return false
                    if (!eventHandled) {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // Perform action for the selected date if a valid selection was made
                    if (selectedDate != null) {
                        performClick();
                    }
                    break;
                default:
                    // For other touch actions, delegate to super implementation
                    return super.onTouchEvent(event);
            }
            return true; // Consume the event

        }
        else if (Objects.equals(modeCalendar, DAY_VIEW)) {
            float x = event.getX();
            float y = event.getY();
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    boolean eventHandled = false; // Flag to track if any event is handled

                    // Check if the touch event occurs within the bounds of any day's block
                    for (int i = 0; i < touchEventsDayView.size(); i++) {
                        if ((x >= touchEventsDayView.get(i).getStartX() && x <= touchEventsDayView.get(i).getEndX()) &&
                                (y >= touchEventsDayView.get(i).getStartY() && y <= touchEventsDayView.get(i).getEndY())) {
                            tempCalendarEvent = getSelectedCalendarEventsDayView(x, y);
                            eventHandled = true; // Mark the event as handled
                            break; // Exit the loop since the event is handled
                        }
                    }
                    // If no event is handled, return false
                    if (!eventHandled) {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // Perform action for the selected date if a valid selection was made
                    if (selectedDate != null) {
                        performClick();
                    }
                    break;
                default:
                    // For other touch actions, delegate to super implementation
                    return super.onTouchEvent(event);
            }
            return true; // Consume the event
        }
        else {
            return false;
        }
    }

    //----------------------------OnTouchMethods----------------------------//
    @Override
    public boolean performClick() {
        if (modeCalendar == MONTH_VIEW) {

            if (weAreInTablet) {
                if (monthCounterOneInDayCell) {
                    dayNow = selectedDate.atStartOfDay();
                    dayClickedFromMonthForNewEvent = CalendarUtils.convertLocalDateTimeToDate(dayNow);
                    CalendarViews.dateForViewPagerRegister=null;
                    MainActivity.monthViewCellClicked = false;
                    handleDaySelection();
                } else {
                    if (hasDayEvents(selectedDate)) {
                        handleCalendarEventSelection();
                    } else {
                        dayNow = selectedDate.atStartOfDay();
                        dayClickedFromMonthForNewEvent = CalendarUtils.convertLocalDateTimeToDate(dayNow);
                        CalendarViews.dateForViewPagerRegister=null;
                        MainActivity.monthViewCellClicked = true;
                        handleDaySelection();
                    }
                }
            } else {
                dayNow = selectedDate.atStartOfDay();
                dayClickedFromMonthForNewEvent = CalendarUtils.convertLocalDateTimeToDate(dayNow);
                CalendarViews.dateForViewPagerRegister=null;
                MainActivity.monthViewCellClicked = true;
                handleDaySelection();
            }
        } else if (modeCalendar == WEEK_VIEW) {

            handleCalendarEventSelection();
        } else if (modeCalendar == DAY_VIEW) {
            handleCalendarEventSelection();
        }

        return super.performClick();
    }

    private LocalDateTime getSelectedDateMonth(float x, float y) {
        // Calculate the selected date based on the touch coordinates
        int row = (int) (y / (getHeight() / 6)); // Assuming 6 rows for weeks
        int column = (int) (x / (getWidth() / 7)); // Assuming 7 columns for days in a week

        // Calculate the index of the selected day in the month
        int selectedIndex = row * 7 + column;

        // Get the LocalDateTime for the selected day
        return monthNow.get(selectedIndex);
    }

    private LocalDateTime getSelectedDateWeek(float x, float y) {
        // Calculate the selected day based on the touch coordinates
        int dayOfWeek = (int) (x / (getWidth() / NUM_DAYS_IN_WEEK)); // Assuming NUM_DAYS_IN_WEEK days in a week
        int hourOfDay = (int) (y / (getHeight() / HOURS_IN_DAY)); // Assuming HOURS_IN_DAY hours in a day

        // Calculate the index of the selected day in the week
        int selectedIndex = hourOfDay * NUM_DAYS_IN_WEEK + dayOfWeek;

        // Get the LocalDateTime for the selected day
        return weekNow.get(dayOfWeek);
    }

    private CalendarEvent getSelectedCalendarEventMonthView(float x, float y) {
        for (int i = 0; i < touchEventsForMonthView.size(); i++) {
            if ((x >= touchEventsForMonthView.get(i).getStartX() && x <= touchEventsForMonthView.get(i).getEndX()) &&
                    (y >= touchEventsForMonthView.get(i).getStartY() && y <= touchEventsForMonthView.get(i).getEndY())) {
                return touchEventsForMonthView.get(i).getCalendarEvent();
            }
        }
        return null;
    }

    private CalendarEvent getSelectedCalendarEventWeekView(float x, float y) {
        for (int i = 0; i < touchEventsForWeekView.size(); i++) {
            if ((x >= touchEventsForWeekView.get(i).getStartX() && x <= touchEventsForWeekView.get(i).getEndX()) &&
                    (y >= touchEventsForWeekView.get(i).getStartY() && y <= touchEventsForWeekView.get(i).getEndY())) {
                return touchEventsForWeekView.get(i).getCalendarEvent();
            }
        }
        return null;
    }

    private CalendarEvent getSelectedCalendarEventsDayView(float x, float y) {
        for (int i = 0; i < touchEventsDayView.size(); i++) {
            if ((x >= touchEventsDayView.get(i).getStartX() && x <= touchEventsDayView.get(i).getEndX()) &&
                    (y >= touchEventsDayView.get(i).getStartY() && y <= touchEventsDayView.get(i).getEndY())) {
                return touchEventsDayView.get(i).getCalendarEvent();
            }
        }
        return null;
    }

    private void handleDaySelection() {
        int counter = 0;
        CalendarEvent dayEvent = new CalendarEvent();
        for (CalendarEvent event : calendarEvents) {
            if (CalendarUtils.convertStringToLocalDateTime(event.getStartDate()).toLocalDate().equals(selectedDate)) {
                dayEvent = event;
                counter++;
            }
        }

        for (CalendarEvent event : MainActivity.TempEvents) {
            if (CalendarUtils.convertStringToLocalDateTime(event.getStartDate()).toLocalDate().equals(selectedDate)) {
                dayEvent = event;
                counter++;
            }
        }
        if (counter == 1) {
            EventActivity.eventId = dayEvent.getId();
            MainActivity.monthViewCellClicked = false;

            MainActivity.SHOW_MODE =true;
            MainActivity.EDIT_MODE =false;
            MainActivity.NEW_MODE =false;

            Intent i = new Intent(getContext(), EventActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line to set the FLAG_ACTIVITY_NEW_TASK flag
            getContext().startActivity(i);
        } else {
            CalendarFormats.reloadActivity(MainActivity.myActivity);
        }


    }

    private boolean hasDayEvents(LocalDate selected) {
        for (CalendarEvent calendarEvent : calendarEvents) {
            if (CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate()).toLocalDate().equals(selected)) {
                return true;
            }
        }

        for (CalendarEvent calendarEvent : MainActivity.TempEvents) {
            if (CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate()).toLocalDate().equals(selected)) {
                return true;
            }
        }
        return false;
    }

    private void handleCalendarEventSelection() {

        if (tempCalendarEvent != null) {
            MainActivity.SHOW_MODE = true;
            MainActivity.EDIT_MODE =false;
            MainActivity.NEW_MODE = false;
            MainActivity.listViewShownFromMenu = false;
            for (int i = 0; i < calendarEvents.size(); i++) {

                if (Objects.equals(calendarEvents.get(i).getId(), tempCalendarEvent.getId()) && !tempCalendarEvent.isTemp()) {
                    MainActivity.position = i;
                    EventActivity.eventId = calendarEvents.get(i).getId();
                    MainActivity.listViewShownFromDayView = true;

                }

                if (tempCalendarEvent.isTemp()) {
                    for (int j = 0; j < calendarEvents.size(); j++) {
                        if (Objects.equals(calendarEvents.get(i).getId(), tempCalendarEvent.getId())) {
                            MainActivity.position = i;
                            EventActivity.eventId = calendarEvents.get(i).getId();
                            MainActivity.listViewShownFromDayView = true;
                        }
                    }
                }


            }
            Intent i = new Intent(getContext(), EventActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line to set the FLAG_ACTIVITY_NEW_TASK flag
            getContext().startActivity(i);
        }

    }

    //----------------------------WEEK----------------------------//

    private void drawWeekHourBackground(Canvas canvas) {
        for (int i = 0; i < NUM_DAYS_IN_WEEK; i++) {
            for (int j = 0; j < NUM_HOURS_IN_DAY; j++) {
                float startX = i * (float) (getWidth() / NUM_DAYS_IN_WEEK) + 15;
                float endX = (float) (i + 1) * getWidth() / NUM_DAYS_IN_WEEK + 15; // Adjusted endX
                float startY = (float) j * getHeight() / NUM_HOURS_IN_DAY;
                float endY = (float) (j + 1) * getHeight() / NUM_HOURS_IN_DAY;

                canvas.drawRect(startX, startY, endX, endY, hourBackgroundPaint);
                if (i == 5 | i == 6) {
                    hourBackgroundPaint.setColor(getResources().getColor(R.color.color_LightGray));
                    canvas.drawRect(startX, 0, endX, endY, hourBackgroundPaint);
                }
            }
        }
    }

    private void drawWeekCurrentTimeIndicator(Canvas canvas) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        for (int i = 0; i < weekNow.size(); i++) {
            if (currentDate.isEqual(weekNow.get(i).toLocalDate())) {
                int currentHour = currentTime.getHour();
                int currentMin = currentTime.getMinute();

                // Calculate the X-coordinate for the current day
                int currentDayOfWeek = weekNow.get(i).getDayOfWeek().getValue(); // 1 = Monday, 2 = Tuesday, ..., 7 = Sunday
                float currentX;
                if (weAreInTablet) {
                    currentX = (currentDayOfWeek - 1) * (viewWidth / NUM_DAYS_IN_WEEK) + dpToPx(10);
                } else {
                    currentX = (currentDayOfWeek - 1) * (viewWidth / NUM_DAYS_IN_WEEK) + dpToPx(5);
                }


                // Calculate the Y-coordinate for the current time
                float currentY;
                if (weAreInTablet) {
                    currentY = ((float) (currentHour * viewHeight) / HOURS_IN_DAY) + ((float) (currentMin * viewHeight) / HOURS_IN_DAY / 60);
                } else {
                    currentY = ((float) (currentHour * viewHeight) / HOURS_IN_DAY) + ((float) (currentMin * viewHeight) / HOURS_IN_DAY / 60) - dpToPx(5);
                }


                // Draw real-time circle
                canvas.drawCircle(currentX, currentY, 10f, realTimePaint);

                // Draw real-time line
                float lineStartX = currentX;
                float lineEndX;

                if (weAreInTablet) {
                    lineEndX = (currentDayOfWeek) * (viewWidth / NUM_DAYS_IN_WEEK) + dpToPx(10);
                } else {
                    lineEndX = (currentDayOfWeek) * (viewWidth / NUM_DAYS_IN_WEEK) + dpToPx(6);
                }


                canvas.drawLine(lineStartX, currentY, lineEndX, currentY, realTimePaint);


            }
        }
    }

    //----------------------------WEEK-EVENTS----------------------------//
    private void drawWeekHoursBoxes(Canvas canvas) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float blockWidth = viewWidth / NUM_DAYS_IN_WEEK;

        // Draw horizontal lines for each hour
        drawHorizontalLines(canvas, viewHeight);

        // Draw vertical lines for each day
        drawVerticalLines(canvas, blockWidth, viewHeight);

        loopForEventProps(viewHeight, canvas, blockWidth);
        drawWeekCurrentTimeIndicator(canvas);
    }

    private void drawHorizontalLines(Canvas canvas, int viewHeight) {
        for (int i = 0; i < HOURS_IN_DAY; i++) {
            float y = i * (viewHeight / HOURS_IN_DAY);
            canvas.drawLine(0, y, getWidth(), y, linePaint);
        }
    }

    private void drawVerticalLines(Canvas canvas, float blockWidth, int viewHeight) {
        for (int i = 0; i < NUM_DAYS_IN_WEEK; i++) {
            float x = i * blockWidth + 15;
            canvas.drawLine(x, 0, x, viewHeight, linePaint);
        }
    }


    private List<CalendarEvent> getEventsInDay(LocalDateTime dateTime) {
        List<CalendarEvent> eventsInDay = new ArrayList<>();
        for (CalendarEvent calendarEvent : calendarEvents) {
            if (!(calendarEvent.getStartDate() == null) && !calendarEvent.isHasTemp() && !calendarEvent.isTemp()) {
                if (dateTime.toLocalDate().equals(CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate()).toLocalDate())) {
                    eventsInDay.add(calendarEvent);
                }
            }

        }

        for (CalendarEvent calendarEvent : MainActivity.TempEvents) {
            if (!(calendarEvent.getStartDate() == null) && calendarEvent.isTemp() && !calendarEvent.isHasTemp()) {
                if (dateTime.toLocalDate().equals(CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate()).toLocalDate())) {
                    eventsInDay.add(calendarEvent);
                }
            }
        }
        return eventsInDay;
    }

    private List<List<CalendarEvent>> groupOverlappingEvents(List<CalendarEvent> events) {
        List<List<CalendarEvent>> groups = new ArrayList<>();

        for (CalendarEvent event : events) {
            if (event.getStartDate() == null || event.getStartTime() == null
                    || event.getEndDate() == null || event.getEndTime() == null) {
                continue; // Skip events with null dates/times
            }

            boolean added = false;

            for (List<CalendarEvent> group : groups) {
                if (isOverlapping(event, group)) {
                    group.add(event);
                    added = true;
                    break;
                }
            }

            if (!added) {
                List<CalendarEvent> newGroup = new ArrayList<>();
                newGroup.add(event);
                groups.add(newGroup);
            }
        }

        return groups;
    }

    private boolean isOverlapping(CalendarEvent event, List<CalendarEvent> group) {
        for (CalendarEvent other : group) {
            if (isTimeOverlapping(event, other)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeOverlapping(CalendarEvent event1, CalendarEvent event2) {
        LocalDateTime start1 = CalendarUtils.convertStringToLocalDateTime(event1.getStartTime());
        LocalDateTime end1 = CalendarUtils.convertStringToLocalDateTime(event1.getEndTime());
        LocalDateTime start2 = CalendarUtils.convertStringToLocalDateTime(event2.getStartTime());
        LocalDateTime end2 = CalendarUtils.convertStringToLocalDateTime(event2.getEndTime());

        // Calculate the time in minutes since midnight for each event
        long startMinutes1 = start1.getHour() * 60 + start1.getMinute();
        long endMinutes1 = end1.getHour() * 60 + end1.getMinute();
        long startMinutes2 = start2.getHour() * 60 + start2.getMinute();
        long endMinutes2 = end2.getHour() * 60 + end2.getMinute();

        // Determine if there is overlap in the time span
        return startMinutes1 < endMinutes2 && startMinutes2 < endMinutes1;
    }

    private void loopForEventProps(int viewHeight, Canvas canvas, float blockWidth) {


        // Draw events
        for (int j = 0; j < weekNow.size(); j++) {
            List<CalendarEvent> eventsInDay = getEventsInDay(weekNow.get(j));
            List<List<CalendarEvent>> overlappingGroups = groupOverlappingEvents(eventsInDay);

            for (List<CalendarEvent> group : overlappingGroups) {
                float groupWidth = blockWidth / group.size();
                float offsetX = 0;

                for (CalendarEvent event : group) {
                    if (event.getStartTime() != null && event.getEndTime() != null
                            && event.getStartDate() != null && event.getEndDate() != null) {
                        int startDayOfWeek = CalendarUtils.convertStringToLocalDateTime(event.getStartDate()).getDayOfWeek().getValue();
                        int startHour = CalendarUtils.convertStringToLocalDateTime(event.getStartTime()).getHour();
                        int startMin = CalendarUtils.convertStringToLocalDateTime(event.getStartTime()).getMinute();
                        int endHour = CalendarUtils.convertStringToLocalDateTime(event.getEndTime()).getHour();
                        int endMin = CalendarUtils.convertStringToLocalDateTime(event.getEndTime()).getMinute();
                        if (startHour != endHour || startMin != endMin) {
                            float startX = (startDayOfWeek - 1) * blockWidth + 15 + offsetX;
                            float startY = (float) ((endHour * 60 + endMin) * viewHeight / (HOURS_IN_DAY * 60));

                            float endY = startHour * (float) (viewHeight / HOURS_IN_DAY) + startMin * 2;
                            float endX = startX + groupWidth;

                            drawEvent(canvas, startX, startY, endX, endY, event, false);
                            offsetX += groupWidth; // Increment offset by event width
                            EventCoordinates eventCoordinates = new EventCoordinates(startX, endX, startY, endY, event);
                            touchEventsForWeekView.add(eventCoordinates);
                        }


                    }
                }
            }
        }
    }

    private void drawEvent(Canvas canvas, float startX, float startY, float endX, float endY, CalendarEvent calendarEvent, boolean drawSmall) {

        // Draw a rounded rectangle for the event background
        RectF rectF = new RectF(startX, startY, endX, endY);
        float cornerRadius = 5f;

        // Adjust size if drawSmall is true
        if (drawSmall) {

            rectF.set(startX, startY, endX, endY);
        }

        weekAndDailyEvents.setColor(getResources().getColor(R.color.pal_blue));

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, weekAndDailyEvents);



            textPaint.setColor(Color.WHITE);

        String eventTitle = calendarEvent.getDescr();
        // Draw event title if not drawSmall
        if (!drawSmall) {
            // Calculate available width for text within the rectangle
            float availableWidth = endX - startX;

            // Calculate text size
            Rect textBounds = new Rect();
            textPaint.getTextBounds(eventTitle, 0, eventTitle.length(), textBounds);

            // Calculate maximum number of characters that can fit on each line
            int maxCharsPerLine = (int) (availableWidth / textBounds.width() * eventTitle.length());

            // Draw the event title with line breaks if necessary
            int startIndex = 0;
            int endIndex = Math.min(maxCharsPerLine, eventTitle.length());
            float textX = startX + 1; // Offset for padding
            float textY = startY + textPaint.getTextSize(); // Initial position for the first line of text

            while (startIndex < eventTitle.length()) {
                // Draw a substring of the event title that fits within the available width
                String substring = eventTitle.substring(startIndex, endIndex);
                canvas.drawText(substring, textX, textY, textPaint);

                // Move to the next line
                textY += 25; // Increase the vertical spacing between lines

                // Update indices for the next substring
                startIndex = endIndex;
                endIndex = Math.min(startIndex + maxCharsPerLine, eventTitle.length());
            }
        }
    }


    //----------------------------DAY----------------------------//
    private void drawDailyHourBackground(Canvas canvas) {
        for (int i = 0; i < NUM_DAYS_IN_WEEK; i++) {
            for (int j = 0; j < NUM_HOURS_IN_DAY; j++) {
                float startX = i * getWidth() / NUM_DAYS_IN_WEEK;
                float endX = (i + 1) * getWidth() / NUM_DAYS_IN_WEEK;
                float endY = (j + 1) * getHeight() / NUM_HOURS_IN_DAY;

                canvas.drawRect(startX, 0, endX, endY, hourBackgroundPaint);
            }
        }
    }

    private void drawDailyHourBoxes(Canvas canvas) {
        if (dayNow == null) {
            dayNow = selectedDate.atStartOfDay();
        }

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float blockWidth = viewWidth - 50f;
        if (weAreInTablet) {
            // Draw horizontal lines for each hour
            for (int i = 0; i < HOURS_IN_DAY; i++) {
                float y = i * viewHeight / HOURS_IN_DAY;
                canvas.drawLine(10, y, viewWidth, y, linePaint);
            }

        } else {
            // Draw horizontal lines for each hour
            for (int i = 0; i < HOURS_IN_DAY; i++) {
                float y = i * viewHeight / HOURS_IN_DAY;
                canvas.drawLine(50f, y, viewWidth, y, linePaint);
            }

        }

        // Draw events
        List<CalendarEvent> dailyEvents = getDailyEvents(dayNow.toLocalDate());
        List<List<CalendarEvent>> overlappingGroups = groupOverlappingEvents(dailyEvents);

        float textVerticalOffset = 35f; // Adjust the vertical offset for the text

        for (List<CalendarEvent> group : overlappingGroups) {
            float groupWidth = blockWidth / group.size();
            float offsetX = 0;

            for (CalendarEvent event : group) {

                if (event.getStartTime() != null && event.getEndTime() != null &&
                        event.getStartDate() != null && event.getEndDate() != null) {

                    int startHour = CalendarUtils.convertStringToLocalDateTime(event.getStartTime()).getHour();
                    int startMin = CalendarUtils.convertStringToLocalDateTime(event.getStartTime()).getMinute();
                    int endHour = CalendarUtils.convertStringToLocalDateTime(event.getEndTime()).getHour();
                    int endMin = CalendarUtils.convertStringToLocalDateTime(event.getEndTime()).getMinute();
                    if (startHour != endHour || startMin != endMin) {
                        float endY = (startHour * viewHeight / HOURS_IN_DAY) + startMin * 2;
                        float startY = ((endHour * 60 + endMin) * viewHeight) / (HOURS_IN_DAY * 60);

                        float startX = offsetX + 25f;
                        float endX = startX + groupWidth;

                        drawDailyEvent(canvas, startX, startY, endX, endY, textVerticalOffset, event);

                        offsetX += groupWidth; // Increment offset by event width
                        EventCoordinates eventCoordinates = new EventCoordinates(startX, endX, startY, endY, event);
                        touchEventsDayView.add(eventCoordinates);
                    }
                }
            }
        }

        drawDayCurrentTimeIndicator(canvas);
    }

    private void drawDailyEvent(Canvas canvas, float eventLineX, float eventStartY, float endX, float eventEndY, float textVerticalOffset, CalendarEvent event) {
        RectF rectF = new RectF(eventLineX, eventStartY, endX, eventEndY);
        float cornerRadius = 5f; // Adjust the radius as needed

        weekAndDailyEvents.setColor(getResources().getColor(R.color.pal_blue));

            textPaint.setColor(Color.WHITE);

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, weekAndDailyEvents);

        // Draw the event description
        canvas.drawText(event.getDescr(), eventLineX + 5, eventStartY + textVerticalOffset, textPaint);
    }

    private void drawDayCurrentTimeIndicator(Canvas canvas) {
        int viewHeight = getHeight();
        LocalDate timeNow = LocalDateTime.now().toLocalDate();
        if (dayNow.toLocalDate().equals(timeNow)) {
            int currentHour = LocalDateTime.now().getHour();
            int currentMin = LocalDateTime.now().getMinute();

            // Calculate the Y-coordinate for the current time
            float currentY = (currentHour * viewHeight / HOURS_IN_DAY) + (currentMin * viewHeight / HOURS_IN_DAY / 60);
            // Draw real-time circle
            canvas.drawCircle(20f, currentY, 20f, realTimePaint);
            // Draw real-time line
            canvas.drawLine(30f, currentY, getWidth(), currentY + 5f, realTimePaint);
        }
    }

    private List<CalendarEvent> getDailyEvents(LocalDate date) {
        List<CalendarEvent> dailyEvents = new ArrayList<>();
        for (CalendarEvent calendarEvent : calendarEvents) {
            if (date.equals(CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate()).toLocalDate())
                    && !calendarEvent.isHasTemp() && !calendarEvent.isTemp()) {
                dailyEvents.add(calendarEvent);
            }
        }

        for (CalendarEvent calendarEvent : MainActivity.TempEvents) {
            if (!(calendarEvent.getStartDate() == null) && calendarEvent.isTemp() && !calendarEvent.isHasTemp()) {
                if (date.equals(CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate()).toLocalDate())) {
                    dailyEvents.add(calendarEvent);
                }
            }
        }
        return dailyEvents;
    }

    public static class DaysOfWeekViews extends View {
        private Paint headerPaint;
        private Paint linePaint;
        private Paint daysOfWeekOfMonthPaint;
        private Paint daysOfWeekPaint;
        private Paint eventsOfWeekPaint;
        private Paint remainingEventsPaint;
        private Paint primaryOutlineColor;
        private Paint dayOfWeekOfMonthCurrentDatePaint;
        private Paint dayOfMonthDayViewPaint;
        private Paint dayOfMonthDayCurrentDayViewPaint;
        private Paint dayOfWeekOfMonthOfDayView;
        private final Path arrowPath = new Path();
        private Canvas myCanvas;

        private ArrayList<LocalDateTime> weekList = new ArrayList<>();
        private ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();
        ArrayList<EventCoordinates> touchEventsDaysOfWeeks = new ArrayList<>();
        private LocalDateTime day;

        private float lastEventY;


        public int extraDimenHeightCalendarView;


        public DaysOfWeekViews(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        //Setters
        public void setMonthForViewPagerDaysOfWeek() {
            initialize();
            invalidate();
            requestLayout();
        }

        public void setDayForViewPagerDaysOfWeek(LocalDateTime newDay, ArrayList<CalendarEvent> calendarEvents) {
            this.calendarEvents = calendarEvents;
            this.day = newDay;

            initialize();
            invalidate();
            requestLayout();
        }

        public void setWeekForViewPagerDaysOfWeek(ArrayList<LocalDateTime> weekList, ArrayList<CalendarEvent> calendarEvents) {
            this.weekList = weekList;
            this.calendarEvents = calendarEvents;
            initialize();
            invalidate();
            requestLayout();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);


            this.myCanvas = canvas;
            initialize();

            if (modeCalendar == MONTH_VIEW) {
                if (weAreInTablet) {
                    drawMonthHeaderTablet(canvas);
                } else {
                    drawMonthHeader(canvas);
                }
            } else if (modeCalendar == WEEK_VIEW) {
                drawWeekHeader(canvas);

            } else if (modeCalendar == DAY_VIEW) {

                drawDayHeader(canvas);

            }

        }


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int desiredHeight = 0;
            switch (modeCalendar) {
                case MONTH_VIEW:

                    desiredHeight = dpToPx(20); // Default height for modeCalendar 0 in dp

                    break;

                case WEEK_VIEW:
                    if (!calendarEvents.isEmpty()) {
                        desiredHeight = calculateDesiredHeightForMode1();
                    } else {
                        if (weAreInTablet) {
                            desiredHeight = dpToPx(80); // Default height for modeCalendar 1 in dp
                        } else {
                            desiredHeight = dpToPx(65); // Default height for modeCalendar 1 in dp
                        }
                    }
                    break;

                case DAY_VIEW:
                    if (weAreInTablet) {
                        desiredHeight = dpToPx(130); // Default height for modeCalendar 2 in dp
                    } else {
                        desiredHeight = dpToPx(85); // Default height for modeCalendar 2 in dp
                    }
                    break;
            }
            extraDimenHeightCalendarView = desiredHeight;

            setMeasuredDimension(widthMeasureSpec, desiredHeight);
        }

        private int dpToPx(int dp) {
            float density = getResources().getDisplayMetrics().density;
            return Math.round(dp * density);
        }


        private int calculateDesiredHeightForMode1() {
            int defaultHeight;
            int plusHeightForEvents;

            if (weAreInTablet) {
                defaultHeight = dpToPx(80); // Default height for modeCalendar 1 in dp
                plusHeightForEvents = dpToPx(25); // Height increment per event for tablets in dp
            } else {
                defaultHeight = dpToPx(65); // Default height for modeCalendar 1 in dp
                plusHeightForEvents = dpToPx(15); // Height increment per event for phones in dp
            }

            int maxEventsPerDay = getMaxEventsPerDayForHeaderDimens();

            // Increase height based on the maximum number of events
            if (maxEventsPerDay > 0) {
                defaultHeight += plusHeightForEvents;
            }
            if (maxEventsPerDay > 1) {
                defaultHeight += plusHeightForEvents;
            }
            if (maxEventsPerDay > 2) {
                defaultHeight += plusHeightForEvents;
            }

            if (maxEventsPerDay > 3) {
                defaultHeight += plusHeightForEvents;
            }

            return defaultHeight;
        }

        private int getMaxEventsPerDayForHeaderDimens() {
            int maxEvents = 0;

            for (LocalDateTime weekDay : weekList) {

                int eventsCount = countEventsForDay(weekDay);
                maxEvents = Math.max(maxEvents, eventsCount);
            }

            return maxEvents;
        }

        private int getMaxEventsPerDayForWeekHeader() {
            int maxEvents = 0;

            for (LocalDateTime weekDay : weekList) {
                int eventsCount = countEventsForDay(weekDay);
                maxEvents = Math.max(maxEvents, eventsCount);
            }

            return maxEvents;
        }

        private int countEventsForDay(LocalDateTime day) {
            int counter = 0;


            for (CalendarEvent calendarEvent : calendarEvents) {
                if (CalendarUtils.convertStringToLocalDateTime(calendarEvent.getStartDate()).toLocalDate().equals(day.toLocalDate()) && !calendarEvent.isHasTemp()) {
                    counter++;
                }
            }

            return counter;
        }

        private void initialize() {
            // Common Paint objects
            Typeface appTypeface = getTypeFaceFont(getContext());

            float textSizeTitle = dpToPx((int) getResources().getDimension(R.dimen.text_size_title));
            if (weAreInTablet) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                    // Here we are in Android version 8 or lower
                    headerPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle,appTypeface);
                    eventsOfWeekPaint = createTextPaint(Color.WHITE, textSizeTitle,appTypeface);
                    remainingEventsPaint = createTextPaint(Color.WHITE, textSizeTitle,appTypeface);


                    daysOfWeekPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle,appTypeface);
                    daysOfWeekOfMonthPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle,appTypeface);
                    dayOfWeekOfMonthCurrentDatePaint = createTextPaint(Color.WHITE, textSizeTitle,appTypeface);

                    dayOfWeekOfMonthOfDayView = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle,appTypeface);
                    dayOfMonthDayCurrentDayViewPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle,appTypeface);
                    dayOfMonthDayViewPaint = createTextPaint(Color.WHITE, textSizeTitle,appTypeface);
                } else {
                    headerPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle,appTypeface);
                    eventsOfWeekPaint = createTextPaint(Color.WHITE, textSizeTitle - spToPx((int) 15),appTypeface);
                    remainingEventsPaint = createTextPaint(Color.WHITE, textSizeTitle - spToPx((int) 12),appTypeface);


                    daysOfWeekPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle - spToPx((int) 15),appTypeface);
                    daysOfWeekOfMonthPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle - spToPx((int) 10),appTypeface);
                    dayOfWeekOfMonthCurrentDatePaint = createTextPaint(Color.WHITE, textSizeTitle - spToPx((int) 10),appTypeface);

                    dayOfWeekOfMonthOfDayView = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle - spToPx((int) 10),appTypeface);
                    dayOfMonthDayCurrentDayViewPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle - spToPx((int) 5),appTypeface);
                    dayOfMonthDayViewPaint = createTextPaint(Color.WHITE, textSizeTitle - spToPx((int) 5),appTypeface);
                }

            } else {
                headerPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle,appTypeface);
                eventsOfWeekPaint = createTextPaint(Color.WHITE, textSizeTitle - spToPx((int) 35),appTypeface);
                remainingEventsPaint = createTextPaint(Color.WHITE, textSizeTitle - spToPx((int) 35),appTypeface);


                daysOfWeekPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle - spToPx((int) 30),appTypeface);
                daysOfWeekOfMonthPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle - spToPx((int) 30),appTypeface);
                dayOfWeekOfMonthCurrentDatePaint = createTextPaint(Color.WHITE, textSizeTitle - spToPx((int) 30),appTypeface);

                dayOfWeekOfMonthOfDayView = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle - spToPx((int) 30),appTypeface);
                dayOfMonthDayCurrentDayViewPaint = createTextPaint(getResources().getColor(R.color.pal_blue), textSizeTitle - spToPx((int) 30),appTypeface);
                dayOfMonthDayViewPaint = createTextPaint(Color.WHITE, textSizeTitle - spToPx((int) 30),appTypeface);
            }

            linePaint = createLinePaint();


            primaryOutlineColor = new Paint();
            primaryOutlineColor.setColor(getResources().getColor(R.color.pal_blue));
            primaryOutlineColor.setStyle(Paint.Style.STROKE); // Set the style to STROKE
            primaryOutlineColor.setDither(true);
            primaryOutlineColor.setAntiAlias(true);
            primaryOutlineColor.setStrokeWidth(2); // Set the stroke width (adjust as needed)
        }

        private float spToPx(float sp) {
            float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
            return sp * scaledDensity;
        }

        private Paint createTextPaint(int color, float textSize,Typeface appTypeface) {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setTextSize(textSize);
            paint.setAntiAlias(true);
            paint.setDither(true);
            if (appTypeface != null) {
                paint.setTypeface(appTypeface);
            }
            return paint;
        }

        private Paint createLinePaint() {
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#e0e0e0"));
            paint.setStrokeWidth((float) 1.0);
            paint.setAntiAlias(true);
            paint.setDither(true);
            return paint;
        }

        //Month
        private void drawMonthHeader(Canvas canvas) {
            float startY = dpToPx(15);
            float x = dpToPx(23);
            String monday = getContext().getString(R.string.monday); // Replace with the actual string resource
            String tuesday = getContext().getString(R.string.tuesday); // Replace with the actual string resource
            String wednesday = getContext().getString(R.string.wednesday); // Replace with the actual string resource
            String thursday = getContext().getString(R.string.thursday); // Replace with the actual string resource
            String friday = getContext().getString(R.string.friday); // Replace with the actual string resource
            String saturday = getContext().getString(R.string.saturday); // Replace with the actual string resource
            String sunday = getContext().getString(R.string.sunday); // Replace with the actual string resource

            // Draw the month header text
            canvas.drawText(monday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7; // Move to the next day

            canvas.drawText(tuesday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(wednesday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(thursday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(friday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(saturday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(sunday, x, startY, daysOfWeekPaint);
        }

        private void drawMonthHeaderTablet(Canvas canvas) {
            float startY = dpToPx(15);
            float x = dpToPx(45);
            String monday = getContext().getString(R.string.day_monday); // Replace with the actual string resource
            String tuesday = getContext().getString(R.string.day_tuesday); // Replace with the actual string resource
            String wednesday = getContext().getString(R.string.day_wednesday); // Replace with the actual string resource
            String thursday = getContext().getString(R.string.day_thursday); // Replace with the actual string resource
            String friday = getContext().getString(R.string.day_friday); // Replace with the actual string resource
            String saturday = getContext().getString(R.string.day_saturday); // Replace with the actual string resource
            String sunday = getContext().getString(R.string.day_sunday); // Replace with the actual string resource

            // Draw the month header text
            canvas.drawText(monday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7; // Move to the next day

            canvas.drawText(tuesday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(wednesday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(thursday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(friday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(saturday, x, startY, daysOfWeekPaint);
            x += getWidth() / 7;

            canvas.drawText(sunday, x, startY, daysOfWeekPaint);
        }

        //Week
        private void drawWeekHeader(Canvas canvas) {
            LocalDate currentDate = LocalDate.now();
            int eventsPerDayCounter;

            int maxEventsToShow = 3; // Maximum number of events to show


// Constants for positioning and sizing
            float circleRadius;
            float circleVerticalOffset; // Offset for the circle center from the y-coordinate of the day text


            float x1;
            int offSetDaysOfWeek;
            float startY;
            float additionalSpacing; // Additional spacing between the days

            if (weAreInTablet) {
                offSetDaysOfWeek = dpToPx(120);
                startY = dpToPx(15); // Adjusted y-coordinate for the days of the week
                circleRadius = dpToPx((int) 15f); // Adjust the circle radius as needed
                circleVerticalOffset = dpToPx(5);
                additionalSpacing = 0; // Adjust this value to increase spacing between days

            } else {
                offSetDaysOfWeek = dpToPx(65);
                startY = dpToPx(20); // Adjusted y-coordinate for the days of the week
                circleRadius = dpToPx((int) 10f); // Adjust the circle radius as needed
                circleVerticalOffset = dpToPx(5);
                additionalSpacing = 0; // Adjust this value to increase spacing between days

            }

            String[] daysOfWeek;
            if (weAreInTablet) {
                daysOfWeek = new String[]{
                        getContext().getString(R.string.day_monday),
                        getContext().getString(R.string.day_tuesday),
                        getContext().getString(R.string.day_wednesday),
                        getContext().getString(R.string.day_thursday),
                        getContext().getString(R.string.day_friday),
                        getContext().getString(R.string.day_saturday),
                        getContext().getString(R.string.day_sunday)
                };
            } else {
                daysOfWeek = new String[]{
                        getContext().getString(R.string.monday),
                        getContext().getString(R.string.tuesday),
                        getContext().getString(R.string.wednesday),
                        getContext().getString(R.string.thursday),
                        getContext().getString(R.string.friday),
                        getContext().getString(R.string.saturday),
                        getContext().getString(R.string.sunday)
                };
            }

            // Draw the days of the week
            for (int i = 0; i < daysOfWeek.length; i++) {

                additionalSpacing = 2;

                x1 = (float) (i * getWidth() / 8) + offSetDaysOfWeek + (i * additionalSpacing);
                canvas.drawText(daysOfWeek[i], x1, startY, daysOfWeekPaint);
            }

            float y;
            if (weAreInTablet) {
                y = dpToPx(50); // Adjusted y-coordinate for the days of the month
            } else {
                y = dpToPx(45); // Adjusted y-coordinate for the days of the month
            }
            for (int i = 0; i < weekList.size(); i++) {
                String dayOfMonth = String.valueOf(weekList.get(i).getDayOfMonth());
                float x = (float) (i * getWidth() / 8) + offSetDaysOfWeek + (i * additionalSpacing);
                // Check if it's the current day
                if (weekList.get(i).toLocalDate().equals(currentDate)) {
                    float circleX;
                    if (weAreInTablet) {
                        circleX = x + 12; // Adjust the circle position as needed

                    } else {
                        circleX = x + 22; // Adjust the circle position as needed

                    }
                    float circleY = y - circleVerticalOffset; // Adjust the circle position as needed
                    canvas.drawCircle(circleX, circleY, circleRadius, headerPaint);

                    // Calculate text width for centering
                    float textWidth = dayOfWeekOfMonthCurrentDatePaint.measureText(dayOfMonth);
                    float textX = circleX - (textWidth / 2); // Center text horizontally within the circle

                    canvas.drawText(dayOfMonth, textX, y, dayOfWeekOfMonthCurrentDatePaint);
                } else {
                    canvas.drawText(dayOfMonth, x, y, daysOfWeekOfMonthPaint);
                }
            }
            eventsPerDayCounter = getMaxEventsPerDayForWeekHeader();

            if (eventsPerDayCounter < 3) {
                drawWeekHeaderEvents(canvas);
            } else {

                drawWeekHeaderEventsRemaining(canvas, maxEventsToShow);
            }
        }

        private void drawWeekHeaderEvents(Canvas canvas) {
            for (int j = 0; j < weekList.size(); j++) {
                List<StringForWeekHeader> stringsAndColors = new ArrayList<>();
                // Collect events for the current day
                for (int i = 0; i < calendarEvents.size(); i++) {
                    if (CalendarUtils.convertStringToLocalDateTime(calendarEvents.get(i).getStartDate()).toLocalDate().equals(weekList.get(j).toLocalDate())
                            && !calendarEvents.get(i).isHasTemp()) {
                        StringForWeekHeader stringForWeekHeader = new StringForWeekHeader(calendarEvents.get(i).getDescr(), calendarEvents.get(i));
                        stringsAndColors.add(stringForWeekHeader);
                    }
                }
                int offSetDaysOfWeek;
                float additionalSpacing; // Additional spacing between the days

                if (weAreInTablet) {
                    offSetDaysOfWeek = dpToPx(120);
                    additionalSpacing = 0; // Adjust this value to increase spacing between days

                } else {
                    offSetDaysOfWeek = dpToPx(70);
                    additionalSpacing = 0; // Adjust this value to increase spacing between days

                }
                // Draw events
                float x;
                x = (float) (j * getWidth() / 8) + offSetDaysOfWeek + (j * additionalSpacing);
                float y;
                if (weAreInTablet) {
                    y = dpToPx(80); // Initial y-coordinate for drawing events, adjusted higher but still below the days of the month

                } else {
                    y = dpToPx(55); // Initial y-coordinate for drawing events, adjusted higher but still below the days of the month

                }

                for (int k = 0; k < stringsAndColors.size(); k++) {
                    String eventTitle = stringsAndColors.get(k).getTitle();
                    if (eventTitle.length() > 5) {
                        eventTitle = stringsAndColors.get(k).getTitle().substring(0, 5);
                    }


                    headerPaint.setColor(getResources().getColor(R.color.pal_blue));
                    if (weAreInTablet) {
                        RectF rect = new RectF(x - dpToPx(15), y - dpToPx(10), x + dpToPx(60), y + dpToPx(10)); // Adjust rectangle size as needed
                        canvas.drawRoundRect(rect, dpToPx(8), dpToPx(8), headerPaint); // Draw rounded rectangle
                        EventCoordinates eventCoordinates = new EventCoordinates(x - dpToPx(15), x + dpToPx(60), y - dpToPx(10), y + dpToPx(10), stringsAndColors.get(k).getColorEvent());

                        touchEventsDaysOfWeeks.add(eventCoordinates);


                            eventsOfWeekPaint.setColor(Color.WHITE);

                        canvas.drawText(eventTitle, x - dpToPx(10), y + dpToPx(5), eventsOfWeekPaint); // Draw event title within the rectangle
                        y += dpToPx(25); // Increase y-coordinate for the next event with extra spacing

                    } else {
                        RectF rect = new RectF(x - dpToPx(10), y, x + dpToPx(25), y + dpToPx(15)); // Adjust rectangle size as needed
                        canvas.drawRoundRect(rect, dpToPx(6), dpToPx(6), headerPaint); // Draw rounded rectangle



                            eventsOfWeekPaint.setColor(Color.WHITE);

                        canvas.drawText(eventTitle, x - dpToPx(6), y + dpToPx(10), eventsOfWeekPaint); // Draw event title within the rectangle
                        y += dpToPx(18); // Increase y-coordinate for the next event with extra spacing

                    }
                    lastEventY = Math.max(lastEventY, y) - dpToPx(20);

                }

            }
        }

        private void drawWeekHeaderEventsRemaining(Canvas canvas, int maxEventsToShow) {

            for (int j = 0; j < weekList.size(); j++) {
                int counter = 0;
                List<StringForWeekHeader> stringsAndColors = new ArrayList<>();
                // Collect events for the current day
                for (int i = 0; i < calendarEvents.size(); i++) {
                    if (CalendarUtils.convertStringToLocalDateTime(calendarEvents.get(i).getStartDate()).toLocalDate().equals(weekList.get(j).toLocalDate())  && !calendarEvents.get(i).isHasTemp()) {
                        StringForWeekHeader stringForWeekHeader = new StringForWeekHeader(calendarEvents.get(i).getDescr(), calendarEvents.get(i));
                        stringsAndColors.add(stringForWeekHeader);
                    }
                }

                int offSetDaysOfWeek;
                float additionalSpacing; // Additional spacing between the days
                if (weAreInTablet) {
                    offSetDaysOfWeek = dpToPx(120);
                    additionalSpacing = 0; // Adjust this value to increase spacing between days

                } else {
                    offSetDaysOfWeek = dpToPx(70);
                    additionalSpacing = 0; // Adjust this value to increase spacing between days
                }
                // Draw events
                float x;
                x = (float) (j * getWidth() / 8) + offSetDaysOfWeek + (j * additionalSpacing);

                float y;
                if (weAreInTablet) {
                    y = dpToPx(80); // Initial y-coordinate for drawing events, adjusted higher but still below the days of the month

                } else {
                    y = dpToPx(55); // Initial y-coordinate for drawing events, adjusted higher but still below the days of the month

                }
                // Draw events
                for (int k = 0; k < stringsAndColors.size(); k++) {

                    counter++;
                    if (counter > 3) {
                        break;
                    }

                    String eventTitle = stringsAndColors.get(k).getTitle();
                    if (eventTitle.length() > 5) {
                        eventTitle = stringsAndColors.get(k).getTitle().substring(0, 5);
                    }

                    headerPaint.setColor(getResources().getColor(R.color.pal_blue));
                    if (weAreInTablet) {
                        RectF rect = new RectF(x - dpToPx(15), y - dpToPx(10), x + dpToPx(60), y + dpToPx(10)); // Adjust rectangle size as needed
                        canvas.drawRoundRect(rect, dpToPx(8), dpToPx(8), headerPaint); // Draw rounded rectangle

                        EventCoordinates eventCoordinates = new EventCoordinates(x - dpToPx(15), x + dpToPx(60), y - dpToPx(10), y + dpToPx(10), stringsAndColors.get(k).getColorEvent());
                        touchEventsDaysOfWeeks.add(eventCoordinates);



                            eventsOfWeekPaint.setColor(Color.WHITE);

                        canvas.drawText(eventTitle, x - dpToPx(10), y + dpToPx(5), eventsOfWeekPaint); // Draw event title within the rectangle
                        y += dpToPx(25); // Increase y-coordinate for the next event with extra spacing

                    } else {
                        RectF rect = new RectF(x - dpToPx(10), y, x + dpToPx(25), y + dpToPx(15)); // Adjust rectangle size as needed
                        canvas.drawRoundRect(rect, dpToPx(6), dpToPx(6), headerPaint); // Draw rounded rectangle



                            eventsOfWeekPaint.setColor(Color.WHITE);

                        canvas.drawText(eventTitle, x - dpToPx(6), y + dpToPx(10), eventsOfWeekPaint); // Draw event title within the rectangle
                        y += dpToPx(18); // Increase y-coordinate for the next event with extra spacing
                    }
                }

                int remainingEvents = Math.max(0, stringsAndColors.size() - maxEventsToShow);
                if (remainingEvents > 0) {
                    String remainingText = "+" + remainingEvents;
                    float rectX = x - dpToPx(10); // Adjusted x-coordinate for the remaining events indicator
                    float textX = x - dpToPx(5); // Adjusted x-coordinate for the remaining events text
                    // Adjust the size of the rectangle for remaining events
                    if (weAreInTablet) {
                        RectF rect = new RectF(rectX, y - dpToPx(4), rectX + dpToPx(30), y + dpToPx(20)); // Larger rectangle for remaining events
                        canvas.drawRoundRect(rect, dpToPx(10), dpToPx(10), daysOfWeekOfMonthPaint); // Draw rounded rectangle
                        canvas.drawText(remainingText, textX, y + dpToPx(15), remainingEventsPaint); // Draw remaining events count
                    } else {
                        RectF rect = new RectF(rectX - dpToPx(10), y, rectX + dpToPx(10), y + dpToPx(15)); // Adjust rectangle size as needed
                        canvas.drawRoundRect(rect, dpToPx(8), dpToPx(8), daysOfWeekOfMonthPaint); // Draw rounded rectangle
                        canvas.drawText(remainingText, textX - dpToPx(10), y + dpToPx(11), remainingEventsPaint); // Draw remaining events count
                    }

                    lastEventY = y - dpToPx(10);

                }
            }

        }

        //Day
        private void drawDayHeader(Canvas canvas) {
            //Watch out potential bug!!
            if (day == null) {
                day = selectedDate.atStartOfDay();
            }
            // Constants for existing elements
            float CIRCLE_CENTER_X;
            float CIRCLE_CENTER_Y;
            float CIRCLE_RADIUS;
            if (weAreInTablet) {
                CIRCLE_CENTER_X = dpToPx((int) 30f);
                CIRCLE_CENTER_Y = dpToPx((int) 72f);
                CIRCLE_RADIUS = dpToPx((int) 22f);
            } else {
                CIRCLE_CENTER_X = dpToPx((int) 25f);
                CIRCLE_CENTER_Y = dpToPx((int) 50f);
                CIRCLE_RADIUS = dpToPx((int) 12f);
            }


            final float dayTextWidth = dayOfMonthDayViewPaint.measureText(String.valueOf(day.getDayOfMonth()));
            final float DAY_TEXT_X = CIRCLE_CENTER_X - (dayTextWidth / 2);
            final float DAY_TEXT_Y = CIRCLE_CENTER_Y + (dayOfMonthDayViewPaint.getTextSize() / 2) - dpToPx(3); // Center text vertically in the circle

            float VERTICAL_LINE_X;
            if (weAreInTablet) {
                VERTICAL_LINE_X = dpToPx((int) 70f);
            } else {
                VERTICAL_LINE_X = dpToPx((int) 50f);
            }


            // Draw existing elements
            setDayOFWeek(day, canvas);
            if (day.toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
                canvas.drawCircle(CIRCLE_CENTER_X, CIRCLE_CENTER_Y, CIRCLE_RADIUS, headerPaint);
                canvas.drawText(String.valueOf(day.getDayOfMonth()), DAY_TEXT_X, DAY_TEXT_Y, dayOfMonthDayViewPaint);
            } else {
                canvas.drawText(String.valueOf(day.getDayOfMonth()), DAY_TEXT_X, DAY_TEXT_Y, dayOfMonthDayCurrentDayViewPaint);
            }
            canvas.drawLine(VERTICAL_LINE_X, 0, VERTICAL_LINE_X, getHeight(), linePaint);


            // Constants for event drawing
            float EVENT_START_X;
            float EVENT_START_Y;
            float EVENT_HEIGHT;
            float EVENT_MARGIN;
            if (weAreInTablet) {
                EVENT_START_X = VERTICAL_LINE_X + dpToPx(5);
                EVENT_START_Y = dpToPx((int) 5f);
                EVENT_HEIGHT = dpToPx((int) 25f);
                EVENT_MARGIN = dpToPx((int) 5f);
            } else {
                EVENT_START_X = VERTICAL_LINE_X + dpToPx(3);
                EVENT_START_Y = dpToPx((int) 3f);
                EVENT_HEIGHT = dpToPx((int) 17f);
                EVENT_MARGIN = dpToPx((int) 3f);
            }


            final int MAX_EVENTS = 3; // Maximum number of events to display

            int remainingEvents = 0;
            int eventCount = 0;

            // Draw events
            for (CalendarEvent event : calendarEvents) {


                if (CalendarUtils.convertStringToLocalDateTime(event.getStartDate()).toLocalDate().equals(day.toLocalDate())
                        && !event.isHasTemp()) {

                    if (eventCount < MAX_EVENTS) {
                        float eventY = EVENT_START_Y + (eventCount * (EVENT_HEIGHT + EVENT_MARGIN));
                        RectF eventRect = new RectF(EVENT_START_X, eventY, getWidth(), eventY + EVENT_HEIGHT);
                        headerPaint.setColor(getResources().getColor(R.color.pal_blue));


                            eventsOfWeekPaint.setColor(Color.WHITE);

                        canvas.drawRoundRect(eventRect, dpToPx(6), dpToPx(6), headerPaint);
                        String eventTitle = event.getDescr();
                        float textX = EVENT_START_X + dpToPx(10);
                        float textY;
                        if (weAreInTablet) {
                            textY = (eventY + (EVENT_HEIGHT / 2)) + dpToPx(5);
                        } else {
                            textY = (eventY + (EVENT_HEIGHT / 2)) + dpToPx(3);
                        }

                        canvas.drawText(eventTitle, textX - dpToPx(5), textY, eventsOfWeekPaint);

                        EventCoordinates eventCoordinates = new EventCoordinates(EVENT_START_X, getWidth(), eventY, eventY + EVENT_HEIGHT, event);

                        touchEventsDaysOfWeeks.add(eventCoordinates);

                        eventCount++;
                    } else {
                        remainingEvents++;
                    }

                }
            }

            // Draw remaining events indicator if there are more events than MAX_EVENTS
            if (remainingEvents > 0) {
                String remainingText = "+" + remainingEvents;
                float remainingTextX = EVENT_START_X + dpToPx(10);
                float remainingTextY = EVENT_START_Y + (MAX_EVENTS * (EVENT_HEIGHT + EVENT_MARGIN)) + (EVENT_HEIGHT / 2);

                if (weAreInTablet) {
                    RectF remainingRect = new RectF(EVENT_START_X, remainingTextY - (EVENT_HEIGHT / 2), EVENT_START_X + dpToPx(50), remainingTextY + (EVENT_HEIGHT / 2));
                    canvas.drawRoundRect(remainingRect, dpToPx(8), dpToPx(8), daysOfWeekOfMonthPaint);
                    canvas.drawText(remainingText, remainingTextX, remainingTextY + dpToPx(5), remainingEventsPaint);
                } else {
                    RectF remainingRect = new RectF(EVENT_START_X, remainingTextY - (EVENT_HEIGHT / 2), EVENT_START_X + dpToPx(20), remainingTextY + (EVENT_HEIGHT / 2));
                    canvas.drawRoundRect(remainingRect, dpToPx(8), dpToPx(8), daysOfWeekOfMonthPaint);
                    canvas.drawText(remainingText, remainingTextX - dpToPx(5), remainingTextY + dpToPx(3), remainingEventsPaint);
                }

            }
        }

        private void setDayOFWeek(LocalDateTime day, Canvas canvas) {
            float HEADER_TEXT_X;
            float HEADER_TEXT_Y;
            if (weAreInTablet) {
                HEADER_TEXT_X = dpToPx((int) 15f);
                HEADER_TEXT_Y = dpToPx((int) 40f);
            } else {
                HEADER_TEXT_X = dpToPx((int) 15f);
                HEADER_TEXT_Y = dpToPx((int) 25f);
            }


            String monday = getContext().getString(R.string.day_monday); // Replace with the actual string resource
            String tuesday = getContext().getString(R.string.day_tuesday); // Replace with the actual string resource
            String wednesday = getContext().getString(R.string.day_wednesday); // Replace with the actual string resource
            String thursday = getContext().getString(R.string.day_thursday); // Replace with the actual string resource
            String friday = getContext().getString(R.string.day_friday); // Replace with the actual string resource
            String saturday = getContext().getString(R.string.day_saturday); // Replace with the actual string resource
            String sunday = getContext().getString(R.string.day_sunday); // Replace with the actual string resource

            switch (day.getDayOfWeek()) {
                case MONDAY:
                    canvas.drawText(monday, HEADER_TEXT_X, HEADER_TEXT_Y, dayOfWeekOfMonthOfDayView);
                    break;
                case TUESDAY:
                    canvas.drawText(tuesday, HEADER_TEXT_X, HEADER_TEXT_Y, dayOfWeekOfMonthOfDayView);
                    break;
                case WEDNESDAY:
                    canvas.drawText(wednesday, HEADER_TEXT_X, HEADER_TEXT_Y, dayOfWeekOfMonthOfDayView);
                    break;
                case THURSDAY:
                    canvas.drawText(thursday, HEADER_TEXT_X, HEADER_TEXT_Y, dayOfWeekOfMonthOfDayView);
                    break;
                case FRIDAY:
                    canvas.drawText(friday, HEADER_TEXT_X, HEADER_TEXT_Y, dayOfWeekOfMonthOfDayView);
                    break;
                case SATURDAY:
                    canvas.drawText(saturday, HEADER_TEXT_X, HEADER_TEXT_Y, dayOfWeekOfMonthOfDayView);
                    break;
                case SUNDAY:
                    canvas.drawText(sunday, HEADER_TEXT_X, HEADER_TEXT_Y, dayOfWeekOfMonthOfDayView);
                    break;
            }
        }

        private CalendarEvent getSelectedCalendarEventDaysOfWeek(float x, float y) {
            for (int i = 0; i < touchEventsDaysOfWeeks.size(); i++) {
                if ((x >= touchEventsDaysOfWeeks.get(i).getStartX() && x <= touchEventsDaysOfWeeks.get(i).getEndX()) &&
                        (y >= touchEventsDaysOfWeeks.get(i).getStartY() && y <= touchEventsDaysOfWeeks.get(i).getEndY())) {
                    return touchEventsDaysOfWeeks.get(i).getCalendarEvent();
                }
            }
            return null;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (Objects.equals(modeCalendar, WEEK_VIEW)) {
                float x = event.getX();
                float y = event.getY();
                int action = event.getAction();
                // Flag to track if any event is handled

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (weAreInTablet) {
                            boolean eventHandled = false;

                            // Check if the touch event occurs within the bounds of any day's block
                            for (int i = 0; i < touchEventsDaysOfWeeks.size(); i++) {
                                if ((x >= touchEventsDaysOfWeeks.get(i).getStartX() && x <= touchEventsDaysOfWeeks.get(i).getEndX()) &&
                                        (y >= touchEventsDaysOfWeeks.get(i).getStartY() && y <= touchEventsDaysOfWeeks.get(i).getEndY())) {


                                    tempCalendarEvent = getSelectedCalendarEventDaysOfWeek(x, y);
                                    eventHandled = true; // Mark the event as handled
                                    break; // Exit the loop since the event is handled
                                } else {
                                    tempCalendarEvent = null;
                                }
                            }
                            // If no event is handled, return false
                            if (!eventHandled) {
                                // Check if the touch event occurs within the bounds of any day's block
                                selectedDate = getSelectedDateWeek(x, y);

                                break;
                            }

                            break;
                        } else {
                            // Check if the touch event occurs within the bounds of any day's block
                            selectedDate = getSelectedDateWeek(x, y);

                            break;

                        }


                    case MotionEvent.ACTION_UP:
                        modeCalendar = DAY_VIEW;
                        // Perform action for the selected date if a valid selection was made
                        if (weAreInTablet) {
                            if (!(tempCalendarEvent == null)) {
                                if (selectedDate != null) {
                                    performClick();
                                }
                            } else {
                                if (selectedDate != null) {
                                    day = selectedDate.atStartOfDay();
                                    dayClickedFromMonthForNewEvent = CalendarUtils.convertLocalDateTimeToDate(day);
                                    CalendarViews.dateForViewPagerRegister=null;
                                    handleDaySelection();
                                }
                            }

                        } else {
                            if (selectedDate != null) {
                                day = selectedDate.atStartOfDay();
                                dayClickedFromMonthForNewEvent = CalendarUtils.convertLocalDateTimeToDate(day);
                                CalendarViews.dateForViewPagerRegister=null;
                                handleDaySelection();
                            }
                        }

                        break;
                    default:
                        // For other touch actions, delegate to super implementation
                        return super.onTouchEvent(event);

                }
                return true; // Consume the event
            } else if (Objects.equals(modeCalendar, DAY_VIEW)) {


                float x = event.getX();
                float y = event.getY();
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        boolean eventHandled = false; // Flag to track if any event is handled

                        // Check if the touch event occurs within the bounds of any day's block
                        for (int i = 0; i < touchEventsDaysOfWeeks.size(); i++) {
                            if ((x >= touchEventsDaysOfWeeks.get(i).getStartX() && x <= touchEventsDaysOfWeeks.get(i).getEndX()) &&
                                    (y >= touchEventsDaysOfWeeks.get(i).getStartY() && y <= touchEventsDaysOfWeeks.get(i).getEndY())) {


                                tempCalendarEvent = getSelectedCalendarEventDaysOfWeek(x, y);
                                eventHandled = true; // Mark the event as handled
                                break; // Exit the loop since the event is handled
                            }
                        }
                        // If no event is handled, return false
                        if (!eventHandled) {
                            return false;
                        }


                        break;
                    case MotionEvent.ACTION_UP:
                        // Perform action for the selected date if a valid selection was made
                        if (selectedDate != null) {
                            performClick();
                        }
                        break;
                    default:
                        // For other touch actions, delegate to super implementation
                        return super.onTouchEvent(event);
                }
                return true; // Consume the event
            } else {
                return false;
            }
        }

        @Override
        public boolean performClick() {

            MainActivity.SHOW_MODE = true;
            MainActivity.EDIT_MODE =false;
            MainActivity.NEW_MODE = false;
            MainActivity.listViewShownFromMenu = false;
            for (int i = 0; i < calendarEvents.size(); i++) {
                if (Objects.equals(calendarEvents.get(i).getId(), tempCalendarEvent.getId()) && !tempCalendarEvent.isTemp()) {
                    MainActivity.position = i;
                    EventActivity.eventId = calendarEvents.get(i).getId();
                    MainActivity.listViewShownFromDayView = true;

                }

                if (tempCalendarEvent.isTemp()) {
                    for (int j = 0; j < calendarEvents.size(); j++) {
                        if (Objects.equals(calendarEvents.get(i).getId(), tempCalendarEvent.getId())) {
                            MainActivity.position = i;
                            EventActivity.eventId = calendarEvents.get(i).getId();
                            MainActivity.listViewShownFromDayView = true;
                        }
                    }
                }

            }



            Intent i = new Intent(getContext(), EventActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line to set the FLAG_ACTIVITY_NEW_TASK flag
            getContext().startActivity(i);
            return super.performClick();


        }

        private void handleDaySelection() {
            // Perform actions for the selected date, such as displaying additional information
            // You can implement custom logic based on the selected date

            int counter = 0;
            CalendarEvent calendarEvent = new CalendarEvent();
            for (CalendarEvent event : calendarEvents) {
                if (CalendarUtils.convertStringToLocalDateTime(event.getStartDate()).toLocalDate().equals(selectedDate)) {
                    calendarEvent = event;
                    counter++;
                }
            }
            for (CalendarEvent event : MainActivity.TempEvents) {
                if (CalendarUtils.convertStringToLocalDateTime(event.getStartDate()).toLocalDate().equals(selectedDate)) {
                    calendarEvent = event;
                    counter++;
                }
            }
            if (counter == 1) {
                EventActivity.eventId = calendarEvent.getId();
                MainActivity.weekViewCellClicked = false;
                MainActivity.SHOW_MODE=true;
                MainActivity.EDIT_MODE=false;
                MainActivity.NEW_MODE=false;
                Intent i = new Intent(getContext(), EventActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line to set the FLAG_ACTIVITY_NEW_TASK flag
                getContext().startActivity(i);
            } else {
                CalendarFormats.reloadActivity(MainActivity.myActivity);
            }

        }

        private LocalDate getSelectedDateWeek(float x, float y) {
            // Calculate the width of each day's block
            int totalBlocks = weekList.size() + 1;
            int dayBlockWidth = getWidth() / totalBlocks;

            // Calculate the space between each block
            int spaceWidth = (getWidth() - (totalBlocks * dayBlockWidth)) / (totalBlocks - 1);

            // Determine the index of the day block based on the touch event coordinates
            int selectedDayIndex;
            if (weAreInTablet) {
                selectedDayIndex = (int) ((x - 80) / (dayBlockWidth + spaceWidth));
            } else {
                selectedDayIndex = (int) ((x - 150) / (dayBlockWidth + spaceWidth));
            }

            // Check if the selected index is within the bounds of the weekList
            if (weAreInTablet) {
                if (selectedDayIndex >= 0 && selectedDayIndex < weekList.size() && (x > 125 && y <= extraDimenHeightCalendarView)) {
                    // Return the corresponding date from the weekList
                    return weekList.get(selectedDayIndex).toLocalDate();
                }
            } else {
                if (selectedDayIndex >= 0 && selectedDayIndex < weekList.size() && (x > 170 && y <= extraDimenHeightCalendarView)) {
                    // Return the corresponding date from the weekList
                    return weekList.get(selectedDayIndex).toLocalDate();
                }
            }
            return null; // Return null if no date is selected
        }

    }

    public static class StableHours extends View {
        private static final int TEXT_SIZE = 31;
        private static final int PADDING_LEFT = 10;

        private Paint stablePaint;

        public StableHours(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);

            initialize();
        }

        private void initialize() {
            Typeface appTypeface = getTypeFaceFont(getContext());
            stablePaint = createTextPaint(R.color.pal_blue);
            stablePaint.setAntiAlias(true);
            stablePaint.setDither(true);
            if (appTypeface != null) {
                stablePaint.setTypeface(appTypeface);
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                stablePaint.setTextSize(getResources().getDimension(R.dimen.text_size_title) );
            } else {
                stablePaint.setTextSize(getResources().getDimension(R.dimen.text_size_title)  - 10);
            }


        }

        private Paint createTextPaint(int color) {
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(color));
            paint.setTextSize((float) TEXT_SIZE);
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setTextAlign(Paint.Align.LEFT);
            // You can add more properties based on your design needs
            return paint;
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            initialize();
            drawStableHours(canvas);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int weekDayHeight;
            int dayHeight;
            if (weAreInTablet) {
                weekDayHeight = dpToPx(2000);  // Example height for tablets in week view
                dayHeight = dpToPx(2000);

            } else {
                weekDayHeight = dpToPx(1500);  // Example height for phones in week view
                dayHeight = dpToPx(1500);

            }
            switch (modeCalendar) {

                case 1:
                    if (weAreInTablet) {
                        setMeasuredDimension(dpToPx(70), weekDayHeight);
                    } else {
                        setMeasuredDimension(dpToPx(45), weekDayHeight);
                    }
                    break;
                case 2:
                    if (weAreInTablet) {
                        setMeasuredDimension(dpToPx(70), dayHeight);
                    } else {

                        setMeasuredDimension(dpToPx((int) 50f), dayHeight);
                    }
                    break;
            }

        }

        private void drawStableHours(Canvas canvas) {

            int viewHeight = getHeight();
            int time = 1;

            // Draw horizontal lines for each hour
            for (int i = 1; i <= HOURS_IN_DAY; i++) {
                float y = i * (viewHeight / HOURS_IN_DAY);

                String hourLabel = CalendarUtils.getFormattedTimeStableHours(time);

                // Draw hour labels on the left side
                if (!hourLabel.isEmpty()) {
                    canvas.drawText(hourLabel.toLowerCase(Locale.ROOT), PADDING_LEFT, y, stablePaint);
                }
                time++;
            }
        }

        private int dpToPx(int dp) {
            float density = getResources().getDisplayMetrics().density;
            return Math.round(dp * density);
        }
    }

}
