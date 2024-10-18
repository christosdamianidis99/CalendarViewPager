package com.example.calendarviewpager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database and Table information
    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "events";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DESCR = "descr";
    private static final String COLUMN_START_DATE = "startDate";
    private static final String COLUMN_START_TIME = "startTime";
    private static final String COLUMN_END_DATE = "endDate";
    private static final String COLUMN_END_TIME = "endTime";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_REMINDER = "reminder";
    private static final String COLUMN_COMMENT = "comment";

    // SQL statement to create the events table
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DESCR + " TEXT, "
            + COLUMN_START_DATE + " TEXT, "
            + COLUMN_START_TIME + " TEXT, "
            + COLUMN_END_DATE + " TEXT, "
            + COLUMN_END_TIME + " TEXT, "
            + COLUMN_DURATION + " REAL, "
            + COLUMN_LOCATION + " TEXT, "
            + COLUMN_REMINDER + " INTEGER, "
            + COLUMN_COMMENT + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it exists and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new event
    public long insertEvent(CalendarEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DESCR, event.getDescr());
        values.put(COLUMN_START_DATE, event.getStartDate());
        values.put(COLUMN_START_TIME, event.getStartTime());
        values.put(COLUMN_END_DATE, event.getEndDate());
        values.put(COLUMN_END_TIME, event.getEndTime());
        values.put(COLUMN_DURATION, event.getDuration());
        values.put(COLUMN_LOCATION, event.getLocation());
        values.put(COLUMN_REMINDER, event.getReminder());
        values.put(COLUMN_COMMENT, event.getComment());

        // Insert the row
        long id = db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
        return id; // Return newly inserted row ID
    }

    // Fetch an event by ID
    public CalendarEvent getEventById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null, // All columns
                COLUMN_ID + "=?", // WHERE clause
                new String[]{String.valueOf(id)}, // WHERE arguments
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        CalendarEvent event = new CalendarEvent();
        event.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        event.setDescr(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCR)));
        event.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)));
        event.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
        event.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE)));
        event.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
        event.setDuration(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
        event.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
        event.setReminder(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMINDER)));
        event.setComment(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT)));

        cursor.close();
        return event;
    }
    public ArrayList<CalendarEvent> getAllCalendarEvents() {
        ArrayList<CalendarEvent> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to fetch all events from the table
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CalendarEvent event = new CalendarEvent();
                event.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                event.setDescr(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCR)));
                event.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)));
                event.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
                event.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE)));
                event.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
                event.setDuration(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
                event.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                event.setReminder(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMINDER)));
                event.setComment(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT)));

                // Add the event to the list
                eventList.add(event);
            } while (cursor.moveToNext());
        }

        // Close the cursor after usage
        cursor.close();
        db.close();

        return eventList;
    }
    // Fetch all events
    public Cursor getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(selectQuery, null);
    }

    // Update an existing event
    public int updateEvent(CalendarEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DESCR, event.getDescr());
        values.put(COLUMN_START_DATE, event.getStartDate());
        values.put(COLUMN_START_TIME, event.getStartTime());
        values.put(COLUMN_END_DATE, event.getEndDate());
        values.put(COLUMN_END_TIME, event.getEndTime());
        values.put(COLUMN_DURATION, event.getDuration());
        values.put(COLUMN_LOCATION, event.getLocation());
        values.put(COLUMN_REMINDER, event.getReminder());
        values.put(COLUMN_COMMENT, event.getComment());

        // Update the row
        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(event.getId())});
    }

    // Delete an event by ID
    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Delete all events
    public void deleteAllEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
