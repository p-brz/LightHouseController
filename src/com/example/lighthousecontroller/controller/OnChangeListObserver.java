package com.example.lighthousecontroller.controller;

import java.util.List;

public interface OnChangeListObserver<T>{
	void onChangeList(List<T> list, int previousSize);
}