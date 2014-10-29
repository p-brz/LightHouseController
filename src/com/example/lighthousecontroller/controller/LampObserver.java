package com.example.lighthousecontroller.controller;

import com.example.lighthousecontroller.model.Lamp;

public interface LampObserver {
	void lampUpdated(Lamp lamp);
}