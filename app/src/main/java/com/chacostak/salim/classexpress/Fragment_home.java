package com.chacostak.salim.classexpress;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.Day_courses.Day_courses_activity;
import com.chacostak.salim.classexpress.Notifications.AlarmHandler;
import com.chacostak.salim.classexpress.Services.ServiceManager;
import com.chacostak.salim.classexpress.Upcoming_events.Fragment_upcoming_exam;
import com.chacostak.salim.classexpress.Upcoming_events.Fragment_upcoming_homework;
import com.chacostak.salim.classexpress.Upcoming_events.Fragment_upcoming_course;
import com.chacostak.salim.classexpress.Upcoming_homework.Fragment_this_week_homework;
import com.chacostak.salim.classexpress.Upcoming_homework.Upcoming_homework_activity;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;
import com.chacostak.salim.classexpress.Utilities.CourseValidation;
import com.chacostak.salim.classexpress.Utilities.Sorter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 05/02/2015.
 */
public class Fragment_home extends Fragment implements View.OnClickListener {

    View v;

    public static DB_Courses_Manager sig_manager;
    public static DB_Schedule_Manager sch_manager;
    public static DB_Homework_Manager homework_manager;
    public static DB_Exams_Manager exams_manager;

    public static ServiceManager serviceManager;

    TextView textSigToday, textThisWeek, textNextWeek;

    Calendar calendar;
    int day, month, year;
    String monthName;
    String day_name;
    int dayOfWeek;
    int weekOfYear;

    DateValidation dateValidation;
    CourseValidation courseValidation;
    AlarmHandler alarmHandler;

    public static String NAME = "NAME";
    public static String INITIAL_TIME = "INITIAL_TIME";
    public static String ENDING_TIME = "ENDING_TIME";
    public static String REMAINING_TIME = "REMAINING_TIME";
    public static String DATE = "DATE";
    public static String DESCRIPTION = "ROOM";
    public static String SIG_PARENT = "SIG_PARENT";
    public static String TEACHER = "TEACHER";
    public static String COLOR = "COLOR";
    public static String ACTIVATE_TIMER = "ACTIVATE_TIMER";
    public static String DAY_OF_WEEK = "DAY_OF_WEEK";

    ArrayList<EventData> events = new ArrayList();
    Sorter sorter = new Sorter();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        if (serviceManager == null)
            serviceManager = new ServiceManager(getActivity());

        if (alarmHandler == null)
            alarmHandler = new AlarmHandler();

        if (!alarmHandler.courseAlarmIsSet(getActivity())) {
            alarmHandler.setCourseAlarm(getActivity(), Calendar.getInstance(), null, null, 0);
        }

        if (savedInstanceState == null) {
            sig_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
            sch_manager = new DB_Schedule_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
            homework_manager = new DB_Homework_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
            exams_manager = new DB_Exams_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

            dateValidation = new DateValidation(getActivity());
            courseValidation = new CourseValidation();

            calendar = Calendar.getInstance();
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);

            weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

            monthName = dateValidation.getMonthName(month);

            setDayName();

            initializeTexts();
            //checkEvents();

            v.findViewById(R.id.course_layout).setOnClickListener(this);
            v.findViewById(R.id.homework_layout).setOnClickListener(this);
        }

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        removeFragments();
    }

    private void removeFragments() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        for(int i = 0; i < events.size(); i++){
            Fragment fragment = getFragmentManager().findFragmentByTag(String.valueOf(i));
            transaction.remove(fragment);
        }
        transaction.commit();
        events.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkEvents();
    }

    private void checkEvents() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkForCourses();
                checkForHomeworks();
                checkForExams();

                events = sorter.bubbleSortRemainingTime(events);
                addEvents();
            }
        });
    }

    private void setDayName() {
        String days[] = getResources().getStringArray(R.array.days);
        switch (calendar.get(calendar.DAY_OF_WEEK)) {
            case 1:
                day_name = days[6];
                dayOfWeek = 1;
                break;
            case 2:
                day_name = days[0];
                dayOfWeek = 2;
                break;
            case 3:
                day_name = days[1];
                dayOfWeek = 3;
                break;
            case 4:
                day_name = days[2];
                dayOfWeek = 4;
                break;
            case 5:
                day_name = days[3];
                dayOfWeek = 5;
                break;
            case 6:
                day_name = days[4];
                dayOfWeek = 6;
                break;
            case 7:
                day_name = days[5];
                dayOfWeek = 7;
                break;
        }
    }

    private void initializeTexts() {
        textSigToday = (TextView) v.findViewById(R.id.text_sig_today);
        textThisWeek = (TextView) v.findViewById(R.id.text_this_week);
        textNextWeek = (TextView) v.findViewById(R.id.text_next_week);
    }

    private void checkForCourses() {
        Cursor schedule = sch_manager.getAll();
        Cursor course;
        int i = 0;
        long remainingTime;
        String storedDay;
        String storedInitialHour;
        String storedEndingHour;
        String storedCourse;
        String storedTeacher;
        String storedColor;
        while (schedule.moveToNext()) {
            storedDay = schedule.getString(schedule.getColumnIndex(sch_manager.DAY_OF_WEEK));
            if (day_name.equals(storedDay)) {
                storedCourse = schedule.getString(schedule.getColumnIndex(sch_manager.COURSE));
                storedInitialHour = schedule.getString(schedule.getColumnIndex(sch_manager.START));
                storedEndingHour = schedule.getString(schedule.getColumnIndex(sch_manager.END));
                remainingTime = dateValidation.getRemainingTime(storedInitialHour);

                course = sig_manager.searchByName(storedCourse);
                course.moveToNext();
                storedTeacher = course.getString(course.getColumnIndex(sig_manager.TEACHER_NAME));
                storedColor = course.getString(course.getColumnIndex(sig_manager.COLOR));

                if (courseValidation.stillInCourse(getActivity(), course.getString(course.getColumnIndex(sig_manager.START)), course.getString(course.getColumnIndex(sig_manager.END)))) {
                    i++;
                    checkCourseRemainingTime(remainingTime, storedCourse, storedInitialHour, storedEndingHour, storedTeacher, storedColor);
                }
                course.close();
            }
        }
        schedule.close();
        textSigToday.setText(getString(R.string.home_today) + " " + i);
    }

    private void checkCourseRemainingTime(long remainingTime, String course, String initialHour, String endingHour, String teacher, String storedColor) {
        long hourAndHalf = 5400000;
        if (remainingTime < hourAndHalf && remainingTime >= 0)
            events.add(new EventData(course, teacher, initialHour, endingHour, remainingTime, 'S', storedColor));
    }

    private void checkForHomeworks() {
        Cursor homework = homework_manager.getAll();
        Cursor cursor_course;
        String storedTitle;
        String storedDescription;
        String storedDate;
        String storedTimeLimit;
        String storedCourse;
        String storedColor;
        int tw = 0;
        int nw = 0;
        int hw_week_of_year;
        long remainingTime;
        while (homework.moveToNext()) {
            storedTitle = homework.getString(homework.getColumnIndex(homework_manager.TITLE));
            storedDescription = homework.getString(homework.getColumnIndex(homework_manager.DESCRIPTION));
            storedDate = homework.getString(homework.getColumnIndex(homework_manager.DAY_LIMIT));
            storedTimeLimit = homework.getString(homework.getColumnIndex(homework_manager.TIME_LIMIT));
            storedCourse = homework.getString(homework.getColumnIndex(homework_manager.COURSE));

            cursor_course = sig_manager.getCourseColor(storedCourse);
            cursor_course.moveToNext();
            storedColor = cursor_course.getString(0);

            hw_week_of_year = dateValidation.getWeekOfYear(storedDate + " " + storedTimeLimit);

            if (hw_week_of_year == weekOfYear) {
                if (calendar.before(dateValidation.formatDateANDTimeInPm(storedDate + " " + storedTimeLimit)))
                    tw++;
            } else if (hw_week_of_year == weekOfYear + 1)
                nw++;

            remainingTime = dateValidation.getRemainingTime(dateValidation.formatDateANDTimeInPm(storedDate + " " + storedTimeLimit));

            checkHwRemainingTime(remainingTime, storedTitle, storedDescription, storedDate, storedTimeLimit, storedCourse, storedColor);

            cursor_course.close();
        }
        homework.close();
        textThisWeek.setText(getString(R.string.home_this_week) + " " + tw);
        textNextWeek.setText(getString(R.string.home_next_week) + " " + nw);
    }

    private void checkHwRemainingTime(long remainingTime, String title, String description, String date, String time, String course, String color) {
        long threeDays = 259200000;
        if (remainingTime <= threeDays && remainingTime >= 0)
            events.add(new EventData(title, description, date, time, remainingTime, 'H', course, color));
    }

    private void checkForExams() {
        Cursor exam = exams_manager.getAll();
        Cursor cursor_course;
        String storedCourse;
        String storedRoom;
        String storedDate;
        String storedTimeLimit;
        String storedColor;
        long remainingTime;
        while (exam.moveToNext()) {
            storedRoom = exam.getString(exam.getColumnIndex(exams_manager.ROOM));
            storedDate = exam.getString(exam.getColumnIndex(exams_manager.DAY_LIMIT));
            storedTimeLimit = exam.getString(exam.getColumnIndex(exams_manager.TIME_LIMIT));
            storedCourse = exam.getString(exam.getColumnIndex(exams_manager.COURSE));

            cursor_course = sig_manager.getCourseColor(storedCourse);
            cursor_course.moveToNext();
            storedColor = cursor_course.getString(0);

            remainingTime = dateValidation.getRemainingTime(dateValidation.formatDateANDTimeInPm(storedDate + " " + storedTimeLimit));

            checkExamRemainingTime(remainingTime, storedCourse, storedRoom, storedDate, storedTimeLimit, storedColor);

            cursor_course.close();
        }
        exam.close();
    }

    private void checkExamRemainingTime(long remainingTime, String course, String room, String date, String time, String color) {
        long fiveDays = 432000000;
        if (remainingTime <= fiveDays && remainingTime >= 0) {
            EventData data = new EventData();
            data.name = course;
            data.description = room;
            data.initial_date = date;
            data.initial_time = time;
            data.remainingTime = remainingTime;
            data.type = 'E';
            data.color = color;
            events.add(data);
        }
    }

    public void addEvents() {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).type == 'S') {
                Bundle arguments = new Bundle();
                Fragment_upcoming_course frag = new Fragment_upcoming_course();
                arguments.putString(NAME, events.get(i).name);
                arguments.putString(TEACHER, events.get(i).teacher);
                arguments.putString(INITIAL_TIME, events.get(i).initial_time);
                arguments.putString(ENDING_TIME, events.get(i).ending_time);
                arguments.putLong(REMAINING_TIME, events.get(i).remainingTime);
                arguments.putString(COLOR, events.get(i).color);
                arguments.putBoolean(ACTIVATE_TIMER, true);
                arguments.putBoolean(Fragment_this_week_homework.THIS_WEEK, true);
                frag.setArguments(arguments);
                getFragmentManager().beginTransaction().add(R.id.event_container, frag, String.valueOf(i))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            } else if (events.get(i).type == 'H') {
                Bundle arguments = new Bundle();
                Fragment_upcoming_homework frag = new Fragment_upcoming_homework();
                arguments.putString(NAME, events.get(i).name);
                arguments.putString(DESCRIPTION, events.get(i).description);
                arguments.putString(INITIAL_TIME, events.get(i).initial_time);
                arguments.putString(DATE, events.get(i).initial_date);
                arguments.putLong(REMAINING_TIME, events.get(i).remainingTime);
                arguments.putString(SIG_PARENT, events.get(i).sig_parent);
                arguments.putString(COLOR, events.get(i).color);
                arguments.putBoolean(ACTIVATE_TIMER, true);
                arguments.putBoolean(Fragment_this_week_homework.THIS_WEEK, true);
                frag.setArguments(arguments);
                getFragmentManager().beginTransaction().add(R.id.event_container, frag, String.valueOf(i))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            } else if (events.get(i).type == 'E') {
                Bundle arguments = new Bundle();
                Fragment_upcoming_exam frag = new Fragment_upcoming_exam();
                arguments.putString(NAME, getString(R.string.exam) + " - " + events.get(i).name);
                arguments.putString(DESCRIPTION, events.get(i).description);
                arguments.putString(INITIAL_TIME, events.get(i).initial_time);
                arguments.putString(DATE, events.get(i).initial_date);
                arguments.putLong(REMAINING_TIME, events.get(i).remainingTime);
                arguments.putString(COLOR, events.get(i).color);
                frag.setArguments(arguments);
                getFragmentManager().beginTransaction().add(R.id.event_container, frag, String.valueOf(i))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
        }

        if (!events.isEmpty())
            removeTextNoEvents();
    }

    private void removeTextNoEvents() {
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.event_container);
        layout.removeView(v.findViewById(R.id.textNoEvents));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_layout:
                Intent sig_intent = new Intent(getActivity(), Day_courses_activity.class);
                sig_intent.putExtra(DATE, day_name);
                sig_intent.putExtra(DAY_OF_WEEK, dayOfWeek);
                startActivity(sig_intent);
                break;
            case R.id.homework_layout:
                Intent hw_intent = new Intent(getActivity(), Upcoming_homework_activity.class);
                hw_intent.putExtra(DATE, weekOfYear);
                startActivity(hw_intent);
                break;
        }
    }
}
