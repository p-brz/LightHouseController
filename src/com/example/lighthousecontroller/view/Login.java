package com.example.lighthousecontroller.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lighthousecontroller.R;
import com.example.lighthousecontroller.model.HomeServer;

public class Login extends Activity {
	class ServersAdapter extends ArrayAdapter<HomeServer>{
		public ServersAdapter(Context context) {
			super(context, R.layout.listitem_server_layout, R.id.serverListItem_name);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view =  super.getView(position, convertView, parent);
			setupView(position, view);
			
			return view;
		}
		@Override
	    public View getDropDownView(int position, View convertView,
	            ViewGroup parent) {
			View view =  super.getDropDownView(position, convertView, parent);
			
			LinearLayout linearLayout = (LinearLayout)view;
			float minHeightPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
					48,  getResources().getDisplayMetrics());
			
			linearLayout.setMinimumHeight((int) minHeightPixels);
			
			setupView(position, view);
	        return view;
	    }

		private void setupView(int position, View view) {
			TextView itemName = (TextView) view.findViewById(R.id.serverListItem_name);
			TextView itemUrl = (TextView) view.findViewById(R.id.serverListItem_url);
			
			HomeServer homeServer = this.getItem(position);
			itemName.setText(homeServer.getServerName());
			itemUrl.setText(homeServer.getServerUrl());
		}
	}
	Spinner serversSpinner;
	ServersAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		adapter = new ServersAdapter(this);
		adapter.addAll(new HomeServer("Server exemplo", "http://MeuServer.com"), 
				new HomeServer("Outro servidor", "http://outroservidor.com"));
		setupView();
	}

	private void setupView() {
		serversSpinner = (Spinner)findViewById(R.id.serversSpinner);
		serversSpinner.setAdapter(adapter);
		
	}
	
	public void submitLogin(View v){
		EditText loginView = (EditText) findViewById(R.id.loginField);
		EditText passwordView = (EditText) findViewById(R.id.passField);
		
		String login = loginView.getText().toString();
		String password = passwordView.getText().toString();
		//TODO: realizar Login
		if(isValidPassword(login, password)){
			Intent intent = new Intent(this, LampListActivity.class);
			startActivity(intent);
			
			finish();
		}else{
			Toast.makeText(this, "Login incorreto", Toast.LENGTH_SHORT).show();
		}
	}
	
	private boolean isValidPassword(String username, String password){
		return username.equalsIgnoreCase("foo") && password.equals("bar");
	}
}
