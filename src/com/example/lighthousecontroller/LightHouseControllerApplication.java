package com.example.lighthousecontroller;

import android.app.Application;

public class LightHouseControllerApplication extends Application{
	private static LightHouseControllerApplication application;
	
	public static LightHouseControllerApplication getApplication() {
		return application;
	}

	public LightHouseControllerApplication() {
		super();
		application = this;
	}
}
