package com.example.lighthousecontroller;

import java.util.Date;
import java.util.List;
import java.util.Random;

import android.os.Handler;
import android.util.Log;

import com.example.lighthousecontroller.LampController.ConsumptionObserver;

public class LampControllerConsumptionSimulator {
	private static final int MIN_CONSUMPTION_TIME = 1000;
	private static final int MAX_CONSUMPTION_TIME = 5000;
	private static final String LOG_TAG = LampControllerConsumptionSimulator.class.getName();
	private static final float CHANGE_CHANCE = 0.1f;
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
		
		if(lampShouldChange()){ //Mudar status de energia
			//Inverte estado da lâmpada
			boolean changedTo = !someLamp.isOn();
			controller.requestChangePower(someLamp, changedTo);
		}
		else if(lampShouldChange()){ //Mudar status de brilho
			float newBright = random.nextFloat();
			controller.requestChangeBright(someLamp, newBright);
		}
		ConsumptionEvent event =  calculateConsumption(someLamp);
		someLamp.addConsumptionEvent(event);
		fireConsumptionEvent(event);
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
	private boolean lampShouldChange() {
		float rand = this.random.nextFloat();
		return (rand <= CHANGE_CHANCE);
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
	
	protected void fireConsumptionEvent(ConsumptionEvent event) {
		List<ConsumptionObserver> consumptionObservers = controller.getConsumptionObservers();
		
		if(consumptionObservers == null || consumptionObservers.isEmpty()){
			return;
		}
		for(ConsumptionObserver observer : consumptionObservers){
			observer.onConsumption(event);
		}
	}
	private long randomBetween(int minValue, int maxValue) {
		return random.nextInt(maxValue - minValue) + minValue;
	}
	
}