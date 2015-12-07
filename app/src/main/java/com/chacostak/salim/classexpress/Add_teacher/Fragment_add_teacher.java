package com.chacostak.salim.classexpress.Add_teacher;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Teacher_Manager;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.Validate;

/**
 * Created by Salim on 05/04/2015.
 */
public class Fragment_add_teacher extends Fragment implements View.OnClickListener {

    View v;

    EditText editWebPage;
    EditText editPhone;
    EditText editName;
    EditText editEmail;

    //OLD VALUES IF IT IS BEING EDITED
    String oldName;
    String oldEmail;
    String oldWebPage;
    String oldPhone;

    //NEW VALUES
    String newName;
    String newEmail;
    String newWebPage;
    String newPhone;

    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String WEB_PAGE = "WEB_PAGE";
    public static final String PHONE = "PHONE";

    DB_Teacher_Manager teacher_manager;

    boolean isBeingEdited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_add_teacher, container, false);

        teacher_manager = new DB_Teacher_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        initializeEdits();

        setListeners();

        if(getArguments() != null){
            isBeingEdited = true;
            oldName = getArguments().getString(NAME);
            oldEmail = getArguments().getString(EMAIL);
            oldWebPage = getArguments().getString(WEB_PAGE);
            oldPhone = getArguments().getString(PHONE);

            editName.setText(oldName);
            editEmail.setText(oldEmail);
            editWebPage.setText(oldWebPage);
            editPhone.setText(oldPhone);
        }

        return v;
    }

    private void initializeEdits() {
        editWebPage = (EditText) v.findViewById(R.id.edit_web_page);
        editPhone = (EditText) v.findViewById(R.id.edit_phone);
        editName = (EditText) v.findViewById(R.id.edit_name);
        editEmail = (EditText) v.findViewById(R.id.edit_email);
    }

    private void setListeners() {
        v.findViewById(R.id.save_button).setOnClickListener(this);
        v.findViewById(R.id.cancel_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                if(!isBeingEdited && !validate())
                    return;

                newEmail = editEmail.getText().toString();
                newWebPage = editWebPage.getText().toString();
                newPhone = editPhone.getText().toString();
                newName = editName.getText().toString();

                storeData();

                if(getArguments() != null){
                    Intent output = new Intent();
                    output.putExtra(EMAIL, newEmail);
                    output.putExtra(WEB_PAGE, newWebPage);
                    output.putExtra(PHONE, newPhone);
                    output.putExtra(NAME, newName);
                    getActivity().setResult(Activity.RESULT_OK, output);
                }
                getActivity().finish();
                break;
            case R.id.cancel_button:
                getActivity().finish();
                break;
        }
    }

    private boolean validate(){
        if(!validateInputs()) {
            Toast.makeText(getActivity(), R.string.required_fields_empty, Toast.LENGTH_LONG).show();
            return false;
        }else if(alreadyExists()) {
            Toast.makeText(getActivity(),R.string.course_exists,Toast.LENGTH_LONG).show();
            return false;
        }else
            return true;
    }

    private boolean alreadyExists(){
        String name = editName.getText().toString();
        Cursor cursor;
        cursor = teacher_manager.searchByName(name);
        if(cursor.moveToNext())
            return true;
        else
            return false;
    }

    private boolean validateInputs(){
        Validate validate = new Validate();
        if(validate.isEmpty(editName.getText().toString()))
            return false;
        else
            return true;
    }

    private void storeData(){
        if(isBeingEdited)
            teacher_manager.update(oldName, newName, newEmail, newWebPage, newPhone);
        else
            teacher_manager.insert(newName, newEmail, newWebPage, newPhone);
    }
}
