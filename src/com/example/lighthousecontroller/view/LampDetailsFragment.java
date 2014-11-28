package com.example.lighthousecontroller.view;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.lighthousecontroller.R;
import com.example.lighthousecontroller.controller.ConsumptionObserver;
import com.example.lighthousecontroller.controller.LampController;
import com.example.lighthousecontroller.controller.LampObserver;
import com.example.lighthousecontroller.model.ConsumptionEvent;
import com.example.lighthousecontroller.model.Lamp;

public class LampDetailsFragment extends Fragment implements ConsumptionObserver, LampObserver, OnCheckedChangeListener{
	public class BrightControlChanged implements OnSeekBarChangeListener {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if(lamp != null){
				LampController.getInstance()
				  .requestChangeBright(lamp, seekBar.getProgress() /((float)seekBar.getMax()));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//			if(fromUser && lamp != null){
//				LampController.getInstance()
//				  .requestChangeBright(lamp, progress /((float)seekBar.getMax()));
//			}
		}
	}
	private static final float MIN_ICON_ALPHA = 0.3f;

	private Lamp lamp;
	
	private ImageView lampIconView;
	private ToggleButton powerControl;
	private SeekBar brightControl;
	private TextView nameView;
	
	private ConsumptionGraphFragment consumptionGraphFragment;
	
	private boolean viewsReady;
	
	public LampDetailsFragment() {
		Log.d(getClass().getName(), "Criando fragmento! " + this.toString());
		consumptionGraphFragment = new ConsumptionGraphFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_lamp_details,
				container, false);
		
		setupViews(rootView);
		updateView();
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(lamp != null && lamp.getId() > 0){
			LampController.getInstance().addConsumptionObserver(this, lamp.getId());
			LampController.getInstance().addLampObserver(this, lamp.getId());
			Lamp readedLamp = LampController.getInstance().getLampStatus(lamp);
			if(readedLamp != null){
				this.lamp.set(readedLamp);
			}
			else{
				//TODO: notificar erro (?)
			}
			updateView();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		LampController.getInstance().removeConsumptionObserver(this, lamp.getId());
		LampController.getInstance().removeLampObserver(this, lamp.getId());
	}
	public Lamp getLamp() {
		return lamp;
	}
	public void setLamp(Lamp lamp) {		
		this.lamp = lamp;
		
		assertValidLocalLamp();
		updateView();
	}

	private void setupViews(View rootView) {
		lampIconView  = (ImageView)rootView.findViewById(R.id.lampDetails_iconView);
		powerControl  = (ToggleButton)rootView.findViewById(R.id.lampDetails_powerControl);
		brightControl = (SeekBar)rootView.findViewById(R.id.lampDetails_brightControl);
		nameView      = (TextView)rootView.findViewById(R.id.lampDetails_lampName);
		
		getChildFragmentManager().beginTransaction()
									.add(R.id.lampDetails_graphContainer, consumptionGraphFragment)
									.commit();
		
		brightControl.setOnSeekBarChangeListener(new BrightControlChanged());

		powerControl.setOnCheckedChangeListener(this);
		
		
		viewsReady = true;
	}

	/* *********************************** ConsumptionObserver ************************************************/
	
	@Override
	public void onConsumption(List<ConsumptionEvent> consumptionEvents) {	
		for(ConsumptionEvent event : consumptionEvents){
			if(lamp !=null && event.getSourceId() == this.lamp.getId()){
				consumptionGraphFragment.plotConsumption(event);
			}
		}
	}

	/* *********************************** LampObserver ************************************************/
	
//	@Override
//	public void changedPowerStatus(Lamp lamp) {
////		assertValidLocalLamp();
////		assertValidLamp(lamp);
////		this.lamp.setOn(lamp.isOn());
//		updateLampStatus();
//	}
//
//	@Override
//	public void changedBright(Lamp lamp) {
//		assertValidLamp(lamp);
//		this.lamp.setBright(lamp.getBright());
//		this.lamp.setOn(lamp.isOn());
//		updateLampStatus();
//	}
	@Override
	public void lampUpdated(Lamp lamp) {
		assertValidLocalLamp();
		assertValidLamp(lamp);
		this.lamp.set(lamp);
		updateLampStatus();
	}

	private void assertValidLocalLamp() {
		if(this.lamp == null){
			throw new RuntimeException("Local lamp is null!" );
		}
		
		if(lamp.getId() <= 0){
			throw new RuntimeException("Local lamp has an invalid id: " + lamp.getId());
		}
	}
	private void assertValidLamp(Lamp lamp) {
		String message = "Invalid lamp value! ";
		if(this.lamp != null && lamp.getId() != this.lamp.getId()){
			throw new RuntimeException(message + "Lamp is not the expected! This id is " + this.lamp.getId()
					+ " and received is: " + lamp.getId());
		}
	}

	/* ************************************* View ***************************************************/
	public void updateView() {
		if(viewsReady){
			updateLampStatus();
			nameView.setText(lamp != null ? lamp.getName() : "");
			consumptionGraphFragment.addConsumptionHistory(
					lamp != null ? lamp.getConsumptionHistory() : null);
		}
	}
	
	private void updateLampStatus() {
		boolean lampIsOn = (lamp != null && lamp.isOn());
		lampIconView.setImageResource( lampIsOn? R.drawable.ic_lamp_on : R.drawable.ic_lamp_off);
		
		powerControl.setOnCheckedChangeListener(null);
		powerControl.setChecked(lampIsOn);
		powerControl.setOnCheckedChangeListener(this);
		if(!lampIsOn){
			brightControl.setProgress(0);
		}
		else{
			brightControl.setProgress((int)(lamp.getBright()*brightControl.getMax()));
			//Define alpha para ícone da imagem baseado no seu brilho
			ViewCompat.setAlpha(lampIconView, (1f - MIN_ICON_ALPHA)*lamp.getBright() + MIN_ICON_ALPHA);
		}
	}
	/* ******************************* OnCheckedChangeListener *********************************/
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		if(lamp != null && lamp.isValid()){
			LampController.getInstance().requestChangePower(lamp, isChecked);
		}
	}

}
