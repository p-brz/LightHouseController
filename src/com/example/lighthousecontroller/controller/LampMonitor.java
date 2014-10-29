package com.example.lighthousecontroller.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.CountDownTimer;

public class LampMonitor extends LongLivedIntentService{
	class LampTimedMonitor extends CountDownTimer{
		public LampTimedMonitor(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			getLampsIfModified();
		}

		@Override
		public void onFinish() {
		}		
	}
	
	interface ServiceExecutor{
		void execute(Intent intent, int startId);
	}
	private static final String GET_LAMPS = "GET LAMPS";
	private static final String GET_LAMPGROUPS = "GET LAMPGROUPS";
	private static final String MODIFIED_SINCE = "MODIFIED SINCE";
	
	private final Map<String, ServiceExecutor> executors;

	class GetLampsExecutor implements ServiceExecutor{
		@Override
		public void execute(Intent intent, int startId) {
			// TODO Auto-generated method stub
			
		}
	}
	class GetLampGroupsExecutor implements ServiceExecutor{

		@Override
		public void execute(Intent intent, int startId) {
			//TODO: fazer requisição a servidor
			Date modifiedSince = null;
			if(intent.getExtras() != null){
//				modifiedSince = intent.getExtras().getString(MODIFIED_SINCE);
			}
		}
	}
	
	public LampMonitor() {
		executors = new HashMap<>();
		executors.put(GET_LAMPS, new GetLampsExecutor());
		executors.put(GET_LAMPGROUPS, new GetLampGroupsExecutor());
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
	
	private void getLampsIfModified() {
		// TODO Auto-generated method stub
		
	}
}
