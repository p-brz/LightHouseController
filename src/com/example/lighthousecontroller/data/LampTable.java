package com.example.lighthousecontroller.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.lighthousecontroller.data.Column.ColumnType;
import com.example.lighthousecontroller.data.Column.Constraint;
import com.example.lighthousecontroller.model.Lamp;

public class LampTable extends BaseTable {
	public static final String LAMPTABLE_NAME = "Lamp";
	public static final String ID_COLUMN = "Id";
	public static final String POWER_COLUMN = "Power";
	public static final String BRIGHT_COLUMN = "Bright";
	public static final String NAME_COLUMN = "Name";
	public static final String GROUP_ID_COLUMN = "GroupId";

	public LampTable() {
		super(LAMPTABLE_NAME);
		createColumns();
	}

	private final void createColumns() {
		super.addColumn(new Column(ID_COLUMN, ColumnType.INTEGER, Constraint.PRIMARY_KEY));
		super.addColumn(new Column(POWER_COLUMN, ColumnType.BOOLEAN));
		super.addColumn(new Column(BRIGHT_COLUMN, ColumnType.FLOAT));
		super.addColumn(new Column(NAME_COLUMN, ColumnType.TEXT));
		super.addColumn(new Column(GROUP_ID_COLUMN, ColumnType.INTEGER)); //Foreign Key
	}

	public Lamp readLamp(Cursor c) {
		Lamp lamp = new Lamp();
		lamp.setId(c.getInt(c.getColumnIndexOrThrow(ID_COLUMN)));
		lamp.setOn(c.getInt(c.getColumnIndexOrThrow(POWER_COLUMN)) != 0);
		lamp.setBright(c.getFloat(c.getColumnIndexOrThrow(BRIGHT_COLUMN)));
		lamp.setName(c.getString(c.getColumnIndexOrThrow(NAME_COLUMN)));
		
		return lamp;
	}

	public ContentValues lampToValues(Lamp lamp) {
		return lampToValues(lamp, null, false);
	}
	public ContentValues lampToValues(Lamp lamp, Integer groupId) {
		return lampToValues(lamp, groupId, true);
	}
	private ContentValues lampToValues(Lamp lamp, Integer groupId, boolean useGroupId) {
		ContentValues values = new ContentValues();
		values.put(ID_COLUMN, lamp.getId());
		values.put(POWER_COLUMN, lamp.isOn());
		values.put(BRIGHT_COLUMN, lamp.getBright());
		values.put(NAME_COLUMN, lamp.getName());
		if(useGroupId){
			values.put(GROUP_ID_COLUMN, groupId);
		}
		return values;
	}
}
