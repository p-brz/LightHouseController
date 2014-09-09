package com.example.lighthousecontroller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;

public class LampDetailsFragment extends Fragment{

	private Lamp lamp;
	
	private ImageView lampIconView;
	private Checkable powerControl;

	private boolean viewsReady;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_lamp_details,
				container, false);
		
		setupViews(rootView);
		populateViews();
		
		return rootView;
	}

	private void setupViews(View rootView) {
		lampIconView = (ImageView)rootView.findViewById(R.id.lampDetails_iconView);
		powerControl = (Checkable)rootView.findViewById(R.id.lampDetails_powerControl);
	
		viewsReady = true;
	}

	public Lamp getLamp() {
		return lamp;
	}
	public void setLamp(Lamp lamp) {
		this.lamp = lamp;
		populateViews();
	}

	private void populateViews() {
		if(viewsReady){
			boolean lampIsOn = (lamp != null && lamp.isOn());
			lampIconView.setImageResource( lampIsOn? R.drawable.ic_lamp_on : R.drawable.ic_lamp_off);
			powerControl.setChecked(lampIsOn);
		}
	}
}
