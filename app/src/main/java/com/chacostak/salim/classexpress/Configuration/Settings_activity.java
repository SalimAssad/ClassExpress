package com.chacostak.salim.classexpress.Configuration;

import android.preference.PreferenceActivity;

import com.chacostak.salim.classexpress.Configuration.Backup_and_restore.Fragment_backup;
import com.chacostak.salim.classexpress.R;

import java.util.List;

public class Settings_activity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.main_preferences, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (Fragment_settings.class.getName().equals(fragmentName))
            return true;
        else if(Fragment_backup.class.getName().equals(fragmentName))
            return true;
        else
            return false;
    }
}
