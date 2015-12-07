package com.chacostak.salim.classexpress.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Vacations_Manager;
import com.chacostak.salim.classexpress.Utilities.EventData;

/**
 * Created by Salim on 22/08/2015.
 */
public class ClassExpressCalendar extends ChacoCalendar{

    DB_Homework_Manager homework_manager;
    DB_Vacations_Manager vacations_manager;
    DB_Exams_Manager exams_manager;

    public ClassExpressCalendar(Context xcontext, View v) {
        super(xcontext, v);

        homework_manager = new DB_Homework_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
        vacations_manager = new DB_Vacations_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
        exams_manager = new DB_Exams_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
    }

    public void loadEvents(String xmonthName){
        Cursor cursor = homework_manager.getAll();
        xmonthName = dateValidation.getMonthAbreviation(xmonthName);
        String array1[], array2[];
        while(cursor.moveToNext()){
            array1 = cursor.getString(cursor.getColumnIndex(homework_manager.DAY_LIMIT)).split("/");
            if(array1[1].equals(xmonthName)){
                EventData d = new EventData();
                d.name = cursor.getString(cursor.getColumnIndex(homework_manager.TITLE));
                d.initial_date = cursor.getString(cursor.getColumnIndex(homework_manager.DAY_LIMIT));
                d.initial_time = cursor.getString(cursor.getColumnIndex(homework_manager.TIME_LIMIT));
                d.type = 'H';
                data.add(d);
            }
        }
        cursor.close();

        cursor = exams_manager.getAll();
        while(cursor.moveToNext()){
            array1 = cursor.getString(cursor.getColumnIndex(exams_manager.DAY_LIMIT)).split("/");
            if(array1[1].equals(xmonthName)){
                EventData d = new EventData();
                d.name = cursor.getString(cursor.getColumnIndex(exams_manager.COURSE));
                d.initial_date = cursor.getString(cursor.getColumnIndex(exams_manager.DAY_LIMIT));
                d.initial_time = cursor.getString(cursor.getColumnIndex(exams_manager.TIME_LIMIT));
                d.type = 'E';
                data.add(d);
            }
        }
        cursor.close();

        cursor = vacations_manager.getAll();
        while(cursor.moveToNext()){
            array1 = cursor.getString(cursor.getColumnIndex(vacations_manager.START)).split("/");
            array2 = cursor.getString(cursor.getColumnIndex(vacations_manager.END)).split("/");
            if(array1[1].equals(xmonthName) || array2.equals(xmonthName)){
                EventData d = new EventData();
                d.name = cursor.getString(cursor.getColumnIndex(vacations_manager.TITLE));
                d.initial_date = cursor.getString(cursor.getColumnIndex(vacations_manager.START));
                d.ending_date = cursor.getString(cursor.getColumnIndex(vacations_manager.END));
                d.type = 'V';
                data.add(d);
            }
        }
        cursor.close();
    }
}
