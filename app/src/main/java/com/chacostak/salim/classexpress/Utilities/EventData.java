package com.chacostak.salim.classexpress.Utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Salim on 09/04/2015.
 */
public class EventData implements Parcelable{

    public String name; //Name or title
    public String teacher;
    public String email;
    public String phone;
    public String web_page;
    public String description;
    public String initial_time;
    public String ending_time;
    public String initial_date;
    public String ending_date;
    public String sig_parent;
    public char type;  //Signature, homework, exam, etc.
    public long remainingTime = 0;
    public String color;

    public boolean flag; //Multi-Use flag, please leave annotation when you use it to remember later

    public EventData(){
    }

    public EventData(Parcel in){
        name = in.readString();
        remainingTime = in.readLong();
        type = in.readString().charAt(0);
        initial_date = in.readString();
        initial_time = in.readString();
    }

    public EventData(String xname, String xinitial_hour, long xremainingTime, char xtype){
        name = xname;
        initial_time = xinitial_hour;
        remainingTime = xremainingTime;
        type = xtype;
    }

    public EventData(String xname, String xteacher, String xinitial_hour, String xending_hour, long xremainingTime, char xtype, String xcolor){
        name = xname;
        teacher = xteacher;
        initial_time = xinitial_hour;
        ending_time = xending_hour;
        remainingTime = xremainingTime;
        type = xtype;
        color = xcolor;
    }

    public EventData(String xname, String xinitial_hour, long xremainingTime, char xtype, String xdate){
        name = xname;
        initial_time = xinitial_hour;
        remainingTime = xremainingTime;
        type = xtype;
        initial_date = xdate;
    }

    public EventData(String xname, String xdescription, String xdate, String xinitial_hour, long xremainingTime, char xtype, String xsig_parent, String xcolor){
        name = xname;
        description = xdescription;
        initial_time = xinitial_hour;
        remainingTime = xremainingTime;
        type = xtype;
        initial_date = xdate;
        sig_parent = xsig_parent;
        color = xcolor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(remainingTime);
        dest.writeString(String.valueOf(type));
        dest.writeString(initial_date);
        dest.writeString(initial_time);
    }

    public static final Parcelable.Creator<EventData> CREATOR
            = new Parcelable.Creator<EventData>() {
        public EventData createFromParcel(Parcel in) {
            return new EventData(in);
        }

        public EventData[] newArray(int size) {
            return new EventData[size];
        }
    };
}
