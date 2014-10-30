package com.example.lighthousecontroller.data;

import java.util.Arrays;
import java.util.List;

public class Column {
	public enum ColumnType{
		NONE, INTEGER, FLOAT, TEXT, BOOLEAN, DATETIME
	};
	private String name;
	private ColumnType type;
	private List<Constraint> constraints;

	public Column() {
		this("", ColumnType.NONE);
	}
	public Column(String name, ColumnType type, Constraint ... constraints) {
		this.name = name;
		this.type = type;
		this.constraints = Arrays.asList(constraints);
	}	
	
	public String getName() {
		return name;
	}
	public ColumnType getType() {
		return type;
	}
	public String getConstraintsAsString() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(Constraint constraint : this.constraints){
			if(!first){
				builder.append(", ");
			}
			else{
				first = false;
			}
			builder.append(constraint);
		}
		return builder.toString();
	}
	
}
