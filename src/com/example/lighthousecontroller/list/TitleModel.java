package com.example.lighthousecontroller.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lighthousecontroller.R;

public class TitleModel implements Model{
    private String title;
    public TitleModel(String title) {
        super();
    	this.title = title;
    }
	public boolean isGroupHeader() {
		return true;
	}
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public View createView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.lv_header_layout, parent, false);
        TextView titleView = (TextView) rowView.findViewById(R.id.lv_list_hdr);
        titleView.setText(title);
		return rowView;
	}
	
}
