package com.example.lighthousecontroller.data;

import com.example.lighthousecontroller.data.Column.ColumnType;
import com.example.lighthousecontroller.data.Column.Constraint;

public class LampTable extends BaseTable {
	private static final String LAMPTABLE_NAME = "Lamp";
	private static final String ID_COLUMN = null;

	public LampTable() {
		super(LAMPTABLE_NAME);
		createColumns();
	}

	private final void createColumns() {
		super.addColumn(new Column(ID_COLUMN, ColumnType.INTEGER, Constraint.PRIMARY_KEY));
	}

}
