package com.chacostak.salim.classexpress.Data_Base;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Salim on 18/01/2015.
 */
public class DB_Helper extends SQLiteOpenHelper{

    public static String DB_Name = "Class scheduler";
    public static int DB_Version = 8;

    public DB_Helper(Context context, String name, int version) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(new DB_Courses_Manager().CREATE_TABLE);
            sqLiteDatabase.execSQL(new DB_Schedule_Manager().CREATE_TABLE);
            sqLiteDatabase.execSQL(new DB_Homework_Manager().CREATE_TABLE);
            sqLiteDatabase.execSQL(new DB_Teacher_Manager().CREATE_TABLE);
        }catch (SQLException e){
            Log.d("Exception",e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        for(; i < i2; i++){
            switch (i){
                case 1:
                    DB_Courses_Manager sig = new DB_Courses_Manager();
                    sqLiteDatabase.execSQL("ALTER TABLE "+sig.TABLE+" ADD COLUMN "+sig.COLOR+" TEXT");
                    break;
                case 2:
                    DB_Exams_Manager exams = new DB_Exams_Manager();
                    sqLiteDatabase.execSQL(exams.CREATE_TABLE);
                    break;
                case 4:
                    DB_Notifications_Manager notifications = new DB_Notifications_Manager();
                    sqLiteDatabase.execSQL(notifications.CREATE_TABLE);
                    break;
                case 5:
                    DB_Vacations_Manager vacations = new DB_Vacations_Manager();
                    sqLiteDatabase.execSQL(vacations.CREATE_TABLE);
                    break;
                case 6:
                    DB_Calendar_Notifications_Manager calendar_notifications = new DB_Calendar_Notifications_Manager();
                    sqLiteDatabase.execSQL(calendar_notifications.CREATE_TABLE);
                    break;
                case 7:
                    DB_Calendar_Notifications_Manager calendar_notifications2 = new DB_Calendar_Notifications_Manager();
                    sqLiteDatabase.execSQL("ALTER TABLE "+calendar_notifications2.TABLE+" ADD COLUMN "+calendar_notifications2.TYPE+" TEXT");
                    break;
            }
        }
    }
}