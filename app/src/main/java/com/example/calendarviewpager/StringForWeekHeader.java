package com.example.calendarviewpager;

public class StringForWeekHeader {
    private String title;
    private CalendarEvent colorJournal;

    public StringForWeekHeader(String title, CalendarEvent colorJournal) {
        this.title = title;
        this.colorJournal = colorJournal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CalendarEvent getColorJournal() {
        return colorJournal;
    }

    public void setColorJournal(CalendarEvent colorJournal) {
        this.colorJournal = colorJournal;
    }

    @Override
    public String toString() {
        return "StringForWeekHeader{" +
                "title='" + title + '\'' +
                ", colorJournal=" + colorJournal +
                '}';
    }
}
