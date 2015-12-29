package com.chacostak.salim.classexpress.Info_activities.Vacation_info;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Add_homework.Fragment_add_homework;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Vacations_Manager;
import com.chacostak.salim.classexpress.Fragment_vacations;
import com.chacostak.salim.classexpress.R;

import java.util.Calendar;

/**
 * Created by Salim on 04/04/2015.
 */
public class Fragment_vacation_info extends Fragment implements CompoundButton.OnCheckedChangeListener {

    View v;
    TextView textTitle, textDate;
    CheckBox yearlyCheckBox;

    Cursor cursor;
    DB_Vacations_Manager vac_manager;

    String title;
    String begin_date;
    String end_date;
    boolean yearly;

    boolean wasEdited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_vacation_info, container, false);

        vac_manager = new DB_Vacations_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        if(savedInstanceState != null)
            title = savedInstanceState.getString(Fragment_add_homework.TITLE);
        else if(!wasEdited)
            title = getArguments().getString(Fragment_vacations.SELECTED_VACATION);

        if (!wasEdited) {
            cursor = vac_manager.searchByTitle(title);
            cursor.moveToNext();//If this is true, then it has not been edited
            initializeAtributtes();
        }

        initializeTexts();

        getActivity().setTitle(title);

        return v;
    }

    @Override
    public void onDestroyView() {
        vac_manager.closeDatabase();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(Fragment_add_homework.TITLE, title);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void initializeTexts() {
        textTitle = (TextView) v.findViewById(R.id.showTitle);
        textDate = (TextView) v.findViewById(R.id.showDate);
        yearlyCheckBox = (CheckBox) v.findViewById(R.id.checkBox);

        textTitle.setText(title);
        setDateTextViews();

        yearlyCheckBox.setChecked(yearly);
        yearlyCheckBox.setOnCheckedChangeListener(this);
    }

    private void initializeAtributtes() {
        title = cursor.getString(cursor.getColumnIndex(vac_manager.TITLE));
        begin_date = cursor.getString(cursor.getColumnIndex(vac_manager.START));
        end_date = cursor.getString(cursor.getColumnIndex(vac_manager.END));
        if(cursor.getInt(cursor.getColumnIndex(vac_manager.YEARLY)) == 1)
            yearly = true;
        else
            yearly = false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        yearly = isChecked;
        String date1[] = begin_date.split("/");
        String date2[] = end_date.split("/");
        if(yearly) {    //If it is being changed to yearly
            begin_date = date1[0] + "/" + date1[1];
            end_date = date2[0] + "/" + date2[1];
            setDateTextViews();
            vac_manager.updateYearly(title, begin_date, end_date, 1);
        }else { //If it is being changed to NOT yearly
            int year = Calendar.getInstance().get(Calendar.YEAR);
            begin_date = date1[0] + "/" + date1[1] + "/" + year;
            end_date = date2[0] + "/" + date2[1] + "/" + year;
            setDateTextViews();
            vac_manager.updateYearly(title, begin_date, end_date, 0);
        }
    }

    private void setDateTextViews() {
        if(begin_date.equals(end_date))
            textDate.setText(begin_date);
        else
            textDate.setText(begin_date + " - " + end_date);
    }
}
