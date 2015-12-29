package com.chacostak.salim.classexpress.Data_Base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Salim on 31/03/2015.
 */
public class DB_Homework_Manager {
    
    private final String TABLE = "Homework";
    public final String COURSE = "_id";
    public final String TITLE = "Title";
    public final String DESCRIPTION = "Description";
    public final String DAY_LIMIT = "Day_limit";
    public final String TIME_LIMIT = "End_hour";
    public final String PRIORITY = "Priority";

    public final String CREATE_TABLE = "create table "+TABLE+"("+ COURSE +" text not null,"
            + TITLE +" text not null,"
            + DESCRIPTION +" text,"
            + DAY_LIMIT +" text not null,"
            + TIME_LIMIT +" text not null,"
            + PRIORITY +" integer);";

    private DB_Helper helper;
    private SQLiteDatabase db;

    Context activity;

    public DB_Homework_Manager(){
    }

    public DB_Homework_Manager(Context xactivity, String DB_Name, int DB_Version){
        helper = new DB_Helper(xactivity,DB_Name,DB_Version);
        db = helper.getWritableDatabase();
        activity = xactivity;
    }

    public void insert(String xid_class, String xtitle, String xdescription, String xday_limit, String xhour_limit, int xpriority){
        try {
            db.insert(TABLE, null, generateContentValues(xid_class, xtitle, xdescription, xday_limit, xhour_limit, xpriority));
        }catch (SQLException e){
            Log.d("Exception:", e.toString());
        }
    }

    public void update(String targetTitle, String newSignature, String new_title, String new_description, String new_day_limit, String new_time_limit, int new_priority){
        try {
            db.update(TABLE, generateContentValues(newSignature, new_title, new_description, new_day_limit, new_time_limit, new_priority),
                    TITLE + "= ?", new String[]{targetTitle});

            DB_Notifications_Manager notifications_manager = new DB_Notifications_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);
            DB_Calendar_Notifications_Manager calendar_notifications = new DB_Calendar_Notifications_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);

            notifications_manager.updateTag(targetTitle, new_title);
            calendar_notifications.updateTag(targetTitle, new_title);

            notifications_manager.closeDatabase();
            calendar_notifications.closeDatabase();

        }catch (SQLException e){
            Log.d("Exception:",e.toString());
        }
    }

    public void updateCourse(String targetSignature, String new_signature){
        db.update(TABLE, generateContentValues(new_signature),
                COURSE + "= ?", new String[]{targetSignature});
    }

    private ContentValues generateContentValues(String xid_class){
        ContentValues content = new ContentValues();
        content.put(COURSE, xid_class);
        return content;
    }

    private ContentValues generateContentValues(String xid_class, String xtitle, String xdescription, String xday_limit, String xhour_limit, int xpriority){
        ContentValues content = new ContentValues();
        content.put(COURSE,xid_class);
        content.put(TITLE,xtitle);
        content.put(DESCRIPTION, xdescription);
        content.put(DAY_LIMIT, xday_limit);
        content.put(TIME_LIMIT, xhour_limit);
        content.put(PRIORITY, xpriority);
        return content;
    }

    public void deleteByTitle(String targetName){
        db.delete(TABLE, TITLE + "=?", new String[]{targetName});

        DB_Notifications_Manager notifications_manager = new DB_Notifications_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        DB_Calendar_Notifications_Manager calendar_notifications = new DB_Calendar_Notifications_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);

        notifications_manager.deleteByTag(targetName);
        calendar_notifications.deleteByTag(targetName);

        notifications_manager.closeDatabase();
        calendar_notifications.closeDatabase();
    }

    public void deleteByCourse(String targetName){
        Cursor cursor = searchByCourse(targetName);
        DB_Notifications_Manager notifications_manager = new DB_Notifications_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        DB_Calendar_Notifications_Manager calendar_notifications = new DB_Calendar_Notifications_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        db.delete(TABLE, COURSE + "=?", new String[]{targetName});

        while(cursor.moveToNext()) {
            notifications_manager.deleteByTag(cursor.getString(cursor.getColumnIndex(TITLE)));
            calendar_notifications.deleteByTag(cursor.getString(cursor.getColumnIndex(TITLE)));
        }

        cursor.close();
        notifications_manager.closeDatabase();
        calendar_notifications.closeDatabase();
    }

    public Cursor searchByCourse(String targetName){
        return db.query(TABLE,new String[]{COURSE, TITLE, DESCRIPTION,DAY_LIMIT, TIME_LIMIT}, COURSE +"=?",
                new String[]{targetName},null,null,null);
    }

    public Cursor searchByTitle(String targetTitle){
        return db.query(TABLE,new String[]{COURSE, TITLE, DESCRIPTION,DAY_LIMIT, TIME_LIMIT, PRIORITY}, TITLE +"=?",
                new String[]{targetTitle},null,null,null);
    }

    public Cursor getAll(){
        return db.query(TABLE,new String[]{COURSE, TITLE, DESCRIPTION,DAY_LIMIT, TIME_LIMIT, PRIORITY},null,null,null,null,null);
    }

    public Cursor searchByMonth(String monthAbbreviation){
        monthAbbreviation = "%"+monthAbbreviation+"%";
        return db.query(TABLE, new String[]{TITLE, COURSE, DAY_LIMIT, TIME_LIMIT}, DAY_LIMIT + " LIKE ?", new String[]{monthAbbreviation}, null, null, null);
    }

    public Cursor searchByDayOfMonth(int day, String monthAbbreviation){
        String dayOfMonth = day+"/"+monthAbbreviation+"%";
        return db.query(TABLE, new String[]{TITLE, COURSE, DAY_LIMIT, TIME_LIMIT}, DAY_LIMIT + " LIKE ?", new String[]{dayOfMonth}, null, null, null);
    }

    public Cursor searchByDate(String date) {
        return db.query(TABLE, new String[]{TITLE, DESCRIPTION, COURSE, DAY_LIMIT, TIME_LIMIT}, DAY_LIMIT + " = ?", new String[]{date}, null, null, null);
    }

    public void closeDatabase(){
        db.close();
    }
}
