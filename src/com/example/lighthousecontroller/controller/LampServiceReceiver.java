package com.example.lighthousecontroller.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.lighthousecontroller.R;
import com.example.lighthousecontroller.data.Data;
import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.Lamp;
import com.example.lighthousecontroller.view.LampDetailsActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class LampServiceReceiver extends BroadcastReceiver {
		private static final int CHANGEBRIGHT_NOTIFICATION = 0;
		private static final int CHANGEPOWER_NOTIFICATION = 1;
	
		IntentFilter receiveGroupsFilter;
		IntentFilter receiveLampFilter;
		IntentFilter receiveChangeBrightFilter;
		IntentFilter receiveChangePowerFilter;

		private final List<LampCollectionObserver> lampCollectionObservers;
		private final Map<Long, List<LampObserver> > lampObservers;
		
		private Context context;
		
		public LampServiceReceiver(Context context) {
			receiveGroupsFilter       = new IntentFilter(LampService.RECEIVE_GROUPS);
			receiveLampFilter         = new IntentFilter(LampService.RECEIVE_LAMP);
			receiveChangeBrightFilter = new IntentFilter(LampService.RECEIVE_CHANGEBRIGHT);
			receiveChangePowerFilter  = new IntentFilter(LampService.RECEIVE_CHANGEPOWER);
			
			this.context = context;
			lampObservers = new HashMap<>();
			lampCollectionObservers = new ArrayList<>();	
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(LampService.RECEIVE_GROUPS)){
				receivedGroups(context, intent);
			}
			else if(intent.getAction().equals(LampService.RECEIVE_LAMP)){
				receiveLamp(context, intent);
			}
			else if(intent.getAction().equals(LampService.RECEIVE_CHANGEBRIGHT)){
				receiveChangeBright(context, intent);
				
			}
			else if(intent.getAction().equals(LampService.RECEIVE_CHANGEPOWER)){
				receiveChangePower(context, intent);	
			}
		}


		private void assertNotNull(Object obj) {
			if(obj == null){
				throw new NullPointerException();
			}
		}
		private void receiveChangePower(Context context, Intent intent) {
			Toast.makeText(context, "Updated power in lamp: " 
					+ intent.getSerializableExtra(LampService.LAMP_DATA)
						, Toast.LENGTH_LONG).show();
			Lamp receivedLamp = (Lamp)intent.getSerializableExtra(LampService.LAMP_DATA);
			
			Log.d(getClass().getName(), "receiveChangePower. Received Lamp: " + receivedLamp);
			
			assertNotNull(receivedLamp);

			Data.instance().getLampDAO().updateLamp(receivedLamp);
			fireChangePower(receivedLamp);
		}

		private void receiveChangeBright(Context context, Intent intent) {
			Toast.makeText(context, "Updated bright in lamp: " 
					+ intent.getSerializableExtra(LampService.LAMP_DATA)
						, Toast.LENGTH_LONG).show();
			Lamp receivedLamp = (Lamp)intent.getSerializableExtra(LampService.LAMP_DATA);
			Data.instance().getLampDAO().updateLamp(receivedLamp);
			fireChangeBright(receivedLamp);
		}

		private void receiveLamp(Context context, Intent intent) {
			Toast.makeText(context, "Received lamp: " 
									+ intent.getSerializableExtra(LampService.LAMP_DATA)
										, Toast.LENGTH_LONG).show();
			Lamp receivedLamp = (Lamp)intent.getSerializableExtra(LampService.LAMP_DATA);
			Data.instance().getLampDAO().updateLamp(receivedLamp);
			//TODO: notificar atualização da lâmpada
		}

		@SuppressWarnings("unchecked")
		private void receivedGroups(Context context, Intent intent) {
			Toast.makeText(context, "Received groups: " + intent.getSerializableExtra(LampService.GROUPS_DATA)
					, Toast.LENGTH_LONG).show();
			
			List<ApplianceGroup> lampGroups = (List<ApplianceGroup>)intent.getSerializableExtra(LampService.GROUPS_DATA);
			Data.instance().getLampDAO().insertOrUpdateGroups(lampGroups);
			
			
			notifyLampCollectionChange(lampGroups);
		}

		public void registerLocalIntentFilters(){
			LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);

			broadcastManager.registerReceiver(this, receiveGroupsFilter);
			broadcastManager.registerReceiver(this, receiveLampFilter);
			broadcastManager.registerReceiver(this, receiveChangeBrightFilter);
			broadcastManager.registerReceiver(this, receiveChangePowerFilter);
			
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
		

		private void notifyLampCollectionChange(List<ApplianceGroup> groups) {
			for(LampCollectionObserver observer : this.lampCollectionObservers){
				observer.onLampCollectionChange(groups);
			}
		}
		
		private void fireChangePower(Lamp lamp) {
			assertNotNull(lamp);
			
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
		private void createChangePowerNotification(Lamp lamp, boolean changedTo) {
			if(context == null){
				throw new IllegalStateException("Context should not be null!");
			}
//			Lamp lamp = getLamp(someLamp.getId());
			
			Log.d(getClass().getName(), "Create nofitication to lamp of id "+lamp.getId()+": " + lamp);
			
			
			Intent intent = new Intent(context, LampDetailsActivity.class);
			intent.putExtra(LampDetailsActivity.LAMP_ARGUMENT, lamp);
			intent.setAction("foo");
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
			

			// build notification
			// the addAction re-use the same intent to keep the example short
			Notification n  = new NotificationCompat.Builder(context)
			        .setContentTitle("Lâmpada " + lamp.getName() + " foi " + (changedTo ? "ligada." : "desligada."))
			        .setSmallIcon(R.drawable.ic_logo)
			        .setContentIntent(pIntent)
			        .setAutoCancel(true).build();
			    
			  
			NotificationManager notificationManager = 
			  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			notificationManager.notify(CHANGEPOWER_NOTIFICATION, n);
		}
	}