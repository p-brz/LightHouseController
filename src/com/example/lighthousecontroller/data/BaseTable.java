package com.example.lighthousecontroller.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.database.sqlite.SQLiteDatabase;

public class BaseTable implements Table{
	private static final Object CREATE_TABLE = "CREATE TABLE";
	private static final Object SPACE = " ";
	private static final Object COLUMN_SEP = ",\n";
	private static final Object START_TABLE_BODY = "(\n";
	private static final Object END_TABLE_BODY = "\n);";
	
	private String tableName;
	private final List<Column> columns;
	
	public BaseTable(String tableName) {
		this.tableName = tableName;
		columns = new ArrayList<>();
	}

	@Override
	public void createTable(SQLiteDatabase db) {
		String createCommand =  generateCreateCommand();
		
		db.execSQL(createCommand);
	}

	protected String generateCreateCommand() {
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append(CREATE_TABLE).append(SPACE).append(getName());
		cmdBuilder.append(START_TABLE_BODY);
			boolean isFirst = true;
			for(Column col : columns ){
				if(!isFirst){
					cmdBuilder.append(COLUMN_SEP);
				}
				cmdBuilder.append(col.getName()).append(SPACE)
						  .append(col.getType()).append(SPACE)
						  .append(col.getConstraintsAsString());
				isFirst = false;
			}
		cmdBuilder.append(END_TABLE_BODY);
		
		return cmdBuilder.toString();
	}

	public String getName() {
		return this.tableName;
	}
	public void setName(String tableName) {
		this.tableName = tableName;
	}
	public void addColumn(Column col){
		this.columns.add(col);
	}
	public void removeColumn(Column col){
		this.columns.remove(col);
	}
	public List<Column> getColumns(){
		return new CopyOnWriteArrayList<>(this.columns);
	}

	@Override
	public void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
	}

}
