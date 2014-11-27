package com.example.lighthousecontroller.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.lighthousecontroller.data.Column.ColumnType;
import com.example.lighthousecontroller.model.HomeServer;

public class ServerTable extends BaseTable{
	public static final String SERVERTABLE_NAME = "Server";
	public static final String ID_COLUMN = "Id";
	public static final String NAME_COLUMN = "Name";
	public static final String ADDRESS_COLUMN = "Address";
//	public static final String TIMESTAMP_COLUMN = "AccessTime";

	public ServerTable() {
		super(SERVERTABLE_NAME);
		createColumns();
	}

	private final void createColumns() {
		super.addColumn(new Column(ID_COLUMN, ColumnType.INTEGER, Constraint.PRIMARY_KEY));
		super.addColumn(new Column(NAME_COLUMN, ColumnType.TEXT));
		super.addColumn(new Column(ADDRESS_COLUMN, ColumnType.TEXT));
	}
	public HomeServer readServer(Cursor c) {
		HomeServer server = new HomeServer();
		server.setId(c.getLong(c.getColumnIndexOrThrow(ID_COLUMN)));
		server.setServerName(c.getString(c.getColumnIndexOrThrow(NAME_COLUMN)));
		server.setServerUrl(c.getString(c.getColumnIndexOrThrow(ADDRESS_COLUMN)));

		return server;
	}

	public ContentValues toValues(HomeServer newServer) {
		ContentValues values = new ContentValues();
		values.put(ID_COLUMN, newServer.getId());
		values.put(NAME_COLUMN, newServer.getServerName());
		values.put(ADDRESS_COLUMN, newServer.getServerUrl());
		
		return values;
	}
}
