package com.example.lighthousecontroller.view;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.lighthousecontroller.controller.LampCollectionObserver;
import com.example.lighthousecontroller.controller.LampController;
import com.example.lighthousecontroller.list.LampListAdapter;
import com.example.lighthousecontroller.list.LampModel;
import com.example.lighthousecontroller.list.Model;
import com.example.lighthousecontroller.model.ApplianceGroup;

public class LampListActivity extends ListActivity implements LampCollectionObserver {
	private LampListAdapter adapter;
	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
 
        adapter = new LampListAdapter(this, LampController.getInstance().getGroups());
        setListAdapter(adapter);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		LampController.getInstance().addLampCollectionObserver(this);
		updateLampsList(LampController.getInstance().getGroups());
	}
	@Override
	protected void onPause() {
		super.onPause();
		LampController.getInstance().removeLampCollectionObserver(this);
	}

	@Override
	public void onLampCollectionChange(List<ApplianceGroup> lampGroups) {
		updateLampsList(lampGroups);
	}

	private void updateLampsList(List<ApplianceGroup> lampGroups) {
		this.adapter.setAppliancesGroups(lampGroups);
		this.adapter.notifyDataSetChanged();
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
