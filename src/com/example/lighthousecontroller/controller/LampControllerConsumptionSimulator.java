package com.example.lighthousecontroller.controller;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.example.lighthousecontroller.model.ConsumptionEvent;
import com.example.lighthousecontroller.model.Lamp;

import android.os.Handler;
import android.util.Log;

public class LampControllerConsumptionSimulator {
	private static final int MIN_CONSUMPTION_TIME = 100;
	private static final int MAX_CONSUMPTION_TIME = 2000;
	private static final String LOG_TAG = LampControllerConsumptionSimulator.class.getName();
	private static final double LAMP_POWER = 60;
	private Handler handler;
	private volatile boolean running;
	private Random random;
	private LampController controller;
	
	public LampControllerConsumptionSimulator() {
		random = new Random();
		running = false;
	}
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return running;
	}
	public synchronized void startConsumptionReceiver(LampController lampController) {
		this.controller = lampController;
		
		if(handler == null){
			handler = new Handler();
		}
		final Runnable consumptionGenerator = new Runnable() {

			/* Este método deverá ser executado na mesma thread deste objeto (não é thread-safe)*/
			@Override
			public void run() {
				if(isRunning()){
					simulate();
					//Chama runnable para ser executado novamente
					handler.postDelayed(this, generateDelayTime());
				}
				else{
					running = false;
				}
			}

		};
		
		long delayTime = generateDelayTime();
		
		Log.d(LOG_TAG, "Post delayed runnable with delay = " + String.valueOf(delayTime));
		
		handler.postDelayed(consumptionGenerator, delayTime);
		running = true;
	}
	public synchronized void stopConsumptionReceiver() {
		running = false;
	}

	private void simulate() {
		Lamp someLamp = choiceRandomLamp();
		
		Log.d(getClass().getName(), "Simulando consumo para lâmpada: " + someLamp + " com id: " + someLamp.getId());
		
		ConsumptionEvent event =  calculateConsumption(someLamp);
		someLamp.addConsumptionEvent(event);
		controller.fireConsumptionEvent(event);
	}
	private ConsumptionEvent calculateConsumption(Lamp someLamp) {
		double consumption = 0;
		if(someLamp.isOn()){
			consumption = LAMP_POWER * getDeltaTimeInHours(someLamp) * someLamp.getBright();
		}
		return new ConsumptionEvent(someLamp.getId(), getTimestamp(), consumption);
	}
	private double getDeltaTimeInHours(Lamp someLamp) {
		List<ConsumptionEvent> consumptionHistory = someLamp.getConsumptionHistory();
		if(consumptionHistory.isEmpty()){
			return 0;
		}
		long lastTimeStamp = consumptionHistory.get(consumptionHistory.size() - 1).getTimestamp();
		
		return millisToHours(getTimestamp() - lastTimeStamp);
	}
	private double millisToHours(long millis) {
		return millis/(1000.0*3600);
	}
	private Lamp choiceRandomLamp() {
		List<Lamp> lamps = controller.getLamps();
		if(lamps == null || lamps.isEmpty()){
			Log.w(LOG_TAG, "Lamps null or empty: " + String.valueOf(lamps));
			return null;
		}
		return lamps.get(random.nextInt(lamps.size()));
	}
	protected long getTimestamp() {
		return new Date().getTime();
	}

	private long generateDelayTime() {
		return randomBetween(MIN_CONSUMPTION_TIME, MAX_CONSUMPTION_TIME);
	}
	
	private long randomBetween(int minValue, int maxValue) {
		return random.nextInt(maxValue - minValue) + minValue;
	}
	
}