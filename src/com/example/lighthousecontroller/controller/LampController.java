package com.example.lighthousecontroller.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.lighthousecontroller.LightHouseControllerApplication;
import com.example.lighthousecontroller.data.Data;
import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.ConsumptionEvent;
import com.example.lighthousecontroller.model.Lamp;

public class LampController {
	private static final String LOG_TAG = LampController.class.getName();
	
	private static LampController singleton;
	public static LampController getInstance() {
		if(singleton == null){
			singleton = new LampController();
		}
		return singleton;
	}

	private final Map<Long, List<ConsumptionObserver>> consumptionObservers;
	private LampControllerConsumptionSimulator consumptionSimulator;
//	private final List<Lamp> lamps;
//	private final List<ApplianceGroup> groups;

	LampServiceReceiver receiver;
	
	public LampController() {
		super();
		consumptionObservers = new HashMap<>();
		consumptionSimulator = new LampControllerConsumptionSimulator();
//		lamps = new ArrayList<>();
//		groups = new ArrayList<>();
		
		Context context = LightHouseControllerApplication.getApplication();
		receiver = new LampServiceReceiver(context);
		receiver.registerLocalIntentFilters();
	}
	/* ***************************************** Listeners ********************************************/
	public void addConsumptionObserver(ConsumptionObserver observer) {
		addConsumptionObserver(observer, null);
	}
	public void removeConsumptionObserver(ConsumptionObserver observer) {
		removeConsumptionObserver(observer, null);
	}
	public void addConsumptionObserver(ConsumptionObserver observer, Long lampId) {
		if(!consumptionObservers.containsKey(lampId)){
			consumptionObservers.put(lampId, new ArrayList<ConsumptionObserver>());
		}
		this.consumptionObservers.get(lampId).add(observer);
		startSimulator();
	}
	public void removeConsumptionObserver(ConsumptionObserver observer, Long lampId) {
		if(consumptionObservers.containsKey(lampId)){
			this.consumptionObservers.get(lampId).remove(observer);
		}
		stopSimulator();
	}

	public void addLampObserver(LampObserver observer) {
		addLampObserver(observer,null);
	}
	public void addLampObserver(LampObserver observer, Long lampId) {
//		Data.instance().getLampDAO().addLampObserver(observer, lampId);
		receiver.addLampObserver(observer, lampId);
		startSimulator();
	}
	public void removeLampObserver(LampObserver observer) {
		removeLampObserver(observer,null);
	}
	public void removeLampObserver(LampObserver observer, Long lampId) {
//		Data.instance().getLampDAO().removeLampObserver(observer, lampId);
		receiver.removeLampObserver(observer, lampId);
		stopSimulator();
	}

	public void addLampCollectionObserver(LampCollectionObserver observer) {
//		Data.instance().getLampDAO().addLampCollectionObserver(observer);
		receiver.addLampCollectionObserver(observer);
	}
	public void removeLampCollectionObserver(LampCollectionObserver observer) {
//		Data.instance().getLampDAO().removeLampCollectionObserver(observer);
		receiver.removeLampCollectionObserver(observer);
	}
	
	/* ***********************************************************************************************/
	boolean generatingData = false;
	public List<ApplianceGroup> getGroups() {
		List<ApplianceGroup> groups =  Data.instance().getLampDAO().getGroups();
//		LampHomeShellClient.instance().getGroups();
		
		sendGetGroupsRequest();
		
		return groups;
	}
	//TODO: Por hora este método é apenas utilizado localmente (por LampControllerConsumptionSimulator): checar necessidade dele
	List<Lamp> getLamps() {
		return Data.instance().getLampDAO().getLamps();
	}
	public Lamp getLampStatus(Lamp lamp) {
		Lamp storedLamp = Data.instance().getLampDAO().getLamp(lamp.getId());
//		LampHomeShellClient.instance().updateLampStatus(storedLamp);
		
		sendGetLampRequest(lamp.getId());
		
		return storedLamp;
	}
	
	public void requestChangePower(Lamp someLamp, boolean on) {
//		LampHomeShellClient.instance().changeLampPower(someLamp, on);
		sendChangePowerRequest(someLamp.getId(), on);
	}

	public void requestChangeBright(Lamp someLamp, float bright) {
//		LampHomeShellClient.instance().changeLampBright(someLamp, bright);
		sendChangeBright(someLamp.getId(), bright);
	}
	/* ******************************** Private methods************************************************/
	private void sendGetGroupsRequest() {
		Log.d(getClass().getName(), "sendGetGroupsRequest");
		Context context = LightHouseControllerApplication.getApplication();
		Intent requestIntent = new Intent(context, LampService.class);
		requestIntent.setAction(LampService.GET_GROUPS);
		
		context.startService(requestIntent);
	}
	private void sendGetLampRequest(long lampId) {
		Log.d(getClass().getName(), "sendGetLampRequest");
		Context context = LightHouseControllerApplication.getApplication();
		Intent requestIntent = new Intent(context, LampService.class);
		requestIntent.setAction(LampService.GET_LAMP);
		requestIntent.putExtra(LampService.LAMP_ID_DATA, lampId);

		context.startService(requestIntent);
	}
	private void sendChangePowerRequest(long id, boolean on) {
		Log.d(getClass().getName(), "sendChangePowerRequest");
		Context context = LightHouseControllerApplication.getApplication();
		Intent requestIntent = new Intent(context, LampService.class);
		requestIntent.setAction(LampService.CHANGE_LAMP_POWER);
		requestIntent.putExtra(LampService.LAMP_ID_DATA, id);
		requestIntent.putExtra(LampService.POWER_DATA, on);

		context.startService(requestIntent);
	}
	private void sendChangeBright(long id, float bright) {
		Log.d(getClass().getName(), "sendChangeBrightRequest");
		Context context = LightHouseControllerApplication.getApplication();
		Intent requestIntent = new Intent(context, LampService.class);
		requestIntent.setAction(LampService.CHANGE_LAMP_BRIGHT);
		requestIntent.putExtra(LampService.LAMP_ID_DATA, id);
		requestIntent.putExtra(LampService.BRIGHT_DATA, bright);

		context.startService(requestIntent);
	}

	void fireConsumptionEvent(ConsumptionEvent event) {
		List<ConsumptionEvent> events = Collections.singletonList(event);
		if(consumptionObservers.containsKey(event.getSourceId())){
			for(ConsumptionObserver observer : consumptionObservers.get(event.getSourceId())){
				observer.onConsumption(events);
			}
			
		}//Notifica observadores "broadcast"
		if(consumptionObservers.containsKey(null)){
			for(ConsumptionObserver observer : consumptionObservers.get(null)){
				observer.onConsumption(events);
			}
		}
	}

	
	private void startSimulator() {
		if(!consumptionSimulator.isRunning() && 
				(!consumptionObservers.isEmpty())){
			Log.d(LOG_TAG, "Request consumption start");
			consumptionSimulator.startConsumptionReceiver(this);
		}
	}
	private void stopSimulator() {
		if(consumptionSimulator.isRunning() && consumptionObservers.isEmpty()){
			consumptionSimulator.stopConsumptionReceiver();
		}
	}
}
