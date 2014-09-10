package com.example.lighthousecontroller;

import java.util.Date;
import java.util.List;
import java.util.Random;

import android.os.Handler;
import android.util.Log;

import com.example.lighthousecontroller.LampController.ConsumptionObserver;

public class LampControllerConsumptionSimulator {
	private static final int MIN_CONSUMPTION_VALUE = 0;
	private static final int MAX_CONSUMPTION_VALUE = 200;
	private static final int MIN_CONSUMPTION_TIME = 0;
	private static final int MAX_CONSUMPTION_TIME = 1000;
	private static final String LOG_TAG = LampControllerConsumptionSimulator.class.getName();
	private List<ConsumptionObserver> consumptionObservers;
	private Handler handler;
	private boolean running;
	private Random random;
	private List<Lamp> lamps;

	public LampControllerConsumptionSimulator() {
		random = new Random();
		running = false;
	}
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return running;
	}
	public synchronized void startConsumptionReceiver(List<ConsumptionObserver> observers, List<Lamp> lamps) {
		consumptionObservers = observers;
		this.lamps = lamps;
		
		if(handler == null){
			handler = new Handler();
		}
		final Runnable consumptionGenerator = new Runnable() {

			/* Este método deverá ser executado na mesma thread deste objeto (não é thread-safe)*/
			@Override
			public void run() {
				Log.d(LOG_TAG, "Run");
				if(isRunning() && consumptionObservers != null && !consumptionObservers.isEmpty()){
					ConsumptionEvent event = 
							new ConsumptionEvent(generateLampId(), getTimestamp(), generateConsumptionValue());
					fireConsumptionEvent(event);
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
	protected void fireConsumptionEvent(ConsumptionEvent event) {
		if(consumptionObservers == null || consumptionObservers.isEmpty()){
			return;
		}
		for(ConsumptionObserver observer : this.consumptionObservers){
			observer.onConsumption(event);
		}
	}
	public synchronized void stopConsumptionReceiver() {
		running = false;
	}


	private long generateLampId() {
		if(lamps == null || lamps.isEmpty()){
			Log.w(LOG_TAG, "Lamps null or empty: " + String.valueOf(lamps));
			return 0;
		}
		return lamps.get(random.nextInt(lamps.size())).getId();
	}
	protected long getTimestamp() {
		return new Date().getTime();
	}
	private long generateConsumptionValue() {
		return randomBetween(MIN_CONSUMPTION_VALUE, MAX_CONSUMPTION_VALUE);
	}

	private long generateDelayTime() {
		return randomBetween(MIN_CONSUMPTION_TIME, MAX_CONSUMPTION_TIME);
	}

	private long randomBetween(int minValue, int maxValue) {
		return random.nextInt(maxValue - minValue) + minValue;
	}
}