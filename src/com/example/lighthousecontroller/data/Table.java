package com.example.lighthousecontroller.data;

import android.database.sqlite.SQLiteDatabase;

public interface Table {

	void createTable(SQLiteDatabase db);
	void dropTable(SQLiteDatabase db);
}
