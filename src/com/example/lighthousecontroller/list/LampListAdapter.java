package com.example.lighthousecontroller.list;

import java.util.ArrayList;

import com.example.lighthousecontroller.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LampListAdapter extends ArrayAdapter<Model> {
	 
    private final Context context;
    private final ArrayList<Model> modelsArrayList;

    public LampListAdapter(Context context, ArrayList<Model> modelsArrayList) {

        super(context, R.layout.target_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = null;
        if(!modelsArrayList.get(position).isGroupHeader()){
            rowView = inflater.inflate(R.layout.target_item, parent, false);

            // 3. Get icon,title & counter views from the rowView
            ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
            TextView titleView = (TextView) rowView.findViewById(R.id.item_title);

            boolean isLampOn = modelsArrayList.get(position).getLamp().isOn();
            //ToggleButton button = (ToggleButton) rowView.findViewById(R.id.lampIsOnButton);
            //button.setChecked(isLampOn);
            
            // 4. Set the text for textView
            imgView.setImageResource(modelsArrayList.get(position).getIcon());
            titleView.setText(modelsArrayList.get(position).getLamp().getName());
        }else{
            rowView = inflater.inflate(R.layout.lv_header_layout, parent, false);
            TextView titleView = (TextView) rowView.findViewById(R.id.lv_list_hdr);
            titleView.setText(modelsArrayList.get(position).getLamp().getName());

        }

        return rowView;
    }
}
