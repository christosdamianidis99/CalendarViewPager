package com.example.calendarviewpager;

public class StringForWeekHeader {
    private String title;
    private CalendarEvent colorEvent;

    public StringForWeekHeader(String title, CalendarEvent colorEvent) {
        this.title = title;
        this.colorEvent = colorEvent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CalendarEvent getColorEvent() {
        return colorEvent;
    }

    public void setColorEvent(CalendarEvent colorEvent) {
        this.colorEvent = colorEvent;
    }

    @Override
    public String toString() {
        return "StringForWeekHeader{" +
                "title='" + title + '\'' +
                ", colorEvent=" + colorEvent +
                '}';
    }
}
