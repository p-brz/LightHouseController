package com.example.lighthousecontroller.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lighthousecontroller.R;

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
