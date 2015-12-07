package com.chacostak.salim.classexpress.Data_Base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chacostak.salim.classexpress.Fragment_home;

/**
 * Created by Salim on 16/06/2015.
 */
public class DB_Notifications_Manager {

    private final String TABLE = "Notification";
    public final String TAG = "Tag";
    public final String TIME_BEFORE = "Time_before";
    public final String UNIT_TYPE = "Priority";

    public final String CREATE_TABLE = "create table "+TABLE+"("+TAG +" text not null,"
            + TIME_BEFORE +" integer not null,"
            + UNIT_TYPE +" text not null);";

    private DB_Helper helper;
    private SQLiteDatabase db;

    public static final String COURSE_TAG = "COURSE";
    public static final String EXAM_TAG = "EXAM";

    public DB_Notifications_Manager(){
    }

    public DB_Notifications_Manager(Context activity, String DB_Name, int DB_Version){
        helper = new DB_Helper(activity,DB_Name,DB_Version);
        db = helper.getWritableDatabase();
    }

    public void insert(String tag, int time_before, String unit){
        try {
            db.insert(TABLE, null, generateContentValues(tag, time_before, unit));
        }catch (SQLException e){
            Log.d("Exception:", e.toString());
        }
    }

    public void update(String old_tag, int old_time_before, String old_unit, String new_tag, int new_time_before, String new_unit){
        try {
            db.update(TABLE, generateContentValues(new_tag, new_time_before, new_unit),
                    TAG + "= ? AND " + TIME_BEFORE + "= ? AND " + UNIT_TYPE + "= ?", new String[]{old_tag, String.valueOf(old_time_before), old_unit});
        }catch (SQLException e){
            Log.d("Exception:",e.toString());
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

    private ContentValues generateContentValues(String xtag, int xtime_before, String xunit_type){
        ContentValues content = new ContentValues();
        content.put(TAG,xtag);
        content.put(TIME_BEFORE, xtime_before);
        content.put(UNIT_TYPE, xunit_type);
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

    public void delete(String targetTag, int xtime_before, String xunit_type){
        db.delete(TABLE, TAG + "=? AND " + TIME_BEFORE + "=? AND " + UNIT_TYPE + "=?",
                new String[]{targetTag, String.valueOf(xtime_before), xunit_type});
    }

    public Cursor searchByTag(String targetTag){
        return db.query(TABLE,new String[]{TAG, TIME_BEFORE, UNIT_TYPE}, TAG +"=?",
                new String[]{targetTag},null,null,null);
    }

    public Cursor search(String targetTag, int time_before, String unit_type){
        return db.query(TABLE,new String[]{TAG, TIME_BEFORE, UNIT_TYPE}, TAG + "=? AND " + TIME_BEFORE + "=? AND " + UNIT_TYPE + "=?",
                new String[]{targetTag, String.valueOf(time_before), unit_type},null,null,null);
    }
}
