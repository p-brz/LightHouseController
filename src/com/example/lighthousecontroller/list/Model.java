package com.example.lighthousecontroller.list;

import com.example.lighthousecontroller.Lamp;

public class Model {
	
	private int icon;
    private String title;
    private Lamp lamp;
    
    private boolean isGroupHeader = false;
 
    public Model(String title) {
        this(-1,title);
        isGroupHeader = true;
    }
    public Model(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
        this.lamp = new Lamp();
        lamp.setBright(0);
    }
    
    
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Lamp getLamp() {
		return lamp;
	}
	public void setLamp(Lamp lamp) {
		this.lamp = lamp;
	}
	public boolean isGroupHeader() {
		return isGroupHeader;
	}
	public void setGroupHeader(boolean isGroupHeader) {
		this.isGroupHeader = isGroupHeader;
	}
	
    
    
}
