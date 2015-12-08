package com.chacostak.salim.classexpress.Data_Base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chacostak.salim.classexpress.Fragment_home;

/**
 * Created by Salim on 26/04/2015.
 */
public class DB_Exams_Manager {

    public final String TABLE = "Exams";
    public final String COURSE = "Signature";
    public final String ROOM = "Room";
    public final String DAY_LIMIT = "Day_limit";
    public final String TIME_LIMIT = "End_hour";

    public final String CREATE_TABLE = "create table "+TABLE+"("+ COURSE +" text not null,"
            + ROOM +" text,"
            + DAY_LIMIT +" text not null,"
            + TIME_LIMIT +" text not null);";

    private DB_Helper helper;
    private SQLiteDatabase db;

    public DB_Exams_Manager(){
    }

    public DB_Exams_Manager(Context activity, String DB_Name, int DB_Version){
        helper = new DB_Helper(activity,DB_Name,DB_Version);
        db = helper.getWritableDatabase();
    }

    public void insert(String xid_class, String xroom, String xday_limit, String xhour_limit){
        try {
            db.insert(TABLE, null, generateContentValues(xid_class, xroom, xday_limit, xhour_limit));
        }catch (SQLException e){
            Log.d("Exception:", e.toString());
        }
    }

    public void update(String targetDay, String targetTime, String newSignature, String new_room, String new_day_limit, String new_time_limit){
        try {
            db.update(TABLE, generateContentValues(newSignature, new_room, new_day_limit, new_time_limit),
                    DAY_LIMIT + "= ? AND " + TIME_LIMIT + "=?", new String[]{targetDay, targetTime});
        }catch (SQLException e){
            Log.d("Exception:",e.toString());
        }
    }

    public void updateSignature(String targetSignature, String new_signature){
        db.update(TABLE, generateContentValues(new_signature),
                COURSE + "= ?", new String[]{targetSignature});
    }
    private ContentValues generateContentValues(String xid_class){
        ContentValues content = new ContentValues();
        content.put(COURSE, xid_class);
        return content;
    }

    private ContentValues generateContentValues(String xid_class, String xroom, String xday_limit, String xhour_limit){
        ContentValues content = new ContentValues();
        content.put(COURSE,xid_class);
        content.put(ROOM,xroom);
        content.put(DAY_LIMIT,xday_limit);
        content.put(TIME_LIMIT, xhour_limit);
        return content;
    }

    public void delete(String targetDay, String targetTime){
        db.delete(TABLE, DAY_LIMIT + "= ? AND " + TIME_LIMIT + "=?", new String[]{targetDay, targetTime});
    }

    public void deleteByCourse(String targetName){
        db.delete(TABLE, COURSE + "=?", new String[]{targetName});
    }

    public Cursor searchByCourse(String targetName){
        return db.query(TABLE,new String[]{COURSE, ROOM, DAY_LIMIT, TIME_LIMIT}, COURSE +"=?",
                new String[]{targetName},null,null,null);
    }

    public Cursor search(String targetDay, String targetTime){
        return db.query(TABLE,new String[]{COURSE, ROOM,DAY_LIMIT, TIME_LIMIT}, DAY_LIMIT + "= ? AND " + TIME_LIMIT + "=?",
                new String[]{targetDay, targetTime},null,null,null);
    }

    public Cursor getAll(){
        return db.query(TABLE,new String[]{COURSE, ROOM,DAY_LIMIT, TIME_LIMIT},null,null,null,null,null);
    }

    public Cursor getAllDateAndTime(){
        return db.query(TABLE,new String[]{DAY_LIMIT, TIME_LIMIT},null,null,null,null,null);
    }
}
