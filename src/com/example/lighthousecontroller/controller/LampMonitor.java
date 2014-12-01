package com.example.lighthousecontroller.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;

import com.example.lighthousecontroller.controller.TimeCounterNotifier.TimeCounterListener;

/**
Serviço LampMonitor de tempos em tempos envia requisições (através de {@link LampService}) 
LampService) para realizar a atualização dos status de uma (ou mais) lâmpadas.
Os clientes de LampMonitor enviam intents para que ele inicia ou pare o monitoramento de 
uma determinada lâmpada.
Se não há lâmpadas para monitorar, ele para de enviar notificações (até que uma nova lâmpada 
seja adicionada)
* */
public class LampMonitor extends LongLivedIntentService 
						 implements TimeCounterListener, OnChangeListObserver<Long>
{	
	interface ServiceExecutor{
		void execute(Intent intent, int startId);
	}
	
	class LampObservationController implements ServiceExecutor{
		
		final List<Long> lampsToWatch;
		OnChangeListObserver<Long> onChangeListObserver;
		
		public LampObservationController(List<Long> lampsList) {
			this.lampsToWatch = lampsList;
		}
		@Override
		public void execute(Intent intent, int startId) {
			long lampId = intent.getLongExtra(LAMPID_DATA, -1);
			if(lampId > 0){
				int listSize = lampsToWatch.size();
				if(intent.getAction().equals(BEGIN_OBSERVE_LAMP)){
					lampsToWatch.add(lampId);
					notifyChangeList(listSize);
				}
				else if(intent.getAction().equals(STOP_OBSERVE_LAMP)){
					lampsToWatch.remove(lampId);
					notifyChangeList(listSize);
				}
			}
		}
		private void notifyChangeList(int listSize) {
			if(onChangeListObserver != null){
				onChangeListObserver.onChangeList(lampsToWatch, listSize);
			}
		}
		public OnChangeListObserver<Long> getOnChangeListObserver() {
			return onChangeListObserver;
		}
		public void setOnChangeListObserver(
				OnChangeListObserver<Long> onChangeListObserver) {
			this.onChangeListObserver = onChangeListObserver;
		}
	}

	public static final String CLASS_NAME = LampMonitor.class.getName();

	public static final String BEGIN_OBSERVE_LAMP = CLASS_NAME + ".BEGIN_OBSERVE_LAMP";
	public static final String STOP_OBSERVE_LAMP  = CLASS_NAME + ".STOP_OBSERVE_LAMP";
	
	public static final String LAMPID_DATA = CLASS_NAME + ".LAMPID_DATA";
	
	//Tempo em milisegundos
	private static final long TOTAL_DURATION = 60 * 1000; 
	private static final long TICK_INTERVAL = 7 * 1000; 
	
	private final Map<String, ServiceExecutor> executors;
	private final LampObservationController lampObservationController;
	private final List<Long> lampsToWatch;	
	
	public LampMonitor() {
		lampsToWatch = new ArrayList<>();
		
		lampObservationController = new LampObservationController(this.lampsToWatch);
		lampObservationController.setOnChangeListObserver(this);
		executors = new HashMap<>();
		executors.put(BEGIN_OBSERVE_LAMP, lampObservationController);
		executors.put(STOP_OBSERVE_LAMP, lampObservationController);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopMonitor();
	}
	private TimeCounterNotifier lampMonitor;
	
	private void startMonitor() {
		if(lampMonitor != null && lampMonitor.isRunning()){
			lampMonitor.cancel();
		}

		lampMonitor = new TimeCounterNotifier(TOTAL_DURATION, TICK_INTERVAL, this);
		lampMonitor.start();
	}
	private void stopMonitor() {
		if(lampMonitor != null){
			lampMonitor.cancel();
		}
	}

	@Override
	protected void onHandleIntent(Intent intent, int startId) {
		for(String key : executors.keySet()){
			if(key.equals(intent.getAction())){
				executors.get(key).execute(intent, startId);
				return;
			}
		}
	}

	private void updateLamps() {
		for(Long lampId : this.lampsToWatch){
			updateLamp(lampId);
		}
	}
	private void updateLamp(Long lampId) {
		Intent intent = new Intent(this, LampService.class);
		intent.setAction(LampService.GET_LAMP);
		intent.putExtra(LampService.LAMP_ID_DATA, lampId);
		startService(intent);
	}

	/* **************************************TimeCounterListener********************************/

	@Override
	public void onTick(long millisUntilFinished) {
		updateLamps();
	}

	@Override
	public void onFinish() {
		if(this.lampsToWatch.size() > 0){
			startMonitor();
		}
	}

	/* ************************************* OnChangeListObserver *******************************/
	@Override
	public void onChangeList(List<Long> list, int previousSize) {
		int currentSize = list.size();
		if(currentSize > previousSize && currentSize == 1){ //Adicionou 1 elemento à lista
			startMonitor();
		}
		else if(currentSize < previousSize && currentSize == 0){ //Esvaziou lista
			stopMonitor();
		}
	}

}
