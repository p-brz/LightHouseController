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
	
	/**
	 * Identificador para a fonte de consumo.
	 * */
	private long sourceId;

	public ConsumptionEvent(long id, long timestamp, double consumptionValue) {
		this.sourceId = id;
		this.timestamp = timestamp;
		this.consumption = consumptionValue;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public double getConsumption() {
		return consumption;
	}
	public long getSourceId(){
		return sourceId;
	}
	
}
