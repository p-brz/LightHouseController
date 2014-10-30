package com.example.lighthousecontroller.homeshell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.lighthousecontroller.data.Data;
import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.Lamp;

import android.os.Handler;

/** Esta classe é responsável por fazer comunicação com o web service, obtendo e enviando os dados 
 * necessários.
 * */
public class LampHomeShellClient {
	private static LampHomeShellClient singleton;

	public static LampHomeShellClient instance(){
		if(singleton == null){
			singleton = new LampHomeShellClient();
		}
		return singleton;
	}

	boolean generatingData = false;
	final List<Lamp> lamps;
	final List<ApplianceGroup> groups;
	public LampHomeShellClient() {
		lamps = new ArrayList<>();
		groups = new ArrayList<>();
	}
	
	/** Consulta o webservice sobre os grupos de lâmpadas e atualiza os dados no banco de dados*/
	public List<ApplianceGroup> getGroups() {
		//FIXME: simulando tempo de carregamento/atualização de grupos
		try {
			Thread.sleep(600);
			return generateData();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();
//		if(!generatingData){
//			generatingData = true;
//			Handler handler = new Handler();
//			handler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					generateData();
//				}
//			}, 800);
//		}
	}
    private List<ApplianceGroup> generateData(){ 
        
        if(groups.isEmpty()){
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
            
            lamps.clear();
        	lamps.addAll(lampadasDaSala);
        	lamps.addAll(lampadasDaCozinha);
        	lamps.addAll(lampadasDoQuarto);

//            groups = new ArrayList<>();
        	groups.clear();
//        	groups.add(new ApplianceGroup(1, "Lâmpadas", lamps));
            groups.add(new ApplianceGroup(1,"Sala", lampadasDaSala));
            groups.add(new ApplianceGroup(2, "Cozinha", lampadasDaCozinha));
            groups.add(new ApplianceGroup(3, "Quarto", lampadasDoQuarto));
        }
        
        
//        Data.instance().getLampDAO().insertGroups(groups);
        return groups;
    }
	public Lamp changeLampPower(long lampId, boolean on) {
		//FIXME: Aqui será enviada requisição ao servidor para apagar ou acender lâmpada
		
		Lamp lamp = getLamp(lampId);
		lamp.setOn(on);
		return new Lamp(lamp);
//        Data.instance().getLampDAO().updateLamp(lamp);
	}
//	public void changeLampBright(Lamp someLamp, float bright) {
//		Lamp lamp = Data.instance().getLampDAO().getLamp(someLamp.getId());
//		lamp.setBright(bright);
//		lamp.setOn(bright > 0);
//		
//        Data.instance().getLampDAO().updateLamp(lamp);
//	}
	public Lamp changeLampBright(long lampId, float bright) {
		Lamp lamp = getLamp(lampId);
		lamp.setBright(bright);
		return new Lamp(lamp);
	}
	public void updateLampStatus(Lamp storedLamp) {
		// TODO Auto-generated method stub
		
	}
	public Lamp getLamp(long lampId) {
		for(Lamp lamp : lamps){
			if(lamp.getId() == lampId){
				return lamp;
			}
		}
		throw new RuntimeException("Could not found lamp with id " + lampId);
//		return null;
	}
}
