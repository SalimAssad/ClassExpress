package com.chacostak.salim.classexpress.Services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Notifications_Manager;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.EventData;
import com.chacostak.salim.classexpress.Utilities.SystemManager;

import java.util.ArrayList;

/**
 * Created by Salim on 29/05/2015.
 */
public class CourseService extends Service {
    ArrayList<EventData> data;
    ArrayList<CountDownTimer> countDowns = new ArrayList<>();

    public static final String EVENTS = "EVENTS";
    public static final String TYPE = "TYPE";
    public static final String ID = "ID";
    public static final String REMAINING_TIME = "REMAINING_TIME";

    public static final String COUNTDOWN = "CourseService";

    Intent filter = new Intent(COUNTDOWN);

    public static boolean isActive = false;

    DB_Notifications_Manager notifications_manager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //If it is already active it will skip all the process below
        if (intent == null)
            return super.onStartCommand(intent, flags, startId);
        else
            isActive = true;

        data = intent.getParcelableArrayListExtra(EVENTS);
        notifications_manager = new DB_Notifications_Manager(this, DB_Helper.DB_Name, DB_Helper.DB_Version);
        final SystemManager systemManager = new SystemManager(this);

        if(countDowns.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                final int POS = i;
                countDowns.add(new CountDownTimer(data.get(POS).remainingTime, 59000) {

                    SharedPreferences preferences;


                    @Override
                    public void onTick(long millisUntilFinished) {
                        long time;
                        long hourAndHalf = 5400000;
                        if (millisUntilFinished <= hourAndHalf) {
                            time = (millisUntilFinished / 1000) / 60;

                            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            if (preferences.getBoolean("notifications_signatures", false)) {
                                Cursor sig_cursor = notifications_manager.searchByTag(DB_Notifications_Manager.COURSE_TAG);
                                while (sig_cursor.moveToNext()) {
                                    if (sig_cursor.getInt(sig_cursor.getColumnIndex(notifications_manager.TIME_BEFORE)) == time)
                                        systemManager.createNotification(data.get(POS).name, time + " " + getString(R.string.minutes_left));
                                }
                            }
                            Log.d("Sigue corriendo: ", "Sip, sigue");
                            filter.putExtra(TYPE, 'S');
                            filter.putExtra(ID, data.get(POS).name);
                            filter.putExtra(REMAINING_TIME, String.valueOf(time));
                            sendBroadcast(filter);
                        }
                    }

                    @Override
                    public void onFinish() {
                        countDowns.remove(this);
                        if (countDowns.isEmpty())
                            stopService(new Intent(getApplicationContext(), CourseService.class));
                    }
                });

                countDowns.get(i).start();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActive = false;
        for (int i = 0; i < countDowns.size(); i++)
            countDowns.get(i).cancel();
        countDowns.clear();
        data.clear();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
