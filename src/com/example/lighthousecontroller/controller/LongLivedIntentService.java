package com.example.lighthousecontroller.controller;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

public abstract class LongLivedIntentService extends Service{
	static final String INTENT_KEY = "Intent";
	static final String STARTID_KEY = "StartId";
	
	private Looper serviceLooper;
	private ServiceHandler serviceHandler;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			Intent intent = (Intent)bundle.getParcelable(INTENT_KEY);
			int startId = bundle.getInt(STARTID_KEY);
			
			onHandleIntent(intent, startId);			
		}
	}	
	
	protected abstract void onHandleIntent(Intent intent, int startId);
	
	@Override
	public void onCreate() {
		super.onCreate();
	    // Start up the thread running the service.  Note that we create a
	    // separate thread because the service normally runs in the process's
	    // main thread, which we don't want to block.  We also make it
	    // background priority so CPU-intensive work will not disrupt our UI.
	    HandlerThread thread = new HandlerThread("ServiceStartArguments",Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();

	    // Get the HandlerThread's Looper and use it for our Handler
	    serviceLooper = thread.getLooper();
	    serviceHandler = new ServiceHandler(serviceLooper);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		//TODO: (?) talvez executar mServiceLooper.quit() (pode não ser necessário) 
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle arguments = new Bundle();
		arguments.putInt(STARTID_KEY, startId);
		arguments.putParcelable(INTENT_KEY, intent);
		
		Message msg = serviceHandler.obtainMessage();
		msg.setData(arguments);
		
		serviceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}
}
