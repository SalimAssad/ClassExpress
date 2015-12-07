package com.chacostak.salim.classexpress.Utilities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.chacostak.salim.classexpress.R;

/**
 * Created by Salim on 17/01/2015.
 */
public class SystemManager {

    Context activity;

    public SystemManager(Context xactivity){
        activity = xactivity;
    }

    public double getDisplaySize(){
        double width, height, inches;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) activity.getSystemService(activity.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        width = Math.pow(dm.widthPixels/dm.xdpi,2);
        height = Math.pow(dm.heightPixels/dm.ydpi,2);
        inches = Math.sqrt(width+height);
        return inches;
    }

    public boolean isTablet(){
        Configuration config = activity.getResources().getConfiguration();
        if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)
            return true;
        else
            return false;
    }

    public boolean isLarge(){
        Configuration config = activity.getResources().getConfiguration();
        if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE)
            return true;
        else
            return false;
    }

    public boolean isxLarge(){
        Configuration config = activity.getResources().getConfiguration();
        if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            return true;
        else
            return false;
    }

    public boolean isInLandscape(){
        Configuration config = activity.getResources().getConfiguration();
        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else
            return false;
    }

    public void createNotification(String title, String description){
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification noti = new NotificationCompat.Builder(activity)
                .setContentTitle(title)
                .setAutoCancel(true).setSmallIcon(R.drawable.ic_launcher)
                .setContentText(description)
                .setSound(soundUri)
                .setLights(0x5588dd, 1500, 2000).build();
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Activity.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), noti);
    }

    public PowerManager.WakeLock getWakelock(){
        PowerManager powerManager = (PowerManager) activity.getSystemService(Activity.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        return wakeLock;
    }
}
