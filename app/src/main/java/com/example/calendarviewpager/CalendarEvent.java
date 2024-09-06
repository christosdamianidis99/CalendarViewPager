package com.example.calendarviewpager;


public class CalendarEvent {
    private Integer id; // i set type to Integer (and not int), because of upload CrmJournal where id can be returned from Capital as null, for some reason

    private String descr;

    private String startDate;

    private String startTime;

    private String endDate;

    private String endTime;

    private Double duration;


    private String location;

    private Integer reminder;


    private String comment;

    // -----------------------------------------------------------------
    private boolean isTemp;
    private boolean hasTemp;


    public CalendarEvent(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getReminder() {
        return reminder;
    }

    public void setReminder(Integer reminder) {
        this.reminder = reminder;
    }



    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public boolean isHasTemp() {
        return hasTemp;
    }

    public void setHasTemp(boolean hasTemp) {
        this.hasTemp = hasTemp;
    }

    @Override
    public String toString() {
        return "CrmJournal{" +
                "id=" + id +
                ", descr='" + descr + '\'' +
                ", startDate='" + startDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endDate='" + endDate + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration=" + duration +
                ", location='" + location + '\'' +
                ", reminder=" + reminder +
                ", comment='" + comment + '\'' +
                ", isTemp=" + isTemp +
                ", hasTemp=" + hasTemp +
                '}';
    }
}
