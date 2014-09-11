package com.example.lighthousecontroller.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lighthousecontroller.Lamp;
import com.example.lighthousecontroller.R;

public class LampModel implements Model{
	
	private int icon;
    private Lamp lamp;
 
    public LampModel(int icon, Lamp lamp) {
        super();
        this.lamp = lamp;
        this.icon = icon;
    }
    
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public Lamp getLamp() {
		return lamp;
	}
	public void setLamp(Lamp lamp) {
		this.lamp = lamp;
	}
	@Override
	public boolean isGroupHeader() {
		return false;
	}
	@Override
	public View createView(LayoutInflater inflater, View convertView, ViewGroup parent) {

        View rowView = inflater.inflate(R.layout.target_item, parent, false);

        // 3. Get icon,title & counter views from the rowView
        ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
        TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
        //ToggleButton button = (ToggleButton) rowView.findViewById(R.id.lampIsOnButton);
        //button.setChecked(isLampOn);
        
        // 4. Set the text for textView
        imgView.setImageResource(icon);
        titleView.setText(lamp.getName());
		return rowView;
	}
	
}
