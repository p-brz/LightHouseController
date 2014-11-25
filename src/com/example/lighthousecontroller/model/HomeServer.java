package com.example.lighthousecontroller.model;

import java.util.Date;

public class HomeServer {
	private String serverName;
	private String serverUrl;
	private long lastAccessTimestamp;

	public HomeServer() {
		this("","");
	}
	public HomeServer(String serverName, String serverUrl) {
		this(serverName, serverUrl, new Date().getTime());
	}
	public HomeServer(String serverName, String serverUrl, long lastAccessTimestamp) {
		this.serverName = serverName;
		this.serverUrl = serverUrl;
		this.lastAccessTimestamp = lastAccessTimestamp;
	}
	
	public String getServerName() {
		return serverName;
	}
	public String getServerUrl() {
		return serverUrl;
	}
	public long getLastAccessTimestamp() {
		return lastAccessTimestamp;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public void setLastAccessTimestamp(long lastAccessTimestamp) {
		this.lastAccessTimestamp = lastAccessTimestamp;
	}
	
	
}
