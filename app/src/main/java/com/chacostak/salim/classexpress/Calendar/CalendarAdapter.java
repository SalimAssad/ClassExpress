package com.chacostak.salim.classexpress.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Calendar.Data.CalendarData;
import com.chacostak.salim.classexpress.Calendar.Data.VacationData;
import com.chacostak.salim.classexpress.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 20/08/2015.
 */
public class CalendarAdapter extends ArrayAdapter {

    ArrayList<CalendarData> data;
    ArrayList<VacationData> vacData;
    String days[] = null;
    int realDay;
    int realMonth;
    int realYear;
    int showedMonth;
    int showedYear;

    int COLUMN_HEIGHT;
    int DAYS_HEIGHT;
    public static int height;

    int rows;

    LinearLayout.LayoutParams linearParams;

    public CalendarAdapter(Context context, int resource, ArrayList xdays, ArrayList xdata, ArrayList xvacData, int xrealDay, int xrealMonth, int xrealYear, int xshowedMonth, int xshowedYear) {
        super(context, resource, xdays);
        data = xdata;
        vacData = xvacData;
        realDay = xrealDay;
        realMonth = xrealMonth;
        realYear = xrealYear;
        showedMonth = xshowedMonth;
        showedYear = xshowedYear;

        rows = (xdays.size() / 7) - 1;

        COLUMN_HEIGHT = (int) context.getResources().getDimension(R.dimen.calendar_cell_height);
        DAYS_HEIGHT = (int) context.getResources().getDimension(R.dimen.calendar_day_height);

        height = (COLUMN_HEIGHT * rows) + DAYS_HEIGHT;

        if(!data.isEmpty())
            linearParams = getLinearLayoutParams();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.calendar_adapter, null);

        TextView textDay = (TextView) v.findViewById(R.id.textDay);

        if(position > 6) {
            int day = (int) getItem(position);
            int month;
            boolean isFromOtherMonth = isFromOtherMonth(day, position);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, COLUMN_HEIGHT);
            RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.main_layout);
            v.setLayoutParams(new GridView.LayoutParams(params));
            textDay.setText(String.valueOf(day));

            if(day > 15 && position < 13)
                month = showedMonth-1;
            else if(day < 8 && position > 30)
                month = showedMonth+1;
            else
                month = showedMonth;


            //Valida los colores de fondo
            if(isFromOtherMonth)
                layout.setBackgroundColor(Color.parseColor("#1E010101"));
            if(isInVacation(day, month)){
                TextView title = (TextView) v.findViewById(R.id.textTitle);
                title.setText(vacData.get(0).getTitle());
                layout.setBackgroundColor(Color.parseColor(vacData.get(0).getColor()));
                if(lastDayOfVacation(day))
                    vacData.remove(0);
            }
            if(isToday(day, month))
                textDay.setTextColor(Color.parseColor("#E700B3EF"));

            while(eventExistsToday(day, month)){
                LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.eventsLayout);
                TextView myView = getEmptyTextView();
                linearLayout.addView(myView, linearParams);
                data.remove(0);
            }

        }else{
            if(days == null)
                days = getContext().getResources().getStringArray(R.array.abrev_days);
            textDay.setText(days[position]);
        }

        return v;
    }

    private LinearLayout.LayoutParams getLinearLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.height = (int) getContext().getResources().getDimension(R.dimen.calendar_event_height);
        params.setMargins(3,3,3,5);
        return params;
    }

    private TextView getEmptyTextView() {
        TextView myView = new TextView(getContext());
        myView.setBackgroundColor(Color.parseColor(data.get(0).getColor()));
        return myView;
    }

    private boolean eventExistsToday(int day, int month) {
        if(data.isEmpty())
            return false;
        else {
            Calendar cal = data.get(0).getInitialDate();
            if (cal.get(Calendar.DAY_OF_MONTH) == day && cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == showedYear)
                return true;
            else
                return false;
        }
    }

    private boolean isToday(int day, int month) {
        if(day == realDay && realMonth == month && realYear == showedYear)
            return true;
        else
            return false;
    }

    private boolean isFromOtherMonth(int day, int position) {
        if((day > 15 && position < 13) || (day < 8 && position > 30))
            return true;
        else
            return false;
    }

    private boolean lastDayOfVacation(int day) {
        if(vacData.get(0).getEndingDate().get(Calendar.DAY_OF_MONTH) == day)
            return true;
        else
            return false;
    }

    private boolean isInVacation(int day, int month) {
        if(vacData.isEmpty())
            return false;
        else {
            boolean flag = false;
            int initialDay = vacData.get(0).getInitialDate().get(Calendar.DAY_OF_MONTH);
            int endingDay = vacData.get(0).getEndingDate().get(Calendar.DAY_OF_MONTH);
            int initialMonth = vacData.get(0).getInitialDate().get(Calendar.MONTH);
            int initialYear = vacData.get(0).getInitialDate().get(Calendar.YEAR);
            if (initialDay <= day && endingDay >= day) {
                if (vacData.get(0).isYearly() || initialYear == showedYear) {
                    if(initialMonth == month)
                        flag = true;
                }
            }
            return flag;
        }
    }
}
