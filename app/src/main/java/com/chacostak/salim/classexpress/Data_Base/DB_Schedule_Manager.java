package com.chacostak.salim.classexpress.Data_Base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chacostak.salim.classexpress.Fragment_home;
import com.chacostak.salim.classexpress.Notifications.AlarmHandler;
import com.chacostak.salim.classexpress.Utilities.DateValidation;

import java.util.Calendar;

/**
 * Created by Salim on 25/01/2015.
 */
public class DB_Schedule_Manager {

    private final String TABLE = "Schedule";
    public final String COURSE = "_id";
    public final String DAY_OF_WEEK = "Day";
    public final String CLASSROOM = "Classroom";
    public final String START = "Start_hour";
    public final String END = "End_hour";

    public final String CREATE_TABLE = "create table "+TABLE+"("+ COURSE +" text not null,"
            +DAY_OF_WEEK+" text not null,"
            +CLASSROOM+" text,"
            +START+" text not null,"
            +END+" text not null);";

    private DB_Helper helper;
    private SQLiteDatabase db;
    private Context activity;

    public DB_Schedule_Manager(){
    }

    public DB_Schedule_Manager(Context xactivity, String DB_Name, int DB_Version){
        helper = new DB_Helper(xactivity,DB_Name,DB_Version);
        db = helper.getWritableDatabase();
        activity = xactivity;
    }

    public boolean insert(String xid_class, String xday, int xid_classroom, String xstart, String xend){
        try {
            Cursor cursor = searchByDay(xday);
            while(cursor.moveToNext()){
                if(cursor.getString(cursor.getColumnIndex(START)).equals(xstart) || cursor.getString(cursor.getColumnIndex(END)).equals(xend))
                    return false;
            }
            db.insert(TABLE, null, generateContentValues(xid_class, xday, xid_classroom, xstart, xend));
            cursor.close();

            resetAlarms();
            return true;
        }catch (SQLException e){
            Log.d("Exception:",e.toString());
            return false;
        }
    }

    public boolean update(String targetCourse, String targetDay, int targetClassroom, String targetStart, String targetEnd, String new_day, int new_id_classroom, String new_start, String new_end){
        try {
            Cursor cursor = searchByDay(new_day);
            while(cursor.moveToNext()){
                //If is the target register...
                if(targetCourse.equals(cursor.getString(cursor.getColumnIndex(COURSE))) && targetDay.equals(cursor.getString(cursor.getColumnIndex(DAY_OF_WEEK))) &&
                        targetStart.equals(cursor.getString(cursor.getColumnIndex(START))) && targetEnd.equals(cursor.getString(cursor.getColumnIndex(END))))
                    continue;//Skips the validation, so it doesn't return a false positive....
                if(cursor.getString(cursor.getColumnIndex(START)).equals(new_start) || cursor.getString(cursor.getColumnIndex(END)).equals(new_end))
                    return false;
            }
            db.update(TABLE, generateContentValues(new_day, new_id_classroom, new_start, new_end),
                    COURSE + "= ? AND " + DAY_OF_WEEK + "= ? AND " + CLASSROOM + "= ? AND " + START + "= ? AND " + END + "= ?",
                    new String[]{targetCourse, targetDay, String.valueOf(targetClassroom), targetStart, targetEnd});
            cursor.close();

            resetAlarms();
            return true;
        }catch (SQLException e){
            Log.d("Exception:",e.toString());
            return false;
        }
    }

    public void updateCourse(String targetCourse, String new_course){
        try {
            db.update(TABLE, generateContentValues(new_course),
                    COURSE + "= ?",
                    new String[]{targetCourse});

            resetAlarms();
        }catch (SQLException e){
            Log.d("Exception:",e.toString());
        }
    }

    private void resetAlarms() {
        AlarmHandler alarmHandler = new AlarmHandler();
        alarmHandler.setCourseAlarm(activity, Calendar.getInstance(), null, null, 0);
    }

    private ContentValues generateContentValues(String xid_class, String xday, int xid_classroom, String xstart, String xend){
        ContentValues content = new ContentValues();
        content.put(COURSE,xid_class);
        content.put(DAY_OF_WEEK,xday);
        content.put(CLASSROOM,xid_classroom);
        content.put(START,xstart);
        content.put(END, xend);
        return content;
    }

    private ContentValues generateContentValues(String xday, int xid_classroom, String xstart, String xend){
        ContentValues content = new ContentValues();
        content.put(DAY_OF_WEEK,xday);
        content.put(CLASSROOM,xid_classroom);
        content.put(START,xstart);
        content.put(END,xend);
        return content;
    }

    private ContentValues generateContentValues(String xcourse){
        ContentValues content = new ContentValues();
        content.put(COURSE, xcourse);
        return content;
    }

    public void deleteByCourse(String targetSig){
        db.delete(TABLE, COURSE + "=?", new String[]{targetSig});

        resetAlarms();
    }

    public void delete(String targetName, String day, int classroom, String xstart, String xend){
        db.delete(TABLE, COURSE +"=? AND "+DAY_OF_WEEK+"=? AND "+ CLASSROOM +"=? AND "+START+"=? AND "+END+"=?",
                new String[]{targetName, day, String.valueOf(classroom), xstart, xend});

        resetAlarms();
    }

    public Cursor searchByCourse(String targetName){
        return db.query(TABLE,new String[]{COURSE,DAY_OF_WEEK, CLASSROOM,START,END}, COURSE +"=?",
                new String[]{targetName},null,null,null);
    }

    public Cursor searchByDay(String targetDay){
        return db.query(TABLE,new String[]{COURSE,DAY_OF_WEEK,CLASSROOM,START,END}, DAY_OF_WEEK +"=?",
                new String[]{targetDay},null,null,null);
    }

    public Cursor getAll(){
        return db.query(TABLE,new String[]{COURSE,DAY_OF_WEEK, CLASSROOM,START,END},null,null,null,null,null);
    }

    public void closeDatabase(){
        db.close();
    }
}
