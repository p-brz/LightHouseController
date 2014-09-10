package com.example.lighthousecontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;
import android.widget.CompoundButton.OnCheckedChangeListener;

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
	
	public LampController() {
		super();
		consumptionObservers = new ArrayList<>();
		consumptionSimulator = new LampControllerConsumptionSimulator();
		lamps = new ArrayList<>();
		populateLamps();
	}
	private void populateLamps() {
		Random random = new Random();
		for(int i=0; i < 10; ++i){
			Lamp lamp = new Lamp();
			lamp.setId(i + 1);
			lamp.setOn(random.nextBoolean());
			if(lamp.isOn()){
				lamp.setBright(1f);
			}
			lamp.setName("Lamp" + String.valueOf(i));
			lamps.add(lamp);
		}
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
	
	
}
