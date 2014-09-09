package com.example.lighthousecontroller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Lamp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5068852375767159490L;
	private boolean on;
	private long id;
	private final List<ConsumptionEvent> consumptionHistory;
	
	public Lamp() {
		super();
		consumptionHistory = new ArrayList<ConsumptionEvent>();
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	public List<ConsumptionEvent> getConsumptionHistory() {
		return consumptionHistory;
	}
	public void setConsumptionHistory(List<ConsumptionEvent> consumptionHistory) {
		this.consumptionHistory.clear();
		if(consumptionHistory != null){
			this.consumptionHistory.addAll(consumptionHistory);
		}
	}
	
}
