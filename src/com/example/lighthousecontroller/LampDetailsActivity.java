package com.example.lighthousecontroller;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class LampDetailsActivity extends ActionBarActivity {
	public static final String CLASS_NAME = LampDetailsActivity.class.getName();
	public static final String LAMP_ARGUMENT = CLASS_NAME + ".LAMP_ARG";
	private static final String LAMPDETAILSFRAG_TAG = CLASS_NAME + ".LAMPDETAILSFRAG_TAG";
	private LampDetailsFragment lampDetailsFragment;
	private Lamp lamp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lamp_details);

		lampDetailsFragment = (LampDetailsFragment) getSupportFragmentManager().findFragmentByTag(LAMPDETAILSFRAG_TAG);
		if (lampDetailsFragment == null) {
			lampDetailsFragment = new LampDetailsFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, lampDetailsFragment, LAMPDETAILSFRAG_TAG).commit();
		}
		
		if(getIntent() != null && getIntent().getExtras() != null){
			lamp = (Lamp) getIntent().getExtras().getSerializable(LAMP_ARGUMENT);
		}
		lampDetailsFragment.setLamp(lamp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lamp_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
