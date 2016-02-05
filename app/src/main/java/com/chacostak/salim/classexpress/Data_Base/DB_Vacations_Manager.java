package com.chacostak.salim.classexpress.Data_Base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chacostak.salim.classexpress.Fragment_home;
import com.chacostak.salim.classexpress.Utilities.DateValidation;

import java.util.Calendar;

/**
 * Created by Salim on 25/01/2015.
 */
public class DB_Vacations_Manager {

    final String TABLE = "Vacation";
    public final String ID = "_id";
    public final String TITLE = "Title";
    public final String START = "Start_date";
    public final String END = "End_date";
    public final String YEARLY = "Yearly";

    public final String CREATE_TABLE = "create table "+TABLE+"("+ID+" integer primary key autoincrement,"
            + TITLE +" text not null,"
            +START+" text not null,"
            +END+" text not null,"
            +YEARLY+" integer not null);"; //1 = yes, 0 = no

    private DB_Helper helper;
    private SQLiteDatabase db;

    Context activity;

    public DB_Vacations_Manager() {

    }

    public DB_Vacations_Manager(Context xactivity, String DB_Name, int DB_Version){
        helper = new DB_Helper(xactivity, DB_Name, DB_Version);
        db = helper.getWritableDatabase();

        activity = xactivity;
    }

    public void insert(String xtitle, String xstart, String xend, int xyearly){
        db.insert(TABLE,null,generateContentValues(xtitle,xstart,xend,xyearly));
    }

    public void update(String targetTitle, String new_title, String new_start, String new_end, int new_yearly){
        try {
            db.update(TABLE, generateContentValues(new_title, new_start, new_end, new_yearly),
                    TITLE + "= ?", new String[]{targetTitle});
        }catch (SQLException e){
            Log.d("Exception:", e.toString());
        }
    }

    public void updateYearly(String targetTitle, String xstart, String xend, int xyearly){
        try {
            db.update(TABLE, generateContentValues(xstart, xend, xyearly),
                    TITLE + "= ?", new String[]{targetTitle});
        }catch (SQLException e){
            Log.d("Exception:", e.toString());
        }
    }

    private ContentValues generateContentValues(String xtitle, String xstart, String xend, int xyearly){
        ContentValues content = new ContentValues();
        content.put(TITLE, xtitle);
        content.put(START, xstart);
        content.put(END, xend);
        content.put(YEARLY, xyearly);
        return content;
    }

    private ContentValues generateContentValues(String xstart, String xend, int xyearly){
        ContentValues content = new ContentValues();
        content.put(START, xstart);
        content.put(END, xend);
        content.put(YEARLY, xyearly);
        return content;
    }

    public void delete(String targetName) {
        db.delete(TABLE, TITLE + "=?", new String[]{targetName});
    }

    public Cursor searchByTitle(String targetName){
        return db.query(TABLE,new String[]{ID, TITLE,START,END,YEARLY}, TITLE +"=?",new String[]{targetName},null,null,null);
    }

    public Cursor getAll(){
        return db.query(TABLE,new String[]{ID, TITLE,START,END,YEARLY},null,null,null,null,null);
    }

    public Cursor searchByMonth(String monthAbbreviation){
        monthAbbreviation = "%"+monthAbbreviation+"%";
        return db.query(TABLE, new String[]{TITLE, START, END, YEARLY}, START + " LIKE ?", new String[]{monthAbbreviation}, null, null, null);
    }

    public Cursor searchByDayOfMonth(int day, String monthAbbreviation){
        String dayOfMonth = day+"/"+monthAbbreviation+"%";
        return db.query(TABLE, new String[]{TITLE, START, END, YEARLY}, START + " LIKE ?", new String[]{dayOfMonth}, null, null, null);
    }

    public String getTitleByDate(String date) {
        String[] array = date.split("/");
        String title = null;
        Cursor cursor = db.query(TABLE, new String[]{TITLE, YEARLY, START}, START + " LIKE ? OR " + END + " LIKE ?", new String[]{array[0] + "/" + array[1], array[0]+"/"+array[1]}, null, null, null);

        while(cursor.moveToNext()){
            if(cursor.getInt(1) == 1){
                title = cursor.getString(0);
                break;
            }else{
                String year = cursor.getString(2).split("/")[2];
                if(year.equals(array[2])){
                    title = cursor.getString(0);
                    break;
                }
            }
        }
        return title;
    }

    public void closeDatabase(){
        db.close();
    }
}
