package com.chacostak.salim.classexpress.Add_course;

import android.text.Editable;
import android.text.TextWatcher;

import com.chacostak.salim.classexpress.Info_activities.Signature_info.Fragment_signature_info;

/**
 * Created by Salim on 30/03/2015.
 */
public class textListener implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        Fragment_signature_info.signature_parent = String.valueOf(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
