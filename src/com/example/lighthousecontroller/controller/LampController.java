package com.example.lighthousecontroller.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.example.lighthousecontroller.data.Data;
import com.example.lighthousecontroller.homeshell.LampHomeShellClient;
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
	
	
	public LampController() {
		super();
		consumptionObservers = new HashMap<>();
		consumptionSimulator = new LampControllerConsumptionSimulator();
//		lamps = new ArrayList<>();
//		groups = new ArrayList<>();
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
	public void removeLampObserver(LampObserver observer) {
		removeLampObserver(observer,null);
	}
	public void addLampObserver(LampObserver observer, Long lampId) {
		Data.instance().getLampDAO().addLampObserver(observer, lampId);
		startSimulator();
	}
	public void removeLampObserver(LampObserver observer, Long lampId) {
		Data.instance().getLampDAO().removeLampObserver(observer, lampId);
		stopSimulator();
	}

	public void addLampCollectionObserver(LampCollectionObserver observer) {
		Data.instance().getLampDAO().addLampCollectionObserver(observer);
	}
	public void removeLampCollectionObserver(LampCollectionObserver observer) {
		Data.instance().getLampDAO().removeLampCollectionObserver(observer);
	}
	
	/* ***********************************************************************************************/
	boolean generatingData = false;
	public List<ApplianceGroup> getGroups() {
		List<ApplianceGroup> groups =  Data.instance().getLampDAO().getGroups();
		LampHomeShellClient.instance().updateGroups();
		
		return groups;
	}
	public List<Lamp> getLamps() {
		return Data.instance().getLampDAO().getLamps();
	}
	public Lamp getLampStatus(Lamp lamp) {
		Lamp storedLamp = Data.instance().getLampDAO().getLamp(lamp.getId());
		LampHomeShellClient.instance().updateLampStatus(storedLamp);
		
		return storedLamp;
	}
	
	public void requestChangePower(Lamp someLamp, boolean on) {
		LampHomeShellClient.instance().changeLampPower(someLamp, on);
		
//		Lamp lamp = Data.instance().getLampDAO().getLamp(someLamp.getId());
//		if(!lamp.isOn() && on==true){
//			lamp.setBright(1f);
//		}
//		else if(lamp.isOn() && on==false){
//			lamp.setBright(0f);
//		}
//		lamp.setOn(on);
//		fireChangePower(lamp);
	}

	public void requestChangeBright(Lamp someLamp, float bright) {
		LampHomeShellClient.instance().changeLampBright(someLamp, bright);
	}

	/* ******************************** Private methods************************************************/
	
	

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
