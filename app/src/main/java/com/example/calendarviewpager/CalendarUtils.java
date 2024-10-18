package com.example.calendarviewpager;

import android.annotation.SuppressLint;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtils {
    public static final int DECS_FOR_CRM_DURATIONS = 2;
    public static final String DECIMAL_FORMAT_FOR_ZERO_DECS = "###,###,###,###,##0";

    public static final String DECIMAL_FORMAT = "###,###,###,###,##0.";
    public static final String SET_AN_INITIAL_DATE = "2000-01-01T00:00:00";
    public static final String DAY_PATTERN = "dd";
    public static final String MONTH_PATTERN = "MM";
    public static final String YEAR_PATTERN = "yyyy";
    public static final String EXTRA_FULL_DATE_PATTERN = "EEEE dd/MM/yyyy";
    public static final String FULL_DATE_PATTERN = "EE dd/MM/yyyy";
    public static final String FULL_DATE_PATTERN_2 = "EE dd-MM-yyyy";
    public static final String SHORT_DATE_PATTERN = "dd/MM/yyyy";
    public static final String VERY_SHORT_DATE_PATTERN = "dd/MM/yy";
    public static final String SHORT_DATE_TIME_PATTERN_WITH_ENTER = "dd/MM/yyyy\nHH:mm:ss";
    public static final String TIME_PATTERN = "HH:mm";
    public static final String INPUT_PATTERN_SHORT_DATE = "yyyy-MM-dd";
    public static final String INPUT_PATTERN_FULL_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String INPUT_PATTERN_FULL_DATE_WITH_ENTER = "yyyy-MM-dd\nHH:mm:ss";
    public static final String INPUT_PATTERN_FULL_DATE_FOR_COMPARISON = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FOR_UPLOAD = "yyyy-MM-dd";
    public static final String TIME_FOR_UPLOAD = "T00:00:00";
    public static LocalDate selectedDate = LocalDate.now();
    public static void formatDecimalNumberInTextView(TextView textView, int decs, BigDecimal number){
        textView.setText(decimals(decs).format(number));
    }
    public static DecimalFormat decimals(int decs){
        // Every time  a new DecimalFormatSymbols object is created and it is initializing with Locale.getDefault values
        // If the choice is eg 2 = 123456789.00 , formatSymbols have some properties, for unisoft formatSymbols.setDecimalSeparator('.');
        // But when user wants to change the choice, he goes out of the tabs, in slide menu. So the next time he comes to the tabs, the formatSymbols object
        // is initializing because it is a new object. And it is initializing with the Locale.getDefault values
        DecimalFormatSymbols formatSymbolsForAdapter = new DecimalFormatSymbols(Locale.getDefault());
        DecimalFormat df = new DecimalFormat();
        df = getDecimalFormat(decs, formatSymbolsForAdapter);
        return df;
    }
    public static String generateNumberOfDecimalPlaces(int decimalPlaces){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < decimalPlaces; i++) {
            s.append("0");  // in Decimal pattern '0' represents a digit even if it is not exist, and '#' represents a digit only if it is exists
            // here i want standar number of decimals so i put '0' in pattern , so if the number is 5 it becomes  for unisoft 5.00 if we have to decimalPlaces
        }
        return s.toString();
    }

    public static DecimalFormat getDecimalFormat(int decs, DecimalFormatSymbols formatSymbolsForAdapter){
        if (decs != 0) {
            return new DecimalFormat(DECIMAL_FORMAT + generateNumberOfDecimalPlaces(decs), formatSymbolsForAdapter);
        } else {  // if i don't have decimal places, i remove decimal seperator "." from the pattern
            return new DecimalFormat(DECIMAL_FORMAT_FOR_ZERO_DECS + generateNumberOfDecimalPlaces(decs), formatSymbolsForAdapter);
        }
    }
    // Methods for formatting and retrieving month and year information

    public static String monthYearFromDateViewPager(YearMonth yearMonth) {
        // Get the default locale of the device
        Locale deviceLocale = Locale.getDefault();

        // Check if the device locale is set to Greek
        if (yearMonth.getYear() == YearMonth.now().getYear())
        {
            if (deviceLocale.getLanguage().equals("el")) {
                // If the device language is Greek, use Greek locale
                Locale greekLocale = new Locale("el", "GR");

                // Convert LocalDate to Date
                Date utilDate = Date.from(yearMonth.atEndOfMonth().atStartOfDay().toInstant(ZoneOffset.UTC));

                // Format the Date to a String using Greek locale
                SimpleDateFormat sdf = new SimpleDateFormat("LLLL", greekLocale);
                return sdf.format(utilDate);
            } else {
                // If the device language is not Greek, use the default locale (which is typically English)
                // Convert LocalDate to Date
                Date utilDate = Date.from(yearMonth.atEndOfMonth().atStartOfDay().toInstant(ZoneOffset.UTC));

                // Format the Date to a String using the default locale
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM", deviceLocale);
                return sdf.format(utilDate);
            }
        }else
        {
            if (deviceLocale.getLanguage().equals("el")) {
                // If the device language is Greek, use Greek locale
                Locale greekLocale = new Locale("el", "GR");

                // Convert LocalDate to Date
                Date utilDate = Date.from(yearMonth.atEndOfMonth().atStartOfDay().toInstant(ZoneOffset.UTC));

                // Format the Date to a String using Greek locale
                SimpleDateFormat sdf = new SimpleDateFormat("LLLL yyyy", greekLocale);
                return sdf.format(utilDate);
            } else {
                // If the device language is not Greek, use the default locale (which is typically English)
                // Convert LocalDate to Date
                Date utilDate = Date.from(yearMonth.atEndOfMonth().atStartOfDay().toInstant(ZoneOffset.UTC));

                // Format the Date to a String using the default locale
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", deviceLocale);
                return sdf.format(utilDate);
            }
        }

    }


//    public static String monthYearFromDateViewPager(YearMonth yearMonth) {
//        Locale greekLocale = new Locale("el", "GR");
//
//        // Convert LocalDate to Date
//        Date utilDate = Date.from(yearMonth.atEndOfMonth().atStartOfDay().toInstant(ZoneOffset.UTC));
//
//        // Format the Date to a String
//        SimpleDateFormat sdf = new SimpleDateFormat("LLLL yyyy", greekLocale);
//        return sdf.format(utilDate);
//    }

    public static String localDateFromDateViewPager(LocalDate localDate) {
        // Convert LocalDate to Date
        Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        // Get the default locale of the device
        Locale deviceLocale = Locale.getDefault();
        if (localDate.getYear()== LocalDate.now().getYear())
        {
            // Check if the device locale is set to Greek
            if (deviceLocale.getLanguage().equals("el")) {
                // If the device language is Greek, use Greek locale
                Locale greekLocale = new Locale("el", "GR");
                SimpleDateFormat formatter = new SimpleDateFormat("LLLL", greekLocale);
                return formatter.format(date);
            } else {
                // If the device language is not Greek, use the default locale (which is typically English)
                SimpleDateFormat formatter = new SimpleDateFormat("MMMM", deviceLocale);
                return formatter.format(date);
            }
        }else
        {
            // Check if the device locale is set to Greek
            if (deviceLocale.getLanguage().equals("el")) {
                // If the device language is Greek, use Greek locale
                Locale greekLocale = new Locale("el", "GR");
                SimpleDateFormat formatter = new SimpleDateFormat("LLLL yyyy", greekLocale);
                return formatter.format(date);
            } else {
                // If the device language is not Greek, use the default locale (which is typically English)
                SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", deviceLocale);
                return formatter.format(date);
            }
        }

    }

    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        // Specify the zone
        ZoneId zoneId = ZoneId.systemDefault(); // You can specify your own zoneId

        // Convert LocalDateTime to ZonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        // Convert ZonedDateTime to Instant
        Instant instant = zonedDateTime.toInstant();

        // Convert Instant to Date
        return Date.from(instant);
    }

//    public static String localDateFromDateViewPager(LocalDate localDate) {
//        Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
//
//        Locale greekLocale = new Locale("el", "GR");
//        SimpleDateFormat formatter = new SimpleDateFormat("LLLL yyyy", greekLocale);
//        return formatter.format(date);
//    }

    public static boolean datesAreEqual(String date1, String date2){
        // convert the strings to Date format
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(INPUT_PATTERN_FULL_DATE_FOR_COMPARISON);
        Date firstDate = new Date();
        Date secondDate = new Date();
        try{
            firstDate = format.parse(date1);
            secondDate = format.parse(date2);
        }catch(ParseException e){
            e.printStackTrace();
        }

        assert secondDate != null;
        return secondDate.equals(firstDate);
    }
    public static String parseDateToAnOutputPattern(String time, String outputPattern, String inputPattern) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String replaceTLetterInDatesWithSpaceOrEnter(String string, boolean replaceWithSpace){
        // this because the date in Delphi has a format YYYY-MM-DDTHH:MM:SS
        // here i replace the "T" with space " " or with a new line
        return string.replace("T", replaceWithSpace ? " " : "\n");
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateString) {
        // Define the pattern of the input date string
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";

        // Create a DateTimeFormatter with the defined pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // Parse the input string into a LocalDateTime object

        return LocalDateTime.parse(dateString, formatter);
    }

    public static String extractDate(String dbDateTime) {
        // Split the string by "T" and return the first part (date)
        if (dbDateTime != null && dbDateTime.contains("T")) {
            return dbDateTime.split("T")[0];  // Returns the date part
        }
        return dbDateTime;  // Return original if it doesn't contain "T"
    }
    public static String extractTime(String dbDateTime) {
        // Split the string by "T" and return the second part (time)
        if (dbDateTime != null && dbDateTime.contains("T")) {
            return dbDateTime.split("T")[1];  // Returns the time part
        }
        return dbDateTime;  // Return original if it doesn't contain "T"
    }
    //-------------------------For ViewPagerMonth-------------------------


    public static ArrayList<YearMonth> monthsInOrder() {
        ArrayList<YearMonth> monthsInOrder = new ArrayList<>();
        YearMonth startDate = YearMonth.of(1950, Month.JANUARY);
        YearMonth endDate = YearMonth.of(2099, Month.DECEMBER);

        YearMonth currentMonth = startDate;

        while (!currentMonth.isAfter(endDate)) {
            monthsInOrder.add(currentMonth);
            currentMonth = currentMonth.plusMonths(1);
        }

        return monthsInOrder;
    }


    //-------------------------For CalendarAdapterMonth-------------------------


    public static ArrayList<LocalDateTime> daysInMonthArrayInOrder(YearMonth yearMonth) {
        ArrayList<LocalDateTime> daysInMonthArray = new ArrayList<>();
        int dayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue();

        LocalDateTime currentDate = yearMonth.atDay(1).minusDays(dayOfWeek - 1).atStartOfDay();

        for (int i = 1; i <= 42; i++) {
            daysInMonthArray.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return daysInMonthArray;
    }
    public static ArrayList<LocalDateTime> daysInMonthArrayInOrderSundayFirst(YearMonth yearMonth) {
        ArrayList<LocalDateTime> daysInMonthArray = new ArrayList<>();
        int dayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue();

        // Adjusting the start day to Sunday (1)
        int daysToSubtract = (dayOfWeek == DayOfWeek.SUNDAY.getValue()) ? 0 : (dayOfWeek - DayOfWeek.SUNDAY.getValue());

        LocalDateTime currentDate = yearMonth.atDay(1).minusDays(daysToSubtract).atStartOfDay();

        for (int i = 1; i <= 42; i++) {
            daysInMonthArray.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return daysInMonthArray;
    }




    //-------------------------For ViewPagerWeek-------------------------


    public static ArrayList<LocalDate> weeksStartingFromSunday() {
        ArrayList<LocalDate> weeksInOrder = new ArrayList<>();

        LocalDate startDate = LocalDate.of(1950, 1, 1);
        LocalDate endDate = LocalDate.of(2099, 12, 31);

        LocalDate currentWeekStart = startDate.with(DayOfWeek.SUNDAY);

        while (!currentWeekStart.isAfter(endDate)) {
            weeksInOrder.add(currentWeekStart);
            currentWeekStart = currentWeekStart.plusWeeks(1);
        }

        return weeksInOrder;
    }

    public static ArrayList<LocalDate> weeksStartingFromMonday() {
        ArrayList<LocalDate> weeksInOrder = new ArrayList<>();

        LocalDate startDate = LocalDate.of(1970, 1, 1);
        LocalDate endDate = LocalDate.of(2099, 12, 31);

        // Calculate the first Monday from the start date
        LocalDate currentWeekStart = startDate.with(DayOfWeek.MONDAY);

        while (!currentWeekStart.isAfter(endDate)) {
            weeksInOrder.add(currentWeekStart);
            currentWeekStart = currentWeekStart.plusWeeks(1);
        }

        return weeksInOrder;
    }



    //-------------------------For ViewPagerDay-------------------------

    public static ArrayList<LocalDate> daysInOrder() {
        ArrayList<LocalDate> daysInOrder = new ArrayList<>();

        LocalDate startDate = LocalDate.of(1950, 1, 1);
        LocalDate endDate = LocalDate.of(2099, 12, 31);

        LocalDate currentWeekStart = startDate.with(DayOfWeek.SUNDAY);

        while (!currentWeekStart.isAfter(endDate)) {
            daysInOrder.add(currentWeekStart);
            currentWeekStart = currentWeekStart.plusDays(1);
        }

        return daysInOrder;
    }


    public static ArrayList<LocalDate> daysInAllMonths() {
        ArrayList<LocalDate> daysInAllMonths = new ArrayList<>();
        LocalDate startDate = LocalDate.of(1950, 1, 1);  // Starting month
        LocalDate endDate = LocalDate.of(2099, 12, 31);  // Ending month

        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            daysInAllMonths.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return daysInAllMonths;
    }

    //  --------------------------------------------------------------------------------------------


    //-------------------------For StableHours-------------------------
    public static String getFormattedTimeStableHours(int hour) {
        if (hour > 0 && hour < 24) {
            // Get the default locale of the device
            Locale deviceLocale = Locale.getDefault();

            // Create a Calendar instance and set the hour
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // Check if the device locale is set to Greek
            if (deviceLocale.getLanguage().equals("el")) {
                // If the device language is Greek, use Greek locale for formatting
                Locale greekLocale = new Locale("el", "GR");
                if (CalendarViews.weAreInTablet) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", deviceLocale);
                    return dateFormat.format(calendar.getTime());
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("h a", greekLocale);
                return dateFormat.format(calendar.getTime());
            } else {
                // If the device language is not Greek, use the default locale (typically English) for formatting
                if (CalendarViews.weAreInTablet) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", deviceLocale);
                    return dateFormat.format(calendar.getTime());
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("h a", deviceLocale);
                return dateFormat.format(calendar.getTime());
            }
        } else {
            return "";
        }
    }

//    public static String getFormattedTimeStableHours(int hour) {
//        if (hour > 0 && hour < 24) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, hour);
//
//            return DateFormat.format("h a", calendar).toString();
//        } else {
//            return "";
//        }
//    }


    //-------------------------For CalendarAdapterWeek-------------------------
    public static ArrayList<LocalDateTime> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDateTime> days = new ArrayList<>();

        // Calculate the start date for the week starting from the previous Monday
        LocalDateTime weekStart = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() - 1).atStartOfDay();

        // Calculate the end date for the week ending on the following Sunday
        LocalDateTime weekEnd = weekStart.plusDays(6);

        LocalDateTime current = weekStart;

        while (!current.isAfter(weekEnd)) {
            days.add(current);
            current = current.plusDays(1);
        }

        return days;
    }

    public static ArrayList<LocalDate> daysInWeekArraySunday(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();

        // Calculate the start date for the week before the selected date
        LocalDate weekBeforeStart = selectedDate.minusDays(1).with(DayOfWeek.SUNDAY);

        // Calculate the end date for the week after the selected date
        LocalDate weekAfterEnd = selectedDate.plusDays(1).with(DayOfWeek.SATURDAY);

        LocalDate current = weekBeforeStart;

        while (!current.isAfter(weekAfterEnd)) {
            days.add(current);
            current = current.plusDays(1);
        }

        return days;
    }

    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = null;
        oneWeekAgo = current.minusWeeks(1);


        while (current.isAfter(oneWeekAgo)) {
            if (current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }


        return null;
    }

    public static ArrayList<CalendarEvent> tempEvents(ArrayList<CalendarEvent> calendarEvents) {
        ArrayList<CalendarEvent> tempEvents = new ArrayList<>();
        for (CalendarEvent journal : calendarEvents) {
            if (journal.isHasTemp()) {
                LocalDateTime startDate = CalendarUtils.convertStringToLocalDateTime(journal.getStartDate());
                LocalDateTime endDate = CalendarUtils.convertStringToLocalDateTime(journal.getEndDate());

                LocalDateTime startTime = CalendarUtils.convertStringToLocalDateTime(journal.getStartTime());
                LocalDateTime endTime = CalendarUtils.convertStringToLocalDateTime(journal.getEndTime());

                long daysDifference = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());

                if (daysDifference >= 1) {
                    // Create event for the first day
                    CalendarEvent tempEvent1 = new CalendarEvent();
                    tempEvent1.setId(journal.getId());
                    tempEvent1.setDescr(journal.getDescr());
                    tempEvent1.setTemp(true);
                    tempEvent1.setStartDate(convertLocalDateTimeToString(startDate));
                    tempEvent1.setStartTime(convertLocalDateTimeToString(startTime));
                    tempEvent1.setEndDate(convertLocalDateTimeToString(startDate));
                    tempEvent1.setEndTime(convertLocalDateTimeToString(startDate.toLocalDate().atTime(23, 59, 0)));
                    tempEvents.add(tempEvent1);

                    // Create events for the middle days
                    for (int j = 1; j < daysDifference; j++) {
                        LocalDateTime middleDay = startDate.plusDays(j).toLocalDate().atStartOfDay();
                        CalendarEvent allDayTemp = new CalendarEvent();
                        allDayTemp.setTemp(true);
                        allDayTemp.setHasTemp(false);

                        allDayTemp.setId(journal.getId());
                        allDayTemp.setDescr(journal.getDescr());
                        allDayTemp.setStartDate(convertLocalDateTimeToString(middleDay));
                        allDayTemp.setStartTime(convertLocalDateTimeToString(middleDay));
                        allDayTemp.setEndDate(convertLocalDateTimeToString(middleDay));
                        allDayTemp.setEndTime(convertLocalDateTimeToString(middleDay.toLocalDate().atTime(23, 59, 0)));
                        tempEvents.add(allDayTemp);
                    }

                    // Create event for the last day
                    CalendarEvent tempEvent2 = new CalendarEvent();
                    tempEvent2.setId(journal.getId());
                    tempEvent2.setDescr(journal.getDescr());
                    tempEvent2.setTemp(true);
                    tempEvent2.setStartDate(convertLocalDateTimeToString(endDate.toLocalDate().atStartOfDay()));
                    tempEvent2.setStartTime(convertLocalDateTimeToString(endDate.toLocalDate().atStartOfDay()));
                    tempEvent2.setEndDate(convertLocalDateTimeToString(endDate));
                    tempEvent2.setEndTime(convertLocalDateTimeToString(endTime));
                    tempEvents.add(tempEvent2);
                }
            }
        }
        return tempEvents;
    }


    public static void setIfAnEventHasTemps(ArrayList<CalendarEvent> calendarEvents) {
        for (int i = 0; i < calendarEvents.size(); i++) {
            long daysDifference = ChronoUnit.DAYS.between(CalendarUtils.convertStringToLocalDateTime(calendarEvents.get(i).getStartDate()), CalendarUtils.convertStringToLocalDateTime(calendarEvents.get(i).getEndDate()));

            if (daysDifference >= 1 && daysDifference<=3) {
                calendarEvents.get(i).setHasTemp(true);
            }
        }
    }

    public static String convertLocalDateTimeToString(LocalDateTime dateTime) {
        // Define the pattern of the desired output date string
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";

        // Create a DateTimeFormatter with the defined pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // Format the LocalDateTime object into a string using the formatter
        String formattedString = dateTime.format(formatter);

        return formattedString;
    }

    //-------------------------For Reminder-------------------------
    public static LocalDateTime convertCalendarToLocalDateTime(Calendar calendar) {
        // Convert Calendar to LocalDateTime
        return LocalDateTime.ofInstant(
                calendar.toInstant(),
                ZoneId.systemDefault()
        );
    }
}