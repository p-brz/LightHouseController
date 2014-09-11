package com.example.lighthousecontroller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter.CreateBeamUrisCallback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
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

import com.example.lighthousecontroller.LampController.ConsumptionObserver;
import com.example.lighthousecontroller.LampController.LampObserver;

public class LampDetailsFragment extends Fragment implements ConsumptionObserver, LampObserver{
	public class BrightControlChanged implements OnSeekBarChangeListener {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if(fromUser && lamp != null){
//				LampController.getInstance()
//				  .requestChangeBright(lamp, progress /((float)seekBar.getMax())
//						  				   , LampDetailsFragment.this);
				LampController.getInstance()
				  .requestChangeBright(lamp, progress /((float)seekBar.getMax()));
			}
		}
	}
	private static final float MIN_ICON_ALPHA = 0.3f;

	private static final int CHANGEBRIGHT_NOTIFICATION = 0;
	private static final int CHANGEPOWER_NOTIFICATION = 1;

	private Lamp lamp;
	
	private ImageView lampIconView;
	private ToggleButton powerControl;
	private SeekBar brightControl;
	private TextView nameView;
	
	private ConsumptionGraphFragment consumptionGraphFragment;
	
	private boolean viewsReady;
	
	public LampDetailsFragment() {
		consumptionGraphFragment = new ConsumptionGraphFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_lamp_details,
				container, false);
		
		setupViews(rootView);
		populateViews();
		
		return rootView;
	}

	
	@Override
	public void onResume() {
		super.onResume();
//		if(lamp != null && lamp.getId() > 0){
//			LampController.getInstance().registerObserver(this);
//		}
		LampController.getInstance().registerObserver(this);
		LampController.getInstance().registerLampObserver(this);
	}
	@Override
	public void onPause() {
		super.onPause();
		LampController.getInstance().unregisterObserver(this);
		LampController.getInstance().unregisterLampObserver(this);
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
		
		powerControl.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(getTag(), "checked change");
				if(lamp != null && lamp.isValid()){
//					LampController.getInstance().requestChangePower(lamp, isChecked, LampDetailsFragment.this);
					LampController.getInstance().requestChangePower(lamp, isChecked);
//					buttonView.setChecked(lamp.isOn());
				}
			}
		});
		
		
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
			layoutLampStatus();
			nameView.setText(lamp != null ? lamp.getName() : "");
			consumptionGraphFragment.addConsumptionHistory(
					lamp != null ? lamp.getConsumptionHistory() : null);
		}
	}

	private void layoutLampStatus() {
		boolean lampIsOn = (lamp != null && lamp.isOn());
		lampIconView.setImageResource( lampIsOn? R.drawable.ic_lamp_on : R.drawable.ic_lamp_off);
		powerControl.setChecked(lampIsOn);
		if(!lampIsOn){
			brightControl.setProgress(0);
//			ViewCompat.setAlpha(lampIconView, 1f);
		}
		else{
			brightControl.setProgress((int)(lamp.getBright()*brightControl.getMax()));
			//Define alpha para ícone da imagem baseado no seu brilho
			ViewCompat.setAlpha(lampIconView, (1f - MIN_ICON_ALPHA)*lamp.getBright() + MIN_ICON_ALPHA);
		}
	}


	@Override
	public void onConsumption(ConsumptionEvent event) {
		// TODO Auto-generated method stub
//		Log.d(getTag(), "On Consumption = "
//							+ "lamp: "    + String.valueOf(event.getSourceId())
//							+ "; at: "    + String.valueOf(event.getTimestamp())
//							+ "; value: " + String.valueOf(event.getConsumption()) );	
		if(lamp !=null && event.getSourceId() == this.lamp.getId()){
			consumptionGraphFragment.plotConsumption(event);
		}
	}

	/* *********************************** LampObserver ************************************************/
	
	@Override
	public void changedPowerStatus(Lamp lamp) {
		createChangePowerNotification(lamp, lamp.isOn());
		
		if(this.lamp != null && lamp.getId() == this.lamp.getId()){
			this.lamp.setOn(lamp.isOn());
			layoutLampStatus();
		}
	}

	@Override
	public void changedBright(Lamp lamp) {
		this.createChangeBrightNotification(lamp, lamp.getBright());
		
		if(this.lamp != null && lamp.getId() == this.lamp.getId()){
			this.lamp.setBright(lamp.getBright());
			this.lamp.setOn(lamp.isOn());
			layoutLampStatus();
		}
	}
	
	


	private void createChangeBrightNotification(Lamp someLamp, float newBright) {
		Intent intent = new Intent(getActivity(), LampDetailsActivity.class);
		intent.putExtra(LampDetailsActivity.LAMP_ARGUMENT, someLamp);
		PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n  = new NotificationCompat.Builder(getActivity())
		        .setContentTitle("Lâmpada " + someLamp.getName() + " mudou o brilho.")
		        .setContentText("Novo brilho: " + String.valueOf(newBright*100) + "%")
		        .setSmallIcon(R.drawable.ic_logo)
		        .setContentIntent(pIntent)
		        .setAutoCancel(true).build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(CHANGEBRIGHT_NOTIFICATION, n);
	}
	private void createChangePowerNotification(Lamp someLamp, boolean changedTo) {
		Intent intent = new Intent(getActivity(), LampDetailsActivity.class);
		intent.putExtra(LampDetailsActivity.LAMP_ARGUMENT, someLamp);
		PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n  = new NotificationCompat.Builder(getActivity())
		        .setContentTitle("Lâmpada " + someLamp.getName() + " foi " + (changedTo ? "ligada." : "desligada."))
		        .setSmallIcon(R.drawable.ic_logo)
		        .setContentIntent(pIntent)
		        .setAutoCancel(true).build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(CHANGEPOWER_NOTIFICATION, n);
	}
}
