package com.example.lighthousecontroller.controller;

import android.os.CountDownTimer;

public class TimeCounterNotifier extends CountDownTimer{
	public interface TimeCounterListener{
		public void onFinish();
		public void onTick(long millisUntilFinished);
	}
	
	private TimeCounterListener timeCounterListener;
	private boolean running;

	public TimeCounterNotifier(long millisInFuture, long countDownInterval) {
		this(millisInFuture,countDownInterval,null);
	}
	public TimeCounterNotifier(long millisInFuture, long countDownInterval, TimeCounterListener listener) {
		super(millisInFuture, countDownInterval);
		running = false;
		this.timeCounterListener = listener;
	}

	@Override
	public synchronized void onFinish() {
		running = false;
		if(timeCounterListener != null){
			timeCounterListener.onFinish();
		}
	}
	@Override
	public synchronized void onTick(long millisUntilFinished) {
		if(timeCounterListener != null){
			timeCounterListener.onTick(millisUntilFinished);
		}
	}

	public synchronized TimeCounterListener getTimeCounterListener() {
		return timeCounterListener;
	}

	public synchronized void setTimeCounterListener(TimeCounterListener timeCounterListener) {
		this.timeCounterListener = timeCounterListener;
	}

	public synchronized boolean isRunning() {
		return running;
	}	
}
