package com.example.lighthousecontroller;

import java.util.Date;

/**
 * Representa uma determinada medida de consumo em um instante de tempo.
 * */
public class ConsumptionEvent {
	/**
	 * O momento em que ocorreu esta medição.
	 * Dado como 'unix time', mas em milisegundos.
	 * @see Date#getTime()
	 * */
	private long timestamp;
	
	/**
	 * O valor de consumo medido.
	 * 
	 * TODO: Ver unidade (talvez Watts ou Killowats)
	 * */
	private double consumption;

	public long getTimestamp() {
		return timestamp;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}
	
	
}
