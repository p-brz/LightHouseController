package com.example.lighthousecontroller.homeshell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.example.lighthousecontroller.data.Data;
import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.Lamp;

import android.os.Handler;

public class LampHomeShellClient {
	private static LampHomeShellClient singleton;

	public static LampHomeShellClient instance(){
		if(singleton == null){
			singleton = new LampHomeShellClient();
		}
		return singleton;
	}

	boolean generatingData = false;
	
	/** Consulta o webservice sobre os grupos de lâmpadas e atualiza os dados no banco de dados*/
	public void updateGroups() {

		//FIXME: simulando tempo de carregamento/atualização de grupos
		if(!generatingData){
			generatingData = true;
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					generateData();
				}
			}, 800);
		}
	}
    private void generateData(){ 
		Random random = new Random();
        int lampCount = 1;
        List<Lamp> lampadasDaSala = Arrays.asList(new Lamp[] {
    			new Lamp(lampCount++, "Lâmpada da Sala", random.nextBoolean())
      		  , new Lamp(lampCount++, "Lâmpada da Copa", random.nextBoolean())});
        List<Lamp> lampadasDaCozinha = Arrays.asList(new Lamp[] {
          			new Lamp(lampCount++, "Principal", random.nextBoolean())
          		  , new Lamp(lampCount++, "Lâmpada da Varanda", random.nextBoolean())});
        List<Lamp> lampadasDoQuarto = Arrays.asList(new Lamp[] {
    			new Lamp(lampCount++, "Minha Lâmpada", random.nextBoolean())});
        
        List<ApplianceGroup> groups = new ArrayList<>();
        groups.add(new ApplianceGroup("Sala", lampadasDaSala));
        groups.add(new ApplianceGroup("Cozinha", lampadasDaCozinha));
        groups.add(new ApplianceGroup("Quarto", lampadasDoQuarto));
        
        Data.instance().getLampDAO().insertGroups(groups);
    }
	public void changeLampPower(Lamp someLamp, boolean on) {

		Lamp lamp = new Lamp(someLamp);
		lamp.setOn(on);
		if(someLamp.isOn() != on){
			lamp.setBright(on ? 1f : 0f);
		}
		
        Data.instance().getLampDAO().updateLamp(lamp);
		//FIXME: Aqui será enviada requisição ao servidor para apagar ou acender lâmpada
	}
	public void changeLampBright(Lamp someLamp, float bright) {
		Lamp lamp = Data.instance().getLampDAO().getLamp(someLamp.getId());
		lamp.setBright(bright);
		lamp.setOn(bright > 0);
		
        Data.instance().getLampDAO().updateLamp(lamp);
	}
	public void updateLampStatus(Lamp storedLamp) {
		// TODO Auto-generated method stub
		
	}
}
