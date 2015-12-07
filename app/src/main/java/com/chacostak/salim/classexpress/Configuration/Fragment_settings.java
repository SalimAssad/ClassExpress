package com.chacostak.salim.classexpress.Configuration;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chacostak.salim.classexpress.R;

/**
 * Created by Salim on 21/04/2015.
 */
public class Fragment_settings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        String settings = getArguments().getString("settings");

        if(settings.equals("notifications"))
            addPreferencesFromResource(R.xml.pref_notification);
        else if(settings.equals("delete_confirmation"))
            addPreferencesFromResource(R.xml.pref_delete_confirmations);
    }
}
