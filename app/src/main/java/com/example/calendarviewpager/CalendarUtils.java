package com.example.calendarviewpager;


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
    public static final String DECIMAL_FORMAT_FOR_ZERO_DECS = "###,###,###,###,##0";

    public static final String DECIMAL_FORMAT = "###,###,###,###,##0.";
    public static LocalDate selectedDate = LocalDate.now();

    public static Calendar getCalendarFromString(String dateString) {
        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm");
        Calendar calendar = Calendar.getInstance();

        try {
            // Parse the date String to a Date object
            calendar.setTime(dateFormat.parse(dateString));
            return calendar; // Return the populated Calendar object
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }

    public static String generateNumberOfDecimalPlaces(int decimalPlaces){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < decimalPlaces; i++) {
            s.append("0");
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


    public static LocalDateTime convertStringToLocalDateTime(String dateString) {
        // Define the pattern of the input date string
        String pattern = "dd-MM-yyyy'T'HH:mm";

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
        for (CalendarEvent event : calendarEvents) {
            if (event.isHasTemp()) {
                LocalDateTime startDate = CalendarUtils.convertStringToLocalDateTime(event.getStartDate());
                LocalDateTime endDate = CalendarUtils.convertStringToLocalDateTime(event.getEndDate());

                LocalDateTime startTime = CalendarUtils.convertStringToLocalDateTime(event.getStartTime());
                LocalDateTime endTime = CalendarUtils.convertStringToLocalDateTime(event.getEndTime());

                long daysDifference = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());

                if (daysDifference >= 1) {
                    // Create event for the first day
                    CalendarEvent tempEvent1 = new CalendarEvent();
                    tempEvent1.setId(event.getId());
                    tempEvent1.setDescr(event.getDescr());
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

                        allDayTemp.setId(event.getId());
                        allDayTemp.setDescr(event.getDescr());
                        allDayTemp.setStartDate(convertLocalDateTimeToString(middleDay));
                        allDayTemp.setStartTime(convertLocalDateTimeToString(middleDay));
                        allDayTemp.setEndDate(convertLocalDateTimeToString(middleDay));
                        allDayTemp.setEndTime(convertLocalDateTimeToString(middleDay.toLocalDate().atTime(23, 59, 0)));
                        tempEvents.add(allDayTemp);
                    }

                    // Create event for the last day
                    CalendarEvent tempEvent2 = new CalendarEvent();
                    tempEvent2.setId(event.getId());
                    tempEvent2.setDescr(event.getDescr());
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
        String pattern = "dd-MM-yyyy'T'HH:mm";

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