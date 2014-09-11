package com.example.lighthousecontroller;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.lighthousecontroller.list.LampListAdapter;
import com.example.lighthousecontroller.list.LampModel;
import com.example.lighthousecontroller.list.Model;

public class LampListActivity extends ListActivity {

	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
 
        LampListAdapter adapter = new LampListAdapter(this, LampController.getInstance().getGroups());
        setListAdapter(adapter);
        
    }
	
	@Override
	protected void onListItemClick (ListView l, View v, int position, long id){
		Model selected = (Model) getListView().getItemAtPosition(position);
		if(!selected.isGroupHeader()){
			Intent intent = new Intent(this, LampDetailsActivity.class);
			intent.putExtra(LampDetailsActivity.LAMP_ARGUMENT, ((LampModel)selected).getLamp());
			startActivity(intent);
		}
	}
    
}
