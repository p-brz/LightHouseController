package com.example.lighthousecontroller.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.lighthousecontroller.data.Data;
import com.example.lighthousecontroller.homeshell.LampHomeShellClient;
import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.ConsumptionEvent;
import com.example.lighthousecontroller.model.Lamp;

public class LampService extends LongLivedIntentService{
	
	/* Identificadores para ações utilizados por clientes para fazer requisições */
	private static final String CLASS_NAME         = LampService.class.getName();
	public static final String GET_GROUPS          = CLASS_NAME + ".GET_GROUPS";
	public static final String GET_LAMP            = CLASS_NAME + ".GET_LAMP";
	public static final String UPDATE_LAMP  	   = CLASS_NAME + ".UPDATE_LAMP";
	public static final String CHANGE_LAMP_POWER   = CLASS_NAME + ".CHANGE_LAMP_POWER";
	public static final String CHANGE_LAMP_BRIGHT  = CLASS_NAME + ".CHANGE_LAMP_BRIGHT";

	/* Identificadores para ações de BroadcastReceivers que recebem resultados de requisições */
	public static final String RECEIVE_GROUPS       = CLASS_NAME + ".RECEIVE_GROUPS";
	public static final String RECEIVE_LAMP         = CLASS_NAME + ".RECEIVE_LAMP";
	public static final String RECEIVE_CHANGEPOWER  = CLASS_NAME + ".RECEIVE_CHANGEPOWER";
	public static final String RECEIVE_CHANGEBRIGHT = CLASS_NAME + ".RECEIVE_CHANGEBRIGHT";
	public static final String RECEIVE_CONSUMPTION  = CLASS_NAME + ".RECEIVE_CONSUMPTION";
	
	/* Identificadores de Extras */
	public static final String GROUPS_DATA  = CLASS_NAME + ".GROUPS_DATA";
	public static final String LAMP_ID_DATA = CLASS_NAME + ".LAMP_ID_DATA";
	public static final String LAMP_DATA    = CLASS_NAME + ".LAMP_DATA";
	public static final String POWER_DATA   = CLASS_NAME + ".POWER_DATA";
	public static final String BRIGHT_DATA  = CLASS_NAME + ".BRIGHT_DATA";
	public static final String CONSUMPTIONLIST_DATA  = CLASS_NAME + ".CONSUMPTIONLIST_DATA";

	@Override
	protected void onHandleIntent(Intent intent, int startId) {
		if(intent == null){
			return;
		}
		if(intent.getAction().equals(GET_LAMP)){
			getLamp(intent);
		}
		else if(intent.getAction().equals(UPDATE_LAMP)){
			getLamp(intent);
			updateLampConsumption(intent);
		}
		else if(intent.getAction().equals(GET_GROUPS)){
			getGroups(intent);
		}
		else if(intent.getAction().equals(CHANGE_LAMP_POWER)){
			changeLampPower(intent);
		}
		else if(intent.getAction().equals(CHANGE_LAMP_BRIGHT)){
			changeLampBright(intent);
		}
		else{//Notificar erro?
			return;
		}
	}

	private void getGroups(Intent intent) {		
		List<ApplianceGroup> groups = LampHomeShellClient.instance().getGroups();

		if(groups == null){
			return;
		}
		Log.d(getClass().getName(),"Get groups from HomeShellClient: " + groups);
		
		for(ApplianceGroup group : groups){
			if(group == null){
				continue;
			}
			Log.d(getClass().getName(),"Lamps in group " + group.getName() +": " + group.getAppliances());
		}
		
		Intent responseIntent = new Intent();
		responseIntent.setAction(RECEIVE_GROUPS);
		responseIntent.putExtra(GROUPS_DATA, (Serializable)groups);
		LocalBroadcastManager.getInstance(this).sendBroadcast(responseIntent);
	}
	private void getLamp(Intent intent) {
		long lampId = intent.getLongExtra(LAMP_ID_DATA, 0);
		if(lampId > 0){
			Lamp lamp = LampHomeShellClient.instance().getLamp(lampId);
			Intent responseIntent = new Intent();
			responseIntent.setAction(RECEIVE_LAMP);
			responseIntent.putExtra(LAMP_DATA, lamp);
			LocalBroadcastManager.getInstance(this).sendBroadcast(responseIntent);
		}
		else{
			//FIXME: gerar erro?
		}
	}
	private void updateLampConsumption(Intent intent) {
		long lampId = intent.getLongExtra(LAMP_ID_DATA, 0);
		if(lampId > 0){
//			Toast.makeText(this, "Update lamp consumption to lamp " + lampId, Toast.LENGTH_SHORT).show();
			ConsumptionEvent event = Data.instance().getLampDAO().getLastConsumptionEvent(lampId);
			Date date = new Date(event.getTimestamp());
//			Toast.makeText(this, "Lamp " + lampId + " last consumption event at: " 
//						+ date.getHours() + "h " + date.getMinutes() + ":" + date.getSeconds(), Toast.LENGTH_SHORT).show();
//			
			
			List<ConsumptionEvent> consumptionHistory = LampHomeShellClient.instance().getLampConsumption(lampId, event);
//			
//			ConsumptionEvent fakeEvent = new ConsumptionEvent(lampId, new Date().getTime(), 0.5);
//			ArrayList<ConsumptionEvent> fakeList = new ArrayList<ConsumptionEvent>();
//			fakeList.add(fakeEvent);
			
			Intent responseIntent = new Intent();
			responseIntent.setAction(RECEIVE_CONSUMPTION);
			responseIntent.putExtra(LAMP_ID_DATA, lampId);
			responseIntent.putExtra(CONSUMPTIONLIST_DATA, (Serializable)consumptionHistory);
			LocalBroadcastManager.getInstance(this).sendBroadcast(responseIntent);
		}
		else{
			//FIXME: gerar erro?
		}
	}
	private void changeLampPower(Intent intent) {
		
		long lampId = intent.getLongExtra(LAMP_ID_DATA, 0);
		
		Log.d("Lâmpada", "" + lampId);
		
		if(lampId > 0 && intent.hasExtra(POWER_DATA)){
			
			boolean on = intent.getBooleanExtra(POWER_DATA, false);
			try{
				Log.d(getClass().getName(), "Executar!!!");
				Lamp lamp = LampHomeShellClient.instance().changeLampPower(lampId, on);
				Intent responseIntent = new Intent();
				responseIntent.setAction(RECEIVE_CHANGEPOWER);
				responseIntent.putExtra(LAMP_DATA, lamp);
				LocalBroadcastManager.getInstance(this).sendBroadcast(responseIntent);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else{
			//FIXME: gerar erro?
		}
	}
	private void changeLampBright(Intent intent) {
		long lampId = intent.getLongExtra(LAMP_ID_DATA, 0);
		float bright = intent.getFloatExtra(BRIGHT_DATA, -1);
		if(lampId > 0 && (bright >=0 && bright <= 1)){
			try{
				Lamp lamp = LampHomeShellClient.instance().changeLampBright(lampId, bright);
				
				Intent responseIntent = new Intent();
				responseIntent.setAction(RECEIVE_CHANGEBRIGHT);
				responseIntent.putExtra(LAMP_DATA, lamp);
				LocalBroadcastManager.getInstance(this).sendBroadcast(responseIntent);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else{
			//FIXME: gerar erro?
		}
	}





}
