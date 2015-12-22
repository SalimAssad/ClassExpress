package com.chacostak.salim.classexpress.Calendar.Data;

import java.util.Calendar;

/**
 * Created by Salim on 10/12/2015.
 */
public class CalendarData {

    private String title;
    private Calendar initialDate;
    private char type;
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public Calendar getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Calendar initialDate) {
        this.initialDate = initialDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
