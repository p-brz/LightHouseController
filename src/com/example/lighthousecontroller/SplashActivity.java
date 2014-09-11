package com.example.lighthousecontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SplashActivity extends Activity {

	private static int SPLASH_TIME_OUT = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
 
        new Handler().postDelayed(new Runnable() {
 
        	// Might be useful for automatic server discover (see nmap)
        	
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, Login.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
	}
}