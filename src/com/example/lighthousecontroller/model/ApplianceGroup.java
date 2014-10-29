package com.example.lighthousecontroller.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ApplianceGroup {
	private String name;
	private final List<Lamp> appliances;
	
	public ApplianceGroup() {
		this("");
	}

	public ApplianceGroup(String someName) {
		this(someName, null);
	}

	public ApplianceGroup(String someName, List<Lamp> someAppliances) {
		super();
		this.name = someName;
		this.appliances = new ArrayList<>();
		if(appliances != null){
			this.appliances.addAll(someAppliances);
		}
	}

	public String getName() {
		return name;
	}

	public List<Lamp> getAppliances() {
		return new CopyOnWriteArrayList<>(this.appliances);
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setAppliances(List<Lamp> lamps) {
		appliances.clear();
		appliances.addAll(lamps);
	}
	
}
