package com.example.lighthousecontroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
	public void submitLogin(View v){
		EditText loginView = (EditText) findViewById(R.id.loginField);
		EditText passwordView = (EditText) findViewById(R.id.passField);
		
		String login = loginView.getText().toString();
		String password = passwordView.getText().toString();
		
		Log.println(1, "Debug Login", login);
		Log.println(1, "Debug Pass", password);

		if(isValidPassword(login, password)){
			Toast.makeText(this, "SUCESSO!", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "Login incorreto", Toast.LENGTH_SHORT).show();
		}
	}
	
	private boolean isValidPassword(String username, String password){
		return username.equalsIgnoreCase("foo") && password.equals("bar");
	}
}
