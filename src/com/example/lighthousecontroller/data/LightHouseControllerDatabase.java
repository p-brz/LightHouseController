package com.example.lighthousecontroller.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LightHouseControllerDatabase extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 3;
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
				Log.d(getClass().getName(),"Create table: " + table.getName());
				table.createTable(database);
			}
//			insertDebugValues(database);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		
	}
//
//	private void insertDebugValues(SQLiteDatabase database) {
//		// TODO Auto-generated method stub
//		List<Lamp> generatedLamps = generateData();
//		LampTable lampTable = new LampTable();
//		database.beginTransaction();
//		try {
//			for(Lamp lamp : generatedLamps){
//				ContentValues lampValues = lampTable.lampToValues(lamp);
//				database.insertWithOnConflict(lampTable.getName(), null, lampValues
//						, SQLiteDatabase.CONFLICT_REPLACE);
//			}
//			database.setTransactionSuccessful();
//		} catch (SQLException ex) {
//			ex.printStackTrace();
//			return;
//		}
//		finally{
//			database.endTransaction();
//		}
//
//		return;
//	}
//	private List<Lamp> generateData(){ 
//        List<Lamp> lamps = new ArrayList<>();
//		Random random = new Random();
//        int lampCount = 1;
//        List<Lamp> lampadasDaSala = Arrays.asList(new Lamp[] {
//    			new Lamp(lampCount++, "Lâmpada da Sala", random.nextBoolean())
//      		  , new Lamp(lampCount++, "Lâmpada da Copa", random.nextBoolean())});
//        List<Lamp> lampadasDaCozinha = Arrays.asList(new Lamp[] {
//          			new Lamp(lampCount++, "Principal", random.nextBoolean())
//          		  , new Lamp(lampCount++, "Lâmpada da Varanda", random.nextBoolean())});
//        List<Lamp> lampadasDoQuarto = Arrays.asList(new Lamp[] {
//    			new Lamp(lampCount++, "Minha Lâmpada", random.nextBoolean())});
//        
//        lamps.clear();
//    	lamps.addAll(lampadasDaSala);
//    	lamps.addAll(lampadasDaCozinha);
//    	lamps.addAll(lampadasDoQuarto);
//
//        return lamps;
//    }

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
