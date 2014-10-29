package com.example.lighthousecontroller.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.lighthousecontroller.R;
import com.example.lighthousecontroller.controller.LampCollectionObserver;
import com.example.lighthousecontroller.controller.LampObserver;
import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.Lamp;
import com.example.lighthousecontroller.view.LampDetailsActivity;

public class LampDAO {
	private static final int CHANGEBRIGHT_NOTIFICATION = 0;
	private static final int CHANGEPOWER_NOTIFICATION = 1;

	private static final float CHANGE_THRESHOLD = 0.1f;
	private final List<Lamp> lamps;
	private final List<ApplianceGroup> groups;

	private final List<LampCollectionObserver> lampCollectionObservers;
	private final Map<Long, List<LampObserver> > lampObservers;
	
	private Context context;

	private LampTable lampTable;
			
	public LampDAO(Context context) {
		Log.d(getClass().getName(), "Create LampDAO");
		if(context == null){
			throw new IllegalStateException("Context should not be null!");
		}
		
		lamps = new ArrayList<>();
		groups = new ArrayList<>();
		lampObservers = new HashMap<>();
		lampCollectionObservers = new ArrayList<>();	
		this.context = context;
		
		lampTable = new LampTable();
	}

	public void addLampObserver(LampObserver observer, Long lampId) {
		Log.d(getClass().getName(), "add lamp observer to lamp id: " + lampId);
		if(!lampObservers.containsKey(lampId)){
			lampObservers.put(lampId, new ArrayList<LampObserver>());
		}
		this.lampObservers.get(lampId).add(observer);
	}
	public void removeLampObserver(LampObserver observer, Long lampId) {
		Log.d(getClass().getName(), "remove lamp observer to lamp id: " + lampId);
		if(lampObservers.containsKey(lampId)){
			this.lampObservers.get(lampId).remove(observer);
		}
	}
	public void addLampCollectionObserver(LampCollectionObserver observer) {
		this.lampCollectionObservers.add(observer);
	}
	public void removeLampCollectionObserver(LampCollectionObserver observer) {
		this.lampCollectionObservers.remove(observer);
	}


	public Collection<? extends Table> getTables() {
		return Collections.singletonList(lampTable);
	}
	public List<Lamp> getLamps() {
		return new CopyOnWriteArrayList<>(this.lamps);
	}
	public List<ApplianceGroup> getGroups() {
		return new CopyOnWriteArrayList<>(this.groups);
	}
	public Lamp getLamp(long id) {
		for(Lamp lamp : this.lamps){
			if(lamp.getId() == id){
				return lamp;
			}
		}
		return null;
	}

	public void insertGroups(List<ApplianceGroup> groups) {
		this.groups.addAll(groups);
		for(ApplianceGroup group : groups){
			this.lamps.addAll(group.getAppliances());
		}

		notifyLampCollectionChange();
	}
	private void notifyLampCollectionChange() {
		for(LampCollectionObserver observer : this.lampCollectionObservers){
			observer.onLampCollectionChange(getGroups());
		}
	}

	public void updateLamp(Lamp newLamp) {
		Lamp oldLamp = getLamp(newLamp.getId());
		if(oldLamp == null){
			throw new IllegalArgumentException("Lâmpada de id " + newLamp.getId() + " não existe!");
		}
		
		if(Math.abs(newLamp.getBright() - oldLamp.getBright()) > CHANGE_THRESHOLD){
			Log.d(getClass().getName(),"Bright changed!");
			fireChangeBright(new Lamp(newLamp));
		}
		if(newLamp.isOn() != oldLamp.isOn()){
			fireChangePower(new Lamp(newLamp));
		}
		
		
		oldLamp.set(newLamp);
	}

	private void fireChangePower(Lamp lamp) {
		assert lamp != null;
		
		
		
		List<LampObserver> observers = new ArrayList<LampObserver>();
		if(lampObservers.containsKey(lamp.getId())){
			observers.addAll(this.lampObservers.get(lamp.getId()));
		}
		if(lampObservers.containsKey(null)){
			observers.addAll(this.lampObservers.get(null));
		}
		for(LampObserver observer : observers){
			observer.changedPowerStatus(lamp);
		}
		createChangePowerNotification(lamp, lamp.isOn());
	}
	
	private void fireChangeBright(Lamp lamp) {
		assert lamp != null;
		if(lampObservers.containsKey(lamp.getId())){
			for(LampObserver observer : this.lampObservers.get(lamp.getId())){
				Log.d(getClass().getName(),"Bright changed! Notify LampObserver : " + observer);
				observer.changedBright(lamp);
			}
		}
		if(lampObservers.containsKey(null)){
			for(LampObserver observer : this.lampObservers.get(null)){
				Log.d(getClass().getName(),"Bright changed! Notify LampObserver : " + observer);
				observer.changedBright(lamp);
			}
		}
		
		createChangeBrightNotification(lamp, lamp.getBright());
	}
	

	
	/* ********************************Notificações*********************************************/

	private void createChangeBrightNotification(Lamp someLamp, float newBright) {
		if(context == null){
			throw new IllegalStateException("Context should not be null!");
		}

		Intent intent = new Intent(context, LampDetailsActivity.class);
		intent.putExtra(LampDetailsActivity.LAMP_ARGUMENT, someLamp);
		intent.setAction("foo");
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n  = new NotificationCompat.Builder(context)
		        .setContentTitle("Lâmpada " + someLamp.getName() + " mudou o brilho.")
		        .setContentText("Novo brilho: " + String.valueOf(newBright*100) + "%")
		        .setSmallIcon(R.drawable.ic_logo)
		        .setContentIntent(pIntent)
		        .setAutoCancel(true).build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(CHANGEBRIGHT_NOTIFICATION, n);
	}
	private void createChangePowerNotification(Lamp someLamp, boolean changedTo) {
		if(context == null){
			throw new IllegalStateException("Context should not be null!");
		}
		Lamp lamp = getLamp(someLamp.getId());
		
		Log.d(getClass().getName(), "Create nofitication to lamp of id "+lamp.getId()+": " + lamp);
		
		
		Intent intent = new Intent(context, LampDetailsActivity.class);
		intent.putExtra(LampDetailsActivity.LAMP_ARGUMENT, lamp);
		intent.setAction("foo");
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n  = new NotificationCompat.Builder(context)
		        .setContentTitle("Lâmpada " + someLamp.getName() + " foi " + (changedTo ? "ligada." : "desligada."))
		        .setSmallIcon(R.drawable.ic_logo)
		        .setContentIntent(pIntent)
		        .setAutoCancel(true).build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(CHANGEPOWER_NOTIFICATION, n);
	}
}
