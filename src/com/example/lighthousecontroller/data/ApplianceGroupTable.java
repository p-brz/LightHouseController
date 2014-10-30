package com.example.lighthousecontroller.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.lighthousecontroller.data.Column.ColumnType;
import com.example.lighthousecontroller.data.Column.Constraint;
import com.example.lighthousecontroller.model.ApplianceGroup;

public class ApplianceGroupTable extends BaseTable {
	public static final String TABLE_NAME = "ApplianceGroup";
	public static final String ID_COLUMN = "Id";
	public static final String NAME_COLUMN = "Name";

	public ApplianceGroupTable() {
		super(TABLE_NAME);
		createColumns();
	}

	private final void createColumns() {
		super.addColumn(new Column(ID_COLUMN, ColumnType.INTEGER, Constraint.PRIMARY_KEY));
		super.addColumn(new Column(NAME_COLUMN, ColumnType.TEXT));
	}

	public ApplianceGroup readGroup(Cursor c) {
		ApplianceGroup group = new ApplianceGroup();
		group.setId(c.getInt(c.getColumnIndexOrThrow(ID_COLUMN)));
		group.setName(c.getString(c.getColumnIndexOrThrow(NAME_COLUMN)));
		
		return group;
	}

	public ContentValues groupToValues(ApplianceGroup group) {
		ContentValues values = new ContentValues();
		values.put(ID_COLUMN, group.getId());
		values.put(NAME_COLUMN, group.getName());
		return values;
	}
}
