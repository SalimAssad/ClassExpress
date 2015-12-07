package com.chacostak.salim.classexpress.Data_Base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Salim on 05/04/2015.
 */
public class DB_Teacher_Manager {

    private final String TABLE = "Teacher";
    public final String NAME = "Name";
    public final String EMAIL = "Email";
    public final String WEB_PAGE = "Web_page";
    public final String PHONE = "Phone";

    public final String CREATE_TABLE = "create table "+TABLE+"("+ NAME +" text not null,"
            + EMAIL +" text,"
            + WEB_PAGE +" text,"
            + PHONE +" text);";

    private DB_Helper helper;
    private SQLiteDatabase db;

    Context activity;

    public DB_Teacher_Manager(){
    }

    public DB_Teacher_Manager(Context xactivity, String DB_Name, int DB_Version){
        helper = new DB_Helper(xactivity,DB_Name,DB_Version);
        db = helper.getWritableDatabase();
        activity = xactivity;
    }

    public void insert(String xname, String xemail, String xweb_page, String xphone){
        try {
            db.insert(TABLE, null, generateContentValues(xname, xemail, xweb_page, xphone));
        }catch (SQLException e){
            Log.d("Exception:", e.toString());
        }
    }

    public void update(String targetName, String new_name, String new_email, String new_web_page, String new_phone){
        try {
            db.update(TABLE, generateContentValues(new_name, new_email, new_web_page, new_phone),
                    NAME + "= ?", new String[]{targetName});
            new DB_Courses_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version).updateTeacher(targetName, new_name);
        }catch (SQLException e){
            Log.d("Exception:",e.toString());
        }
    }

    private ContentValues generateContentValues(String xname, String xemail, String xweb_page, String xphone){
        ContentValues content = new ContentValues();
        content.put(NAME,xname);
        content.put(EMAIL,xemail);
        content.put(WEB_PAGE,xweb_page);
        content.put(PHONE, xphone);
        return content;
    }

    public void deleteByName(String targetName){
        db.delete(TABLE, NAME +"=?", new String[]{targetName});
    }

    public void delete(String targetName, String day, int classroom, String xstart, String xend){
        db.delete(TABLE, NAME +"=? AND "+ EMAIL +"=? AND "+ WEB_PAGE +"=? AND "+ PHONE +"=?",
                new String[]{targetName, day, String.valueOf(classroom), xstart, xend});
    }


    public Cursor searchByName(String targetName){
        return db.query(TABLE,new String[]{NAME, EMAIL, WEB_PAGE, PHONE}, NAME +"=?",
                new String[]{targetName},null,null,null);
    }

    public Cursor getAll(){
        return db.query(TABLE,new String[]{NAME, EMAIL, WEB_PAGE, PHONE},null,null,null,null,null);
    }
}
