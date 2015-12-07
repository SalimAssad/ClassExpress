package com.chacostak.salim.classexpress.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.chacostak.salim.classexpress.R;

/**
 * Created by Salim on 16/01/2015.
 */
public class Dialogs {

    private AlertDialog.Builder builder;

    public Dialogs(){

    }

    public Dialogs(Context activity){
        builder = new AlertDialog.Builder(activity);
    }

    //Creates a basic Dialog, must receive the id of the title and the id of the message
    public void createDialog(int title, int message){
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Creates a basic Dialog, must receive the id of the title and a String with the message
    public void createDialog(int title, String message){
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Creates a basic Dialog, must receive a String with the title and the id of the message
    public void createDialog(String title, int message){
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Creates a basic Dialog, must a String with the title and a String with the message
    public void createDialog(String title, String message){
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Creates a Dialog with an icon, must receive the id of the title and the id of the message
    public void createDialog(int title, int message, int icon){
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(icon);
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Creates a Dialog with an icon, must receive the id of the title and a String with the message
    public void createDialog(int title, String message, int icon){
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(icon);
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Creates a Dialog with an icon, must receive a String with the title and the id of the message
    public void createDialog(String title, int message, int icon){
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(icon);
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Creates a Dialog with an icon, must a String with the title and a String with the message
    public void createDialog(String title, String message, int icon){
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(icon);
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void createConfirmationDialog(int title, int message, int icon, final DialogInterface.OnClickListener listener){
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(icon);
        builder.setPositiveButton(builder.getContext().getString(R.string.yes), listener);
        builder.setNegativeButton(builder.getContext().getString(R.string.no), listener);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
