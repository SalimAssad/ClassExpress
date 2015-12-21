package com.chacostak.salim.classexpress.Calendar.Data;

import java.util.Calendar;

/**
 * Created by Salim on 10/12/2015.
 */
public class VacationData extends CalendarData {

    private boolean yearly;
    private Calendar endingDate;

    public Calendar getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Calendar endingDate) {
        this.endingDate = endingDate;
    }

    public boolean isYearly() {
        return yearly;
    }

    public void setYearly(boolean yearly) {
        this.yearly = yearly;
    }
}
