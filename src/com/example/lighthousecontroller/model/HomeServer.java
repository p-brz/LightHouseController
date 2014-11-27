package com.example.lighthousecontroller.model;


public class HomeServer {
	private String serverName;
	private String serverUrl;
//	private long lastAccessTimestamp;
	private long id;

	public HomeServer() {
		this("","");
	}
	public HomeServer(String serverName, String serverUrl) {
//		this(serverName, serverUrl, new Date().getTime());
		this.serverName = serverName;
		this.serverUrl = serverUrl;
	}
//	public HomeServer(String serverName, String serverUrl, long lastAccessTimestamp) {
//		this.serverName = serverName;
//		this.serverUrl = serverUrl;
////		this.lastAccessTimestamp = lastAccessTimestamp;
//	}
	
	public String getServerName() {
		return serverName;
	}
	public String getServerUrl() {
		return serverUrl;
	}
//	public long getLastAccessTimestamp() {
//		return lastAccessTimestamp;
//	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
//	public void setLastAccessTimestamp(long lastAccessTimestamp) {
//		this.lastAccessTimestamp = lastAccessTimestamp;
//	}
	public long getId(){
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void set(HomeServer createdServer) {
		this.setId(createdServer.getId());
		this.setServerName(createdServer.getServerName());
		this.setServerUrl(createdServer.getServerUrl());
//		this.setLastAccessTimestamp(createdServer.getLastAccessTimestamp());
	}
	
	
}
