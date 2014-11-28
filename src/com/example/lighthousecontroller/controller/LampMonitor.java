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
//			getLampsIfModified();
		}

		@Override
		public void onFinish() {
		}		
	}
	
	interface ServiceExecutor{
		void execute(Intent intent, int startId);
	}
	
	private final Map<String, ServiceExecutor> executors;

//	class GetLampsExecutor implements ServiceExecutor{
//		@Override
//		public void execute(Intent intent, int startId) {
//			// TODO Auto-generated method stub
//			
//		}
//	}
//	class GetLampGroupsExecutor implements ServiceExecutor{
//
//		@Override
//		public void execute(Intent intent, int startId) {
//			//TODO: fazer requisição a servidor
//			Date modifiedSince = null;
//			if(intent.getExtras() != null){
////				modifiedSince = intent.getExtras().getString(MODIFIED_SINCE);
//			}
//		}
//	}
	
	class SyncNowExecutor implements ServiceExecutor{
		@Override
		public void execute(Intent intent, int startId) {
			Intent getGroupsIntent = new Intent();
			getGroupsIntent.setAction(LampService.GET_GROUPS);
			startService(getGroupsIntent);
		}
	}
	
	public LampMonitor() {
		executors = new HashMap<>();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		startMonitor();
	}
	
	private void startMonitor() {
		// TODO Auto-generated method stub
		
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
