package com.chacostak.salim.classexpress.Calendar.Day_info;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chacostak.salim.classexpress.Calendar.Data.CalendarData;
import com.chacostak.salim.classexpress.Calendar.Data.ExamData;
import com.chacostak.salim.classexpress.Calendar.Data.HomeworkData;
import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Vacations_Manager;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.Sorter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 12/04/2015.
 */
public class Fragment_day_info extends Fragment implements AdapterView.OnItemClickListener {

    View v;
    Day_info_adapter adapter;
    ListView list;

    Calendar calendar;
    String date;

    Cursor cursor;

    ArrayList<CalendarData> data;
    Sorter sort = new Sorter();

    DateValidation dateValidation;

    DB_Exams_Manager exams_manager;
    DB_Homework_Manager homework_manager;
    DB_Vacations_Manager vacations_manager;
    DB_Courses_Manager courses_manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_day_info, container, false);

        dateValidation = new DateValidation();

        if (getArguments() != null) {
            date = getArguments().getString(Day_info_activity.DATE);
            calendar = dateValidation.formatDate(date);
        }

        exams_manager = new DB_Exams_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        homework_manager = new DB_Homework_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        vacations_manager = new DB_Vacations_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        courses_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        data = new ArrayList<>();

        prepareAdapter();
        prepareListview();

        getActivity().setTitle(date);

        return v;
    }

    @Override
    public void onDestroyView() {
        exams_manager.closeDatabase();
        homework_manager.closeDatabase();
        vacations_manager.closeDatabase();
        courses_manager.closeDatabase();
        super.onDestroyView();
    }

    //Sets up the adapter reading the values from the data base
    private void prepareAdapter() {
        data.clear();

        Cursor course_cursor;
        String course;
        cursor = homework_manager.searchByDate(date);
        while (cursor.moveToNext()) {
            HomeworkData homeworkData = new HomeworkData();
            Calendar cal = dateValidation.formatDateANDTimeInPm(cursor.getString(cursor.getColumnIndex(homework_manager.DAY_LIMIT)) + " " + cursor.getString(cursor.getColumnIndex(homework_manager.TIME_LIMIT)));
            course = cursor.getString(cursor.getColumnIndex(homework_manager.COURSE));
            course_cursor = courses_manager.getCourseColor(course);
            course_cursor.moveToNext();
            homeworkData.setTitle(cursor.getString(cursor.getColumnIndex(homework_manager.TITLE)));
            homeworkData.setDescription(cursor.getString(cursor.getColumnIndex(homework_manager.DESCRIPTION)));
            homeworkData.setColor(course_cursor.getString(0));
            homeworkData.setType('H');
            homeworkData.setInitialDate(cal);
            homeworkData.setCourse(course);

            data.add(homeworkData);

            course_cursor.close();
        }
        cursor.close();

        cursor = exams_manager.searchByDate(date);
        while (cursor.moveToNext()) {
            ExamData examData = new ExamData();
            Calendar cal = dateValidation.formatDateANDTimeInPm(cursor.getString(cursor.getColumnIndex(exams_manager.DAY_LIMIT)) + " " + cursor.getString(cursor.getColumnIndex(exams_manager.TIME_LIMIT)));
            course = cursor.getString(cursor.getColumnIndex(exams_manager.COURSE));
            course_cursor = courses_manager.getCourseColor(course);
            course_cursor.moveToNext();
            examData.setTitle(getString(R.string.exam) + ": " + course);
            examData.setColor(course_cursor.getString(0));
            examData.setType('E');
            examData.setInitialDate(cal);
            examData.setCourse(course);

            data.add(examData);

            course_cursor.close();
        }
        cursor.close();


        data = sort.bubbleSortCalendarData(data);
        adapter = new Day_info_adapter(getActivity(), R.layout.day_info_adapter, data);
    }

    private void prepareListview() {
        list = (ListView) v.findViewById(R.id.listView);
        list.setAdapter(adapter);

        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}