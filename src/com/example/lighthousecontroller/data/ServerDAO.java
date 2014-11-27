package com.example.lighthousecontroller.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lighthousecontroller.model.HomeServer;

public class ServerDAO {
	private ServerTable serverTable;
	private SQLiteOpenHelper databaseHelper;
	public ServerDAO(Context context,  SQLiteOpenHelper openHelper) {
		serverTable = new ServerTable();
		databaseHelper = openHelper;
	}

	public Collection<? extends Table> getTables() {
		return Collections.singletonList(serverTable);
	}

	public List<HomeServer> getServers() {
		return listServers(null);
	}
	public HomeServer getServer(long id) {
		List<HomeServer> servers = listServers(id);
		return servers.isEmpty() ? null : servers.get(0);
	}
	public List<HomeServer> listServers(Long id){
		SQLiteDatabase database = databaseHelper.getReadableDatabase();
		List<HomeServer> servers = readServers(database, id);
		database.close();
		return servers;
	}

	protected List<HomeServer> readServers(SQLiteDatabase database, Long id) {
		String whereClause = id == null ? null : ServerTable.ID_COLUMN + " = " + id;
		Cursor c = database.query(serverTable.getName(),
				serverTable.listColumnsNames(), whereClause, null, null, null,
				null);

		List<HomeServer> servers = new ArrayList<>();
		for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
			HomeServer server = serverTable.readServer(c);

			servers.add(server);
		}

		return servers;
	}

	public HomeServer save(HomeServer newServer) {
		SQLiteDatabase database = this.databaseHelper.getWritableDatabase();
		database.beginTransaction();
		try {
			ContentValues serverValues = serverTable.toValues(newServer);
			if(newServer.getId() == 0){
				serverValues.remove(ServerTable.ID_COLUMN);
			}
			long id = database.insertWithOnConflict(serverTable.getName(), null, serverValues
					, SQLiteDatabase.CONFLICT_REPLACE);		
			database.setTransactionSuccessful();
			if(id == -1){
				return null;
			}
			newServer.setId(id);
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		finally{
			database.endTransaction();
			database.close();
		}
		
		return getServer(newServer.getId());
	}

	public void delete(HomeServer serverToDelete) {
		SQLiteDatabase database = this.databaseHelper.getWritableDatabase();
		database.beginTransaction();
		try {
			long id = serverToDelete.getId();	
			String whereClause = ServerTable.ID_COLUMN + " = " + id;
			database.delete(serverTable.getName(), whereClause, null);
			
			database.setTransactionSuccessful();
		} 
		finally{
			database.endTransaction();
			database.close();
		}
	}
}
