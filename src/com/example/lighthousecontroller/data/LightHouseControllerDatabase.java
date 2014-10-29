package com.example.lighthousecontroller.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LightHouseControllerDatabase extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LightHouseControllerDatabase";
    
    private final List<Table> tables;
    
	public LightHouseControllerDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		tables = new ArrayList<>();
	}

	public void addTable(Table table){
		tables.add(table);
	}
	public void removeTable(Table table){
		tables.remove(table);
	}
	public List<Table> getTables(Table table){
		return new CopyOnWriteArrayList<>(this.tables);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.beginTransaction();
		try {
			for(Table table : tables){
				table.createTable(database);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.beginTransaction();
		try {
			for (Table table : this.tables) {
				table.dropTable(database);
			}
			onCreate(database);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}
	

}
