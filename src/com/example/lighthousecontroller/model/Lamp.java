package com.example.lighthousecontroller.model;

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
	private String name;
	private float bright;
	
	public Lamp() {
		this(0,"",false);
	}
	public Lamp(long lampId, String lampName, boolean isLampOn) {
		super();
		this.id = lampId;
		this.name = lampName;
		this.on = isLampOn;
		bright = (on ? 1f : 0f);
		consumptionHistory = new ArrayList<ConsumptionEvent>();
	}
	public Lamp(Lamp otherLamp) {
		this();
		this.set(otherLamp);
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
	
	public String getName() {
		return name;
	}
	public void setName(String someName) {
		this.name = someName;
	}
	/** Informa se está instância representa um dispositivo válido.*/
	public boolean isValid() {
		return id > 0;
	}
	/** Retorna um valor entre 0f e 1f correspondente à porcentagem de brilho desta lâmpada.*/
	public float getBright() {
		return bright;
	}
	public void setBright(float bright) {
		this.bright = bright;
	}
	public void addConsumptionEvent(ConsumptionEvent event) {
		this.consumptionHistory.add(event);
	}
	public void set(Lamp lamp) {
		this.id = lamp.getId();
		this.bright = lamp.getBright();
		this.on     = lamp.isOn();
		this.name   = lamp.getName();
		this.consumptionHistory.clear();
		this.consumptionHistory.addAll(lamp.getConsumptionHistory());
	}
}
