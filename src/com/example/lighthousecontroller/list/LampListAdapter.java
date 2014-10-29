package com.example.lighthousecontroller.list;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.lighthousecontroller.R;
import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.Lamp;

public class LampListAdapter extends ArrayAdapter<Model> {
	 
    private final Context context;
    private final List<ApplianceGroup> appliancesGroups;

    public LampListAdapter(Context context, List<ApplianceGroup> groups) {
        super(context, R.layout.target_item);

        this.context = context;
        this.appliancesGroups = new ArrayList<>();
        if(groups != null && !groups.isEmpty()){
        	this.appliancesGroups.addAll(groups);
        	this.doLayout();
        }
    }

    public List<ApplianceGroup> getAppliancesGroups() {
		return new CopyOnWriteArrayList<>(this.appliancesGroups);
	}
    public void setAppliancesGroups(List<ApplianceGroup> groups) {
		appliancesGroups.clear();
    	if(groups != null){
    		appliancesGroups.addAll(groups);
    	}
    	doLayout();
	}
    
    
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Model model = super.getItem(position);
        return model.createView(inflater, convertView, parent);
    }
    

	private void doLayout() {
		super.clear();
		for(ApplianceGroup group : this.appliancesGroups){
			//Adiciona título
			super.add(new TitleModel(group.getName()));
			//Adiciona lâmpadas
			for(Lamp lamp : group.getAppliances()){
				super.add(new LampModel(getLampIcon(lamp), lamp));
			}
		}
		notifyDataSetChanged();
	}

	private int getLampIcon(Lamp lamp) {
		// TODO Auto-generated method stub
		return R.drawable.ic_logo;
	}
}
