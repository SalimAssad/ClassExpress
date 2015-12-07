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
public class DB_Courses_Manager {

    final String TABLE = "Classes";
    public final String ID = "_id";
    public final String SIGNATURE = "Signature";
    public final String AVERAGE = "Average";
    public final String START = "Start_date";
    public final String END = "End_date";
    public final String TEACHER_NAME = "Teacher_name";
    public final String COLOR = "Color";

    public final String CREATE_TABLE = "create table "+TABLE+"("+ID+" integer primary key autoincrement,"
            + SIGNATURE +" text not null,"
            +AVERAGE+" real,"
            +START+" text not null,"
            +END+" text not null,"
            +TEACHER_NAME+" text);";

    private DB_Helper helper;
    private SQLiteDatabase db;

    Context activity;

    public DB_Courses_Manager() {

    }

    public DB_Courses_Manager(Context xactivity, String DB_Name, int DB_Version){
        helper = new DB_Helper(xactivity, DB_Name, DB_Version);
        db = helper.getWritableDatabase();

        activity = xactivity;
    }

    public void insert(String xsignature, double xaverage, String xstart, String xend, String xteacher_id, String xcolor){
        db.insert(TABLE,null,generateContentValues(xsignature,xaverage,xstart,xend,xteacher_id,xcolor));
    }

    public void update(String targetSignature, String new_signature, double new_average, String new_start, String new_end, String new_teacher_id, String xcolor){
        try {
            db.update(TABLE, generateContentValues(new_signature, new_average, new_start, new_end, new_teacher_id, xcolor),
                    SIGNATURE + "= ?", new String[]{targetSignature});
            new DB_Homework_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version).updateSignature(targetSignature, new_signature);
            new DB_Exams_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version).updateSignature(targetSignature, new_signature);
            new DB_Schedule_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version).updateCourse(targetSignature, new_signature);

            String day_name = new DateValidation(activity).getDayName(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
            Fragment_home.serviceManager.updateSignatureServiceData(day_name);
        }catch (SQLException e){
            Log.d("Exception:", e.toString());
        }
    }

    public void updateTeacher(String targetTeacher, String new_teacher){
        try {
            db.update(TABLE, generateContentValues(new_teacher),
                    TEACHER_NAME + "= ?", new String[]{targetTeacher});
        }catch (SQLException e){
            Log.d("Exception:", e.toString());
        }
    }

    private ContentValues generateContentValues(String xteacher){
        ContentValues content = new ContentValues();
        content.put(TEACHER_NAME,xteacher);
        return content;
    }

    private ContentValues generateContentValues(String xsignature, double xaverage, String xstart, String xend, String xteacher_id, String xcolor){
        ContentValues content = new ContentValues();
        content.put(SIGNATURE, xsignature);
        content.put(AVERAGE, xaverage);
        content.put(START, xstart);
        content.put(END, xend);
        content.put(TEACHER_NAME, xteacher_id);
        content.put(COLOR, xcolor);
        return content;
    }

    public void deleteCourse(String sigTarget){
        db.delete(TABLE, SIGNATURE + "=?", new String[]{sigTarget});
        new DB_Homework_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version).deleteBySignature(sigTarget);
        new DB_Schedule_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version).deleteBySignature(sigTarget);
        new DB_Exams_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version).deleteBySignature(sigTarget);

        String day_name = new DateValidation(activity).getDayName(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        Fragment_home.serviceManager.updateSignatureServiceData(day_name);
        Fragment_home.serviceManager.updateCountDownServiceData();
    }

    public Cursor searchByName(String targetName){
        return db.query(TABLE,new String[]{ID, SIGNATURE,AVERAGE,START,END, TEACHER_NAME,COLOR}, SIGNATURE +"=?",new String[]{targetName},null,null,null);
    }

    public Cursor getCourseColor(String targetName){
        return db.query(TABLE,new String[]{COLOR}, SIGNATURE +"=?",new String[]{targetName},null,null,null);
    }

    public Cursor getAll(){
        return db.query(TABLE,new String[]{ID, SIGNATURE,AVERAGE,START,END, TEACHER_NAME, COLOR},null,null,null,null,null);
    }

    public Cursor getTeacherAndColor(String storedSignature) {
        return db.query(TABLE,new String[]{TEACHER_NAME,COLOR}, SIGNATURE +"=?",new String[]{storedSignature},null,null,null);
    }
}
