package com.example.lighthousecontroller.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ApplianceGroup {
	private String name;
	private final List<Lamp> appliances;
	private long id;
	public ApplianceGroup() {
		this(0, "");
	}

	public ApplianceGroup(long id, String someName) {
		this(id, someName, null);
	}

	public ApplianceGroup(long id, String someName, List<Lamp> someAppliances) {
		super();
		this.id = id;
		this.name = someName;
		this.appliances = new ArrayList<>();
		if(someAppliances != null){
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
