package com.example.lighthousecontroller.data;

public class Constraint{
	public static final Constraint PRIMARY_KEY = new Constraint("PRIMARY KEY");
	
	public static Constraint DEFAULT(String defaultValue){
		return new Constraint("DEFAULT " + defaultValue);
	}
	
	
	private String name;
	private Constraint(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
}