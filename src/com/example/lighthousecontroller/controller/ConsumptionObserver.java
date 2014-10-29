package com.example.lighthousecontroller.controller;

import java.util.List;

import com.example.lighthousecontroller.model.ConsumptionEvent;

public interface ConsumptionObserver {
	public void onConsumption(List<ConsumptionEvent> event);
}