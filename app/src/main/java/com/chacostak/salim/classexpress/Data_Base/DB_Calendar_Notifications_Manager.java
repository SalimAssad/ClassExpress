package com.chacostak.salim.classexpress.Data_Base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Salim on 16/06/2015.
 */
public class DB_Calendar_Notifications_Manager {

    public final String TABLE = "Calendar_Notification";
    public final String TAG = "Tag";
    public final String TIME = "Time";
    public final String DATE = "Date";
    public final String TYPE = "Type";

    public final String CREATE_TABLE = "create table "+TABLE+"("+TAG+" text not null,"
            + DATE +" text not null,"
            + TIME +" text not null);";

    private DB_Helper helper;
    private SQLiteDatabase db;

    public static final String HOMEWORK = "HOMEWORK";
    public static final String EXAM = "EXAM";
    public static final String GENERIC = "GENERIC";

    public DB_Calendar_Notifications_Manager(){
    }

    public DB_Calendar_Notifications_Manager(Context activity, String DB_Name, int DB_Version){
        helper = new DB_Helper(activity,DB_Name,DB_Version);
        db = helper.getWritableDatabase();
    }

    public void insert(String tag, String xdate, String xtime, String xtype){
        try {
            db.insert(TABLE, null, generateContentValues(tag, xdate, xtime, xtype));
        }catch (SQLException e){
            Log.d("Exception:", e.toString());
        }
    }

    public void updateTag(String old_tag, String new_tag){
        try {
            db.update(TABLE, generateContentValues(new_tag),
                    TAG + "=?", new String[]{old_tag});
        }catch (SQLException e){
            Log.d("Exception:",e.toString());
        }
    }

    private ContentValues generateContentValues(String xtag, String xdate, String xtime, String xtype){
        ContentValues content = new ContentValues();
        content.put(TAG, xtag);
        content.put(DATE, xdate);
        content.put(TIME, xtime);
        content.put(TYPE, xtype);
        return content;
    }

    private ContentValues generateContentValues(String xtag){
        ContentValues content = new ContentValues();
        content.put(TAG,xtag);
        return content;
    }

    public void deleteByTag(String targetTag){
        db.delete(TABLE, TAG + "=?", new String[]{targetTag});
    }

    public void delete(String targetTag, String xdate, String xtime, String xtype){
        db.delete(TABLE, TAG + "=? AND " + DATE + "=? AND " + TIME + "=? AND " + TYPE + "=?",
                new String[]{targetTag, xdate, xtime, xtype});
    }

    public void delete(String xdate, String xtime, String xtype){
        db.delete(TABLE, DATE + "=? AND " + TIME + "=? AND " + TYPE + "=?",
                new String[]{xdate, xtime, xtype});
    }

    public Cursor searchByTag(String targetTag){
        return db.query(TABLE,new String[]{TAG, TIME, DATE, TYPE}, TAG +"=?",
                new String[]{targetTag},null,null,null);
    }

    public Cursor search(String targetTag, String xdate, String xtime){
        return db.query(TABLE,new String[]{TAG, DATE, TIME}, TAG + "=? AND " + DATE + "=? AND " + TIME + "=?",
                new String[]{targetTag, String.valueOf(xdate), xtime},null,null,null);
    }

    public Cursor getAll(){
        return db.query(TABLE, new String[]{TAG, DATE, TIME, TYPE}, null, null, null, null, null);
    }
}
