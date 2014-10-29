package com.example.lighthousecontroller.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.util.Log;

import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.Lamp;

public class LampDAO {
	private final List<Lamp> lamps;
	private final List<ApplianceGroup> groups;

	private LampTable lampTable;
			
	public LampDAO(Context context) {
		Log.d(getClass().getName(), "Create LampDAO");
		if(context == null){
			throw new IllegalStateException("Context should not be null!");
		}
		
		lamps = new ArrayList<>();
		groups = new ArrayList<>();
		
		lampTable = new LampTable();
	}


	public Collection<? extends Table> getTables() {
		return Collections.singletonList(lampTable);
	}
	public List<Lamp> getLamps() {
		return new CopyOnWriteArrayList<>(this.lamps);
	}
	public List<ApplianceGroup> getGroups() {
		return new CopyOnWriteArrayList<>(this.groups);
	}
	public Lamp getLamp(long id) {
		for(Lamp lamp : this.lamps){
			if(lamp.getId() == id){
				return lamp;
			}
		}
		return null;
	}

	public void insertOrUpdateGroups(List<ApplianceGroup> groups) {
//		this.groups.addAll(groups);
		lamps.clear();
		for(ApplianceGroup group : groups){
			ApplianceGroup currentGroup = findGroup(group);
			if(currentGroup == null){
				this.groups.add(group);
			}
			else{
				currentGroup.setAppliances(group.getAppliances());
			}
			this.lamps.addAll(group.getAppliances());
		}
	}

	private ApplianceGroup findGroup(ApplianceGroup group) {
		for(ApplianceGroup someGroup : this.groups){
			if(someGroup.getName().equals(group)){//FIXME: substituir por id
				return someGroup;
			}
		}
		return null;
	}


	public void updateLamp(Lamp newLamp) {
		Lamp oldLamp = getLamp(newLamp.getId());
		if(oldLamp == null){
			throw new IllegalArgumentException("Lâmpada de id " + newLamp.getId() + " não existe!");
		}		
		
		oldLamp.set(newLamp);
	}

	
}
