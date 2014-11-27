package com.example.lighthousecontroller.data;

import java.util.Arrays;
import java.util.List;

import com.example.lighthousecontroller.model.HomeServer;

import android.content.Context;

public class ServerDAO {

	public ServerDAO(Context context, LightHouseControllerDatabase database) {
		// TODO Auto-generated constructor stub
	}

	public List<HomeServer> getServers() {
		return Arrays.asList(new HomeServer("Server exemplo", "http://MeuServer.com"), 
				new HomeServer("Outro servidor", "http://outroservidor.com"));
	}

}
