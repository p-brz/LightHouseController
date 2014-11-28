package com.example.lighthousecontroller.homeshell;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.example.lighthousecontroller.model.Lamp;

public class GetLampResponseHandler implements ResponseHandler<Lamp> {

	
	@Override
	public Lamp handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		
		InputStream in = response.getEntity().getContent();
		String encoding =  "UTF-8";
		String body = IOUtils.toString(in, encoding);
		
		try {
			JSONObject obj = new JSONObject(body);
			
			Lamp lamp = parseJsonLamp(obj);
			
			return lamp;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	{
	    "contents": {
	        "appliance": {
	            "id": 1, 
	            "name": "defaultlamp", 
	            "package": "com.homeshell.lamp", 
	            "services": [
	                {
	                    "id": 1, 
	                    "name": "ligar", 
	                    "params": []
	                }, 
	                {
	                    "id": 2, 
	                    "name": "desligar", 
	                    "params": []
	                }, 
	                {
	                    "id": 3, 
	                    "name": "definir_brilho", 
	                    "params": [
	                        {
	                            "id": 1, 
	                            "name": "brilho"
	                        }
	                    ]
	                }
	            ], 
	            "status": [
	                {
	                    "ligada": 1
	                }
	            ], 
	            "type": "defaultlamp"
	        }
	    }, 
	    "message": "OK", 
	    "status": 200
	}
	 * @throws JSONException 
	 * */
	private Lamp parseJsonLamp(JSONObject obj) throws JSONException {
		JSONObject applianceJson = obj.getJSONObject("contents").getJSONObject("appliance");
		Lamp lamp = new Lamp();
		lamp.setId(applianceJson.getLong("id"));
		lamp.setName(applianceJson.getString("name"));
		JSONArray status = applianceJson.getJSONArray("status");
		for(int i=0; i <  status.length(); ++i){
			JSONObject state = status.getJSONObject(i);
			String name = state.names().getString(0);
			if(name.equals("ligada")){
				Log.d(getClass().getName(),"ligada: " + state.getInt(name));
				lamp.setOn(state.getInt(name) == 1);
			}
			else if(name.equals("bright")){
				lamp.setBright(state.getInt(name)/100f);
			}
		}
		return lamp;
	}

}