package com.chacostak.salim.classexpress.Configuration;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chacostak.salim.classexpress.R;

/**
 * Created by Salim on 01/06/2015.
 */
public class Fragment_backup extends Fragment {

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_backup, container, false);

        return v;
    }
}