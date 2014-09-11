package com.example.lighthousecontroller;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lighthousecontroller.list.LampListAdapter;
import com.example.lighthousecontroller.list.Model;

public class LampListActivity extends ListActivity {

	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
 
        LampListAdapter adapter = new LampListAdapter(this, generateData());
        setListAdapter(adapter);
        
    }
	
	@Override
	protected void onListItemClick (ListView l, View v, int position, long id){
		Model selected = (Model) getListView().getItemAtPosition(position);
		if(!selected.isGroupHeader()){
			Intent intent = new Intent(this, LampDetailsActivity.class);
			intent.putExtra(LampDetailsActivity.LAMP_ARGUMENT, selected.getLamp());
			startActivity(intent);
		}
	}
	
 
    private ArrayList<Model> generateData(){
        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model("Sala"));
        models.add(new Model(R.drawable.ic_logo,"Lampada da Sala"));
        models.add(new Model(R.drawable.ic_logo,"Lampada da Copa"));
        models.add(new Model("Cozinha"));
        models.add(new Model(R.drawable.ic_logo,"Principal"));
        models.add(new Model(R.drawable.ic_logo,"Varanda"));
        models.add(new Model("Quarto"));
        models.add(new Model(R.drawable.ic_logo,"Lampada do quarto"));
 
        return models;
    }
    
}
