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
import com.example.lighthousecontroller.model.Lamp;

public class LampDAO {
//	private final List<Lamp> lamps;
//	private final List<ApplianceGroup> groups;

	private LampTable lampTable;
	private ApplianceGroupTable applianceGroupTable;
			
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
		
		sqliteDb = openHelper;
	}


	public Collection<? extends Table> getTables() {
		return Arrays.asList(lampTable, applianceGroupTable);
	}
	public List<Lamp> getLamps() {	
		SQLiteDatabase database = sqliteDb.getReadableDatabase();
		List<Lamp> lamps = readLamps(database, null, null);
		database.close();
		return lamps;
	}

	public List<ApplianceGroup> getGroups() {
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
	public Lamp getLamp(long id) {
		SQLiteDatabase database = sqliteDb.getReadableDatabase();
		List<Lamp> lamps = readLamps(database, id, null);
		database.close();
		if(!lamps.isEmpty()){
			return lamps.get(0);
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

//	public void insertOrUpdateGroups(List<ApplianceGroup> groups) {
//		this.groups.addAll(groups);
//		lamps.clear();
//		for(ApplianceGroup group : groups){
//			ApplianceGroup currentGroup = findGroup(group);
//			if(currentGroup == null){
//				this.groups.add(group);
//			}
//			else{
//				currentGroup.setAppliances(group.getAppliances());
//			}
//			this.lamps.addAll(group.getAppliances());
//		}
//	}
//	private ApplianceGroup findGroup(ApplianceGroup group) {
//		for(ApplianceGroup someGroup : this.groups){
//			if(someGroup.getName().equals(group)){//FIXME: substituir por id
//				return someGroup;
//			}
//		}
//		return null;
//	}

	public void setGroupsAndLamps(List<ApplianceGroup> groups) {
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


	public void setLamps(List<Lamp> lamps) {
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


//	public void updateLamp(Lamp newLamp) {
//	Lamp oldLamp = getLamp(newLamp.getId());
//	if(oldLamp == null){
//		throw new IllegalArgumentException("Lâmpada de id " + newLamp.getId() + " não existe!");
//	}		
//	
//	oldLamp.set(newLamp);
//	}
	public void updateLamp(Lamp newLamp) {
		SQLiteDatabase database = sqliteDb.getReadableDatabase(); //TODO: não deveria abrir banco de dados na main thread
		
		ContentValues lampValues = lampTable.lampToValues(newLamp);

		String whereClause = LampTable.ID_COLUMN + "=" + String.valueOf(newLamp.getId());
		
		
		long rowsAffected = database.update(lampTable.getName(), lampValues, whereClause, null);
		Log.d(getClass().getName(), "UpdateLamp. RowsAffected: " + rowsAffected);
		
		database.close();
	}

	
}
