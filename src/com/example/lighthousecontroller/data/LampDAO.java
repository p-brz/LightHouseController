package com.example.lighthousecontroller.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.ConsumptionEvent;
import com.example.lighthousecontroller.model.Lamp;

public class LampDAO {
//	private final List<Lamp> lamps;
//	private final List<ApplianceGroup> groups;

	private LampTable lampTable;
	private ApplianceGroupTable applianceGroupTable;
	private LampConsumptionTable consumptionTable;
			
	private SQLiteOpenHelper sqliteDb;
	
	public LampDAO(Context context, SQLiteOpenHelper openHelper) {
		Log.d(getClass().getName(), "Create LampDAO");
		if(context == null){
			throw new IllegalStateException("Context should not be null!");
		}
		
//		lamps = new ArrayList<>();
//		groups = new ArrayList<>();
		
		lampTable = new LampTable();
		applianceGroupTable = new ApplianceGroupTable();
		consumptionTable = new LampConsumptionTable();
		
		sqliteDb = openHelper;
	}


	public Collection<? extends Table> getTables() {
		return Arrays.asList(lampTable, applianceGroupTable, consumptionTable);
	}
	public synchronized List<Lamp> getLamps() {	
		SQLiteDatabase database = sqliteDb.getReadableDatabase();
		List<Lamp> lamps = readLamps(database, null, null);
		database.close();
		return lamps;
	}

	public synchronized List<ApplianceGroup> getGroups() {
		SQLiteDatabase database = sqliteDb.getReadableDatabase();
		List<ApplianceGroup> groups = readGroups(database, null);
		Map<Integer, List<Lamp> > groupIdToLamp = new HashMap<>();
		
		readLamps(database, null, groupIdToLamp);
		for(ApplianceGroup group : groups){
			List<Lamp> groupLamps = groupIdToLamp.get(group.getId());
			if(groupLamps != null){
				group.setAppliances(groupLamps);
			}
		}
		
		database.close();
		return groups;
	}
	public synchronized  Lamp getLamp(long id) {
		SQLiteDatabase database = sqliteDb.getReadableDatabase();
		List<Lamp> lamps = readLamps(database, id, null);
		database.close();
		if(!lamps.isEmpty()){
			return lamps.get(0);
		}
		return null;
	}

	public synchronized void setGroupsAndLamps(List<ApplianceGroup> groups) {
		SQLiteDatabase database = this.sqliteDb.getWritableDatabase();
		database.beginTransaction();
		try {
			List<Lamp> allLamps = new ArrayList<Lamp>();
			for(ApplianceGroup group : groups){
				ContentValues values = applianceGroupTable.groupToValues(group);
				database.insertWithOnConflict(applianceGroupTable.getName(), null, values
						, SQLiteDatabase.CONFLICT_REPLACE);
				
				//Inserir ou atualizar as lâmpadas
				for(Lamp lamp : group.getAppliances()){
					allLamps.add(lamp);
					
					ContentValues lampValues = lampTable.lampToValues(lamp);
					database.insertWithOnConflict(lampTable.getName(), null, lampValues
							, SQLiteDatabase.CONFLICT_REPLACE);
				}
			}

			excludeOtherGroups(database, groups);
			excludeOtherLamps(database, allLamps);			
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return;
		}
		finally{
			database.endTransaction();
			database.close();
		}

		return;
	}


	public synchronized void setLamps(List<Lamp> lamps) {
		SQLiteDatabase database = this.sqliteDb.getWritableDatabase();
		database.beginTransaction();
		try {
			for(Lamp lamp : lamps){
				ContentValues lampValues = lampTable.lampToValues(lamp);
				database.insertWithOnConflict(lampTable.getName(), null, lampValues
						, SQLiteDatabase.CONFLICT_REPLACE);
			}

			excludeOtherLamps(database, lamps);			
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return;
		}
		finally{
			database.endTransaction();
			database.close();
		}

		return;
	}
	public synchronized void updateLamp(Lamp newLamp) {
		SQLiteDatabase database = sqliteDb.getReadableDatabase(); //TODO: não deveria abrir banco de dados na main thread
		
		ContentValues lampValues = lampTable.lampToValues(newLamp);

		String whereClause = LampTable.ID_COLUMN + "=" + String.valueOf(newLamp.getId());
		
		
		long rowsAffected = database.update(lampTable.getName(), lampValues, whereClause, null);
		Log.d(getClass().getName(), "UpdateLamp. RowsAffected: " + rowsAffected);
		
		database.close();
	}


	public synchronized void addConsumption(ConsumptionEvent event) {
		SQLiteDatabase database = sqliteDb.getReadableDatabase(); //TODO: não deveria abrir banco de dados na main thread
		
		ContentValues values = consumptionTable.toValues(event);
		
		long inserted = database.insert(consumptionTable.getName(), null, values);
		
		Log.d(getClass().getName(), "Inserted consumption: " + inserted);
		
		database.close();
	}
	public synchronized ConsumptionEvent getLastConsumptionEvent(long lampId) {
		SQLiteDatabase database = sqliteDb.getReadableDatabase(); //TODO: não deveria abrir banco de dados na main thread
		
		Log.d(getClass().getName(), "getLastContumptionEvent. database is closed: " + !database.isOpen());
		//TODO: consulta do banco deveria filtrar este valor e não fazer "a mão"
		List<ConsumptionEvent> history = readLampConsumption(database, lampId);
		
		database.close();
		
		if(history.size() > 0){
			return filterLastConsumption(history);
		}
		return null;
	}

	private List<ApplianceGroup> readGroups(SQLiteDatabase database, Long groupId) {
		String whereClause = groupId == null ? null 
										    : ApplianceGroupTable.ID_COLUMN + " = " + groupId;
		Cursor c = database.query(applianceGroupTable.getName()
								, applianceGroupTable.listColumnsNames()
								, whereClause, null, null, null, null);
		
		List<ApplianceGroup> groups = new ArrayList<>();
		for(c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()){
			ApplianceGroup group = applianceGroupTable.readGroup(c);
			
			groups.add(group);
		}
		
		return groups;
	}
	
	private List<Lamp> readLamps(SQLiteDatabase database, Long lampId, Map<Integer, List<Lamp> > lampsGroups) {
		String whereClause = lampId == null ? null 
										    : LampTable.ID_COLUMN + " = " + lampId;
		Cursor c = database.query(lampTable.getName(), lampTable.listColumnsNames(), whereClause, null, null, null, null);
		
		List<Lamp> lamps = new ArrayList<>();
		for(c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()){
			Lamp lamp = lampTable.readLamp(c);
			
			readLampConsumption(database, lamp);
			
			if(lampsGroups != null){
				Integer groupId = c.getInt(c.getColumnIndexOrThrow(LampTable.GROUP_ID_COLUMN));
				if(lampsGroups.get(groupId) == null){
					lampsGroups.put(groupId, new ArrayList<Lamp>());
				}
				lampsGroups.get(groupId).add(lamp);
			}
			
			lamps.add(lamp);
		}
		
		return lamps;
	}

	private void readLampConsumption(SQLiteDatabase database, Lamp lamp) {
		List<ConsumptionEvent> consumptionHistory = readLampConsumption(database, lamp.getId());
		lamp.setConsumptionHistory(consumptionHistory);
	}
	private List<ConsumptionEvent> readLampConsumption(SQLiteDatabase database, long id) {
		String whereClause = LampConsumptionTable.LAMPSOURCE_COLUMN + " = " + id;
		Cursor c = database.query(consumptionTable.getName(), consumptionTable.listColumnsNames(), whereClause, null, null, null, null);
		
		List<ConsumptionEvent> consumptionHistory = new ArrayList<>();
		for(c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()){
			ConsumptionEvent event = consumptionTable.read(c);
			consumptionHistory.add(event);
		}
		return consumptionHistory;
	}
	
	private void excludeOtherGroups(SQLiteDatabase database, List<ApplianceGroup> groups) {
		String groupsIds = getGroupsIds(groups);
		
		Log.d(getClass().getName(),"excludeOthersGroups not in: " + groupsIds);
		
		long rowsAffected = database.delete(applianceGroupTable.getName(),  
				LampTable.ID_COLUMN + " NOT IN ("+ groupsIds +")", null);
		
		Log.d(getClass().getName(), "Excluded " + rowsAffected + " groups");
	}


	private void excludeOtherLamps(SQLiteDatabase database, List<Lamp> lamps) {
		String lampsIds = getLampsIds(lamps);
		
		Log.d(getClass().getName(),"excludeOthersLamps not in: " + lampsIds);
		
		long rowsAffected = database.delete(lampTable.getName(),  
				LampTable.ID_COLUMN + " NOT IN ("+lampsIds+")", null);
		
		Log.d(getClass().getName(), "Excluded " + rowsAffected + " lamps");
	}

	
	private String getGroupsIds(List<ApplianceGroup> groups) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(ApplianceGroup group : groups){
			if(!first){
				builder.append(", ");
			}
			else{
				first = false;
			}
			builder.append(group.getId());
		}
		return builder.toString();
	}
	private String getLampsIds(List<Lamp> lamps) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(Lamp lamp : lamps){
			if(!first){
				builder.append(", ");
			}
			else{
				first = false;
			}
			builder.append(lamp.getId());
		}
		return builder.toString();
	}


	private ConsumptionEvent filterLastConsumption(List<ConsumptionEvent> history) {
		ConsumptionEvent lastEvt = null;
		for(ConsumptionEvent evt : history){
			if(lastEvt != null){
				if(lastEvt.getTimestamp() < evt.getTimestamp()){
					lastEvt = evt;
				}
			}
			else{
				lastEvt = evt;
			}
		}
		
		return lastEvt;
	}	
}
