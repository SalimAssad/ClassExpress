package com.chacostak.salim.classexpress.Info_activities.Teacher_info;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Add_teacher.Fragment_add_teacher;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Teacher_Manager;
import com.chacostak.salim.classexpress.Fragment_teachers;
import com.chacostak.salim.classexpress.R;

/**
 * Created by Salim on 05/04/2015.
 */
public class Fragment_teacher_info extends Fragment {

    View v;
    TextView textName, textPhone, textEmail, textWebPage;

    Cursor cursor;
    DB_Teacher_Manager teacher_manager;

    String name;
    String phone;
    String email;
    String web_page;

    boolean wasEdited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_teacher_info, container, false);

        teacher_manager = new DB_Teacher_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        if(savedInstanceState != null)
            name = savedInstanceState.getString(Fragment_add_teacher.NAME);
        else if(!wasEdited)
            name = getArguments().getString(Fragment_teachers.SELECTED_TEACHER);

        if (!wasEdited) {
            cursor = teacher_manager.searchByName(name);
            cursor.moveToNext();//If this is true, then it has not been edited
            initializeAtributtes();
        }

        initializeTexts();

        getActivity().setTitle(name);

        return v;
    }

    @Override
    public void onDestroyView() {
        teacher_manager.closeDatabase();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(Fragment_add_teacher.NAME, name);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void initializeTexts() {
        textName = (TextView) v.findViewById(R.id.showName);
        textPhone = (TextView) v.findViewById(R.id.showPhone);
        textEmail = (TextView) v.findViewById(R.id.showEmail);
        textWebPage = (TextView) v.findViewById(R.id.showWebPage);

        textName.setText(name);
        textPhone.setText(getString(R.string.phone)+ " " + phone);
        textEmail.setText(getString(R.string.email)+ " " + email);
        textWebPage.setText(getString(R.string.web_page)+ " " + web_page);
    }

    private void initializeAtributtes() {
        name = cursor.getString(cursor.getColumnIndex(teacher_manager.NAME));
        phone = cursor.getString(cursor.getColumnIndex(teacher_manager.PHONE));
        email = cursor.getString(cursor.getColumnIndex(teacher_manager.EMAIL));
        web_page = cursor.getString(cursor.getColumnIndex(teacher_manager.WEB_PAGE));
    }
}
