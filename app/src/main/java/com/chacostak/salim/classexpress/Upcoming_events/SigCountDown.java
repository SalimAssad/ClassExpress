package com.chacostak.salim.classexpress.Upcoming_events;

import android.app.FragmentTransaction;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by Salim on 09/04/2015.
 */
public class SigCountDown extends CountDownTimer {

    Fragment_upcoming_course frag;

    public SigCountDown(long millisInFuture, long countDownInterval, Fragment_upcoming_course xfrag) {
        super(millisInFuture, countDownInterval);
        frag = xfrag;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        frag.timer.setText(String.valueOf((millisUntilFinished/1000)/60));
    }

    @Override
    public void onFinish() {
        try {
            frag.getFragmentManager().beginTransaction().remove(frag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }catch (Exception e){
            Log.d("Exception: ", e.toString());
        }
    }
}
