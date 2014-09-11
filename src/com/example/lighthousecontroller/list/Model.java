package com.example.lighthousecontroller.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface Model {
	public boolean isGroupHeader();
	public View createView(LayoutInflater inflater, View convertView, ViewGroup parent);
	
}
