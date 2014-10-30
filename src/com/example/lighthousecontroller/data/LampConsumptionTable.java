package com.example.lighthousecontroller.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.lighthousecontroller.data.Column.ColumnType;
import com.example.lighthousecontroller.model.ConsumptionEvent;

public class LampConsumptionTable extends BaseTable {
	public static final String TABLE_NAME = "Consumption";
//	public static final String ID_COLUMN = "Id";
	public static final String TIMESTAMP_COLUMN = "Timestamp";
	public static final String CONSUMPTION_COLUMN = "ConsumptionValue";
	public static final String LAMPSOURCE_COLUMN = "LampSourceId";

	public LampConsumptionTable() {
		super(TABLE_NAME);
		createColumns();
	}

	private final void createColumns() {
//		super.addColumn(new Column(ID_COLUMN, ColumnType.INTEGER, Constraint.PRIMARY_KEY));
		super.addColumn(new Column(TIMESTAMP_COLUMN, ColumnType.INTEGER));
		super.addColumn(new Column(CONSUMPTION_COLUMN, ColumnType.FLOAT));
		super.addColumn(new Column(LAMPSOURCE_COLUMN, ColumnType.INTEGER));
	}

	public ConsumptionEvent read(Cursor c) {
		long sourceId = c.getLong(c.getColumnIndexOrThrow(LAMPSOURCE_COLUMN));
		long timestamp = c.getLong(c.getColumnIndexOrThrow(TIMESTAMP_COLUMN));
		double consumptionValue = c.getDouble(c.getColumnIndexOrThrow(CONSUMPTION_COLUMN));
		ConsumptionEvent event = new ConsumptionEvent(sourceId, timestamp, consumptionValue);
		
		return event;
	}

	public ContentValues toValues(ConsumptionEvent evt) {
		ContentValues values = new ContentValues();
//		values.put(ID_COLUMN, evt.getId());
		values.put(TIMESTAMP_COLUMN, evt.getTimestamp());
		values.put(CONSUMPTION_COLUMN, evt.getConsumption());
		values.put(LAMPSOURCE_COLUMN, evt.getSourceId());
		return values;
	}
}
