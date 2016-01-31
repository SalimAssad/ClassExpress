package com.chacostak.salim.classexpress.Calendar.Day_info;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Calendar.Data.CalendarData;
import com.chacostak.salim.classexpress.Calendar.Data.ExamData;
import com.chacostak.salim.classexpress.Calendar.Data.HomeworkData;
import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Vacations_Manager;
import com.chacostak.salim.classexpress.Fragment_calendar;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.Sorter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 12/04/2015.
 */
public class Fragment_day_info extends Fragment {

    View v;
    TextView textSelectedDate;

    Calendar calendar;
    String date = "";

    Cursor cursor;

    ArrayList<CalendarData> homeworkData;
    ArrayList<CalendarData> examsData;
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
            date = getArguments().getString(Fragment_calendar.DATE);
            calendar = dateValidation.formatDate(date);
        }

        exams_manager = new DB_Exams_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        homework_manager = new DB_Homework_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        vacations_manager = new DB_Vacations_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        courses_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        homeworkData = new ArrayList<>();
        examsData = new ArrayList<>();

        textSelectedDate = (TextView) v.findViewById(R.id.textSelectedDate);
        textSelectedDate.setText(date);

        load();

        return v;
    }

    private void load() {
        int tag;
        prepareHomeworkData();
        tag = addHomeworkFragments(0);

        prepareExamsData();
        addExamsFragments(tag);
    }

    @Override
    public void onDestroyView() {
        exams_manager.closeDatabase();
        homework_manager.closeDatabase();
        vacations_manager.closeDatabase();
        courses_manager.closeDatabase();
        super.onDestroyView();
    }

    //Sets up the adapter reading the values from the homeworkData base
    private void prepareHomeworkData() {
        homeworkData.clear();

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

            this.homeworkData.add(homeworkData);

            course_cursor.close();
        }
        cursor.close();

        homeworkData = sort.bubbleSortCalendarData(homeworkData);
    }

    private int addHomeworkFragments(int startingTag) {
        for (int i = 0; i < homeworkData.size(); i++) {
            Bundle arguments = new Bundle();
            Fragment_homework_day_info frag = new Fragment_homework_day_info();
            arguments.putString(Fragment_homework_day_info.TITLE, homeworkData.get(i).getTitle());
            arguments.putString(Fragment_homework_day_info.DESCRIPTION, ((HomeworkData) homeworkData.get(i)).getDescription());
            arguments.putString(Fragment_homework_day_info.COURSE, ((HomeworkData) homeworkData.get(i)).getCourse());
            arguments.putString(Fragment_homework_day_info.DATE, getDate(homeworkData.get(i).getInitialDate()));
            arguments.putString(Fragment_homework_day_info.COLOR, homeworkData.get(i).getColor());
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.homeworkContainer, frag, String.valueOf(startingTag))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

            startingTag++;
        }

        return startingTag;
    }

    //Sets up the adapter reading the values from the homeworkData base
    private void prepareExamsData() {
        examsData.clear();

        Cursor course_cursor;
        String course;

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

            examsData.add(examData);

            course_cursor.close();
        }
        cursor.close();


        examsData = sort.bubbleSortCalendarData(examsData);
    }

    private int addExamsFragments(int startingTag) {
        for (int i = 0; i < examsData.size(); i++) {
            Bundle arguments = new Bundle();
            Fragment_exam_day_info frag = new Fragment_exam_day_info();
            arguments.putString(Fragment_exam_day_info.TITLE, examsData.get(i).getTitle());
            arguments.putString(Fragment_exam_day_info.DESCRIPTION, ((ExamData) examsData.get(i)).getRoom());
            arguments.putString(Fragment_exam_day_info.DATE, getDate(examsData.get(i).getInitialDate()));
            arguments.putString(Fragment_exam_day_info.COLOR, examsData.get(i).getColor());
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.examsContainer, frag, String.valueOf(startingTag))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

            startingTag++;
        }

        return startingTag;
    }

    private String getDate(Calendar cal) {
        String am_pm;
        final int AM_PM = cal.get(Calendar.AM_PM);
        String minutes = String.valueOf(cal.get(Calendar.MINUTE));
        if (AM_PM == 0)
            am_pm = "am";
        else
            am_pm = "pm";

        if (minutes.length() == 1)
            minutes = "0" + minutes;

        return cal.get(Calendar.HOUR) + ":" + minutes + " " + am_pm;
    }

    public void updateData(String date) {
        this.date = date;
        textSelectedDate.setText(date);

        removeFragments();
        load();
    }

    private void removeFragments() {
        int totalSize = homeworkData.size() + examsData.size();

        for (int i = 0; i < totalSize; i++){
            Fragment frag = getFragmentManager().findFragmentByTag(String.valueOf(i));
            getFragmentManager().beginTransaction().remove(frag).commit();
        }

        homeworkData.clear();
        examsData.clear();
    }
}