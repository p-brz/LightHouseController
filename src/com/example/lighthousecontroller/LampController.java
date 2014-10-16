package com.example.lighthousecontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.lighthousecontroller.view.LampDetailsActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LampController {
	private static final String LOG_TAG = LampController.class.getName();
	private static final int CHANGEBRIGHT_NOTIFICATION = 0;
	private static final int CHANGEPOWER_NOTIFICATION = 1;
	
	public interface LampCollectionObserver {
		void onLampCollectionChange(List<ApplianceGroup> lampGroups);
	}
	public interface LampObserver {
		void changedPowerStatus(Lamp lamp);
		void changedBright(Lamp lamp);
		void lampUpdated(Lamp lamp);
	}
	public interface ConsumptionObserver {
		public void onConsumption(List<ConsumptionEvent> event);
	}
	
	private static LampController singleton;
	public static LampController getInstance() {
		if(singleton == null){
			singleton = new LampController();
		}
		return singleton;
	}

	private final List<LampCollectionObserver> lampCollectionObservers;
	private final Map<Long, List<ConsumptionObserver>> consumptionObservers;
	private final Map<Long, List<LampObserver> > lampObservers;
	private LampControllerConsumptionSimulator consumptionSimulator;
	private final List<Lamp> lamps;
	private final List<ApplianceGroup> groups;

	
	
	public Context context;
	
	
	public LampController() {
		super();
		consumptionObservers = new HashMap<>();
		lampObservers = new HashMap<>();
		lampCollectionObservers = new ArrayList<>();
		consumptionSimulator = new LampControllerConsumptionSimulator();
		lamps = new ArrayList<>();
		groups = new ArrayList<>();
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
		if(!lampObservers.containsKey(lampId)){
			lampObservers.put(lampId, new ArrayList<LampObserver>());
		}
		this.lampObservers.get(lampId).add(observer);
		startSimulator();
	}
	public void removeLampObserver(LampObserver observer, Long lampId) {
		if(lampObservers.containsKey(lampId)){
			this.lampObservers.get(lampId).remove(observer);
		}
		stopSimulator();
	}

	public void addLampCollectionObserver(LampCollectionObserver observer) {
		this.lampCollectionObservers.add(observer);
	}
	public void removeLampCollectionObserver(LampCollectionObserver observer) {
		this.lampCollectionObservers.remove(observer);
	}
	
	/* ***********************************************************************************************/
	public List<ApplianceGroup> getGroups() {
		//FIXME: simulando tempo de carregamento/atualização de grupos
		if(groups.isEmpty()){
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					generateData();
					notifyLampCollectionChange();
				}
			}, 800);
		}
		return new CopyOnWriteArrayList<>(this.groups);
	}
	public List<Lamp> getLamps() {
		return new CopyOnWriteArrayList<>(this.lamps);
	}
	public Lamp getLampStatus(Lamp lamp) {
		// TODO Auto-generated method stub
		return lamp;
	}
	
	public void requestChangePower(Lamp lamp, boolean on) {
		this.requestChangePower(lamp, on, null);
	}
	public void requestChangePower(Lamp someLamp, boolean on, LampObserver lampObserver) {
		//FIXME: Aqui será enviada requisição ao servidor para apagar ou acender lâmpada
		Lamp lamp = getLamp(someLamp.getId());
		if(!lamp.isOn() && on==true){
			lamp.setBright(1f);
		}
		else if(lamp.isOn() && on==false){
			lamp.setBright(0f);
		}
		lamp.setOn(on);
		if(lampObserver != null){
			lampObserver.changedPowerStatus(lamp);
		}
		fireChangePower(lamp);
	}

	public void requestChangeBright(Lamp lamp, float bright) {
		this.requestChangeBright(lamp, bright, null);
	}
	public void requestChangeBright(Lamp someLamp, float bright, LampObserver lampObserver) {
		//FIXME: Aqui será enviada requisição ao servidor para modificar o brilho da lâmpada

		Lamp lamp = getLamp(someLamp.getId());
		lamp.setBright(bright);
		lamp.setOn(bright > 0);
		if(lampObserver != null){
			lampObserver.changedBright(lamp);
		}
		fireChangeBright(lamp);
	}

	/* ******************************** Private methods************************************************/
	private Lamp getLamp(long id) {
		for(Lamp lamp : this.lamps){
			if(lamp.getId() == id){
				return lamp;
			}
		}
		return null;
	}
	private void fireChangePower(Lamp lamp) {
		assert lamp != null;
		List<LampObserver> observers = new ArrayList<LampController.LampObserver>();
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
				observer.changedBright(lamp);
			}
		}
		if(lampObservers.containsKey(null)){
			for(LampObserver observer : this.lampObservers.get(null)){
				observer.changedBright(lamp);
			}
		}
		
		createChangeBrightNotification(lamp, lamp.getBright());
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

	private void notifyLampCollectionChange() {
		for(LampCollectionObserver observer : this.lampCollectionObservers){
			observer.onLampCollectionChange(getGroups());
		}
	}
    private void generateData(){ 
		Random random = new Random();
        int lampCount = 1;
        List<Lamp> lampadasDaSala = Arrays.asList(new Lamp[] {
    			new Lamp(lampCount++, "Lâmpada da Sala", random.nextBoolean())
      		  , new Lamp(lampCount++, "Lâmpada da Copa", random.nextBoolean())});
        List<Lamp> lampadasDaCozinha = Arrays.asList(new Lamp[] {
          			new Lamp(lampCount++, "Principal", random.nextBoolean())
          		  , new Lamp(lampCount++, "Lâmpada da Varanda", random.nextBoolean())});
        List<Lamp> lampadasDoQuarto = Arrays.asList(new Lamp[] {
    			new Lamp(lampCount++, "Minha Lâmpada", random.nextBoolean())});

        this.lamps.clear();
        this.lamps.addAll(lampadasDaSala);
        this.lamps.addAll(lampadasDaCozinha);
        this.lamps.addAll(lampadasDoQuarto);
        
        this.groups.clear();
        groups.add(new ApplianceGroup("Sala", lampadasDaSala));
        groups.add(new ApplianceGroup("Cozinha", lampadasDaCozinha));
        groups.add(new ApplianceGroup("Quarto", lampadasDoQuarto));
    }
	private void startSimulator() {
		if(!consumptionSimulator.isRunning() && 
				(!consumptionObservers.isEmpty() || !lampObservers.isEmpty())){
			Log.d(LOG_TAG, "Request consumption start");
			consumptionSimulator.startConsumptionReceiver(this);
		}
	}
	private void stopSimulator() {
		if(consumptionSimulator.isRunning() && consumptionObservers.isEmpty() && lampObservers.isEmpty()){
			consumptionSimulator.stopConsumptionReceiver();
		}
	}
	
	/* ********************************Notificações*********************************************/

	/* ************************************************************************************************/
	private void createChangeBrightNotification(Lamp someLamp, float newBright) {
		if(context != null){
			Intent intent = new Intent(context, LampDetailsActivity.class);
			intent.putExtra(LampDetailsActivity.LAMP_ARGUMENT, someLamp);
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
	}
	private void createChangePowerNotification(Lamp someLamp, boolean changedTo) {
		Log.d(LOG_TAG, someLamp.getName());
		if(context == null){
			return;
		}
		Intent intent = new Intent(context, LampDetailsActivity.class);
		intent.putExtra(LampDetailsActivity.LAMP_ARGUMENT, someLamp);
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
