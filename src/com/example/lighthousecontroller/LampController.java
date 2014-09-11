package com.example.lighthousecontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

public class LampController {
	public interface LampObserver {
		void changedPowerStatus(Lamp lamp);

		void changedBright(Lamp lamp);
	}
	public interface ConsumptionObserver {
		public void onConsumption(ConsumptionEvent event);
	}

	private static final String LOG_TAG = LampController.class.getName();
	
	private static LampController singleton;
	public static LampController getInstance() {
		if(singleton == null){
			singleton = new LampController();
		}
		return singleton;
	}

	private final List<ConsumptionObserver> consumptionObservers;
	private LampControllerConsumptionSimulator consumptionSimulator;
	private final List<Lamp> lamps;
	private final List<ApplianceGroup> groups;
	
	public LampController() {
		super();
		consumptionObservers = new ArrayList<>();
		consumptionSimulator = new LampControllerConsumptionSimulator();
		lamps = new ArrayList<>();
		groups = new ArrayList<>();
		generateData();
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

        this.lamps.addAll(lampadasDaSala);
        this.lamps.addAll(lampadasDaCozinha);
        this.lamps.addAll(lampadasDoQuarto);
        
        groups.add(new ApplianceGroup("Sala", lampadasDaSala));
        groups.add(new ApplianceGroup("Cozinha", lampadasDaCozinha));
        groups.add(new ApplianceGroup("Quarto", lampadasDoQuarto));
    }
	/* ***************************************** Listeners ********************************************/
	public void registerObserver(ConsumptionObserver observer) {
		this.consumptionObservers.add(observer);
		if(!consumptionSimulator.isRunning() && !consumptionObservers.isEmpty()){
			Log.d(LOG_TAG, "Request consumption start");
			consumptionSimulator.startConsumptionReceiver(this.consumptionObservers, this.lamps);
		}
	}
	public void unregisterObserver(ConsumptionObserver observer) {
		this.consumptionObservers.remove(observer);
		if(consumptionSimulator.isRunning() && consumptionObservers.isEmpty()){
			consumptionSimulator.stopConsumptionReceiver();
		}
	}
	public List<Lamp> getLamps() {
		return new CopyOnWriteArrayList<>(this.lamps);
	}
	public void requestChangePower(Lamp lamp, boolean on, LampObserver lampObserver) {
		//FIXME: Aqui será enviada requisição ao servidor para apagar ou acender lâmpada
		if(!lamp.isOn() && on==true){
			lamp.setBright(1f);
		}
		else if(lamp.isOn() && on==false){
			lamp.setBright(0f);
		}
		lamp.setOn(on);
		lampObserver.changedPowerStatus(lamp);
	}
	public void requestChangeBright(Lamp lamp, float bright, LampObserver lampObserver) {
		//FIXME: Aqui será enviada requisição ao servidor para modificar o brilho da lâmpada

		lamp.setBright(bright);
		lamp.setOn(bright > 0);
		lampObserver.changedBright(lamp);
	}
	public List<ApplianceGroup> getGroups() {
		return new CopyOnWriteArrayList<>(this.groups);
	}
	
	
}
