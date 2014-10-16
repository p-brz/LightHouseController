package com.example.lighthousecontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.Handler;
import android.util.Log;

public class LampController {
	private static final String LOG_TAG = LampController.class.getName();
	
	public interface LampCollectionObserver {
		void onLampCollectionChange(List<ApplianceGroup> lampGroups);
	}
	public interface LampObserver {
		void changedPowerStatus(Lamp lamp);
		void changedBright(Lamp lamp);
	}
	public interface ConsumptionObserver {
		public void onConsumption(ConsumptionEvent event);
	}
	
	private static LampController singleton;
	public static LampController getInstance() {
		if(singleton == null){
			singleton = new LampController();
		}
		return singleton;
	}

	private final List<ConsumptionObserver> consumptionObservers;
	private final List<LampCollectionObserver> lampCollectionObservers;
	private LampControllerConsumptionSimulator consumptionSimulator;
	private final List<Lamp> lamps;
	private final List<ApplianceGroup> groups;

	private final List<LampObserver> lampObservers;
	
	public LampController() {
		super();
		consumptionObservers = new ArrayList<>();
		lampCollectionObservers = new ArrayList<>();
		lampObservers = new ArrayList<>();
		consumptionSimulator = new LampControllerConsumptionSimulator();
		lamps = new ArrayList<>();
		groups = new ArrayList<>();
	}
	/* ***************************************** Listeners ********************************************/
	public void registerObserver(ConsumptionObserver observer) {
		this.consumptionObservers.add(observer);
		startSimulator();
	}
	public void unregisterObserver(ConsumptionObserver observer) {
		this.consumptionObservers.remove(observer);
		stopSimulator();
	}
	public List<ConsumptionObserver> getConsumptionObservers() {
		return new CopyOnWriteArrayList<>(this.consumptionObservers);
	}

	public void registerLampObserver(LampObserver observer) {
		this.lampObservers.add(observer);
		startSimulator();
	}
	public void unregisterLampObserver(LampObserver observer) {
		this.lampObservers.remove(observer);
		stopSimulator();
	}
	public List<LampObserver> getLampObservers() {
		return new CopyOnWriteArrayList<>(this.lampObservers);
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
		for(LampObserver observer : this.lampObservers){
			observer.changedPowerStatus(lamp);
		}
	}
	private void fireChangeBright(Lamp lamp) {
		for(LampObserver observer : this.lampObservers){
			observer.changedBright(lamp);
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
}
