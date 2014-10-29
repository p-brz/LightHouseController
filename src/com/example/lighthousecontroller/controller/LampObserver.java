package com.example.lighthousecontroller.controller;

import com.example.lighthousecontroller.model.Lamp;

public interface LampObserver {
	void changedPowerStatus(Lamp lamp);
	void changedBright(Lamp lamp);
	void lampUpdated(Lamp lamp);
}