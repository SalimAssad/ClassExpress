package com.chacostak.salim.classexpress;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;

/**
 * Created by Salim on 11/04/2015.
 */
public class CustomAdapter extends ArrayAdapter {

    ArrayList<LinearLayout> layouts = new ArrayList<>();

    public CustomAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CustomAdapter(Context context, int resource, ArrayList<EventData> data) {
        super(context, resource, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.custom_list_view, null);
        }

        EventData data = (EventData) getItem(position);

        if(data != null){
            TextView text_title = (TextView) v.findViewById(R.id.text_title);
            TextView text_medium = (TextView) v.findViewById(R.id.text_medium);
            View view = v.findViewById(R.id.show_color);

            LinearLayout aux = (LinearLayout) v.findViewById(R.id.main_layout);

            if(!layouts.contains(aux)) {
                layouts.add(aux);
                // If the event has already passed it will gray it out
                if(data.remainingTime < 0)
                    layouts.get(position).setBackgroundColor(Color.parseColor("#1E010101"));
            }

            text_title.setText(data.name);

            if(data.color != null)
                view.setBackgroundColor(Color.parseColor(data.color));

            if(data.teacher != null)
                text_medium.setText(data.teacher);
            else if(data.description != null)
                text_medium.setText(data.description);
            else if(!data.email.equals(""))
                text_medium.setText(data.email);
            else if(!data.phone.equals(""))
                text_medium.setText(data.phone);
            else if(!data.web_page.equals(""))
                text_medium.setText(data.web_page);
            else
                text_medium.setText(getContext().getString(R.string.no_more_info));
        }

        return v;
    }

    public void setSelection(int pos){
        try {
            layouts.get(pos).setBackgroundColor(Color.parseColor("#B41272FF"));
        }catch (IndexOutOfBoundsException e){

        }
    }

    public void deSelect(int pos){
        try {
            layouts.get(pos).setBackgroundColor(Color.TRANSPARENT);
        }catch (IndexOutOfBoundsException e){

        }
    }
}
