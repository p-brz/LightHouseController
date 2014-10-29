package com.example.lighthousecontroller.controller;

import java.util.List;

import com.example.lighthousecontroller.model.ApplianceGroup;

public interface LampCollectionObserver {
	void onLampCollectionChange(List<ApplianceGroup> lampGroups);
}