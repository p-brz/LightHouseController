package com.example.lighthousecontroller.homeshell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.Lamp;

public class GetGroupsResponseHandler implements
		ResponseHandler<List<ApplianceGroup>> {

	@Override
	public List<ApplianceGroup> handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//        arg0.getEntity().writeTo(out);
//        out.close();
//        String responseString = out.toString();
        
//        
//		ApplianceGroup group = new ApplianceGroup(1, "Sala", Collections.singletonList(new Lamp(1, "Lampada principal", true)));
//		
//		// TODO Auto-generated method stub
//		return Collections.singletonList(group);
		

		InputStream in = response.getEntity().getContent();
		String encoding =  "UTF-8";
		String body = IOUtils.toString(in, encoding);
		
		try {
			JSONObject obj = new JSONObject(body);
			
			List<ApplianceGroup> groups = parseJsonGroups(obj);
			
			return groups;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
/**
	 {
	    "contents": {
	        "groups": [
	            {
	                "appliances": [
	                    {
	                        ...
	                    }
	                ], 
	                "id": 1, 
	                "name": "Sala"
	            }
	        ]
	    }, 
	    "message": "OK", 
	    "status": 200
	}
 * @throws JSONException 
 * */
	private List<ApplianceGroup> parseJsonGroups(JSONObject obj) throws JSONException {
		List<ApplianceGroup> groups = new ArrayList<ApplianceGroup>();
		JSONArray groupsArray = obj.getJSONObject("contents").getJSONArray("groups");
		for(int i=0; i < groupsArray.length(); ++i){
			groups.add(parseGroup(groupsArray.getJSONObject(i)));
		}
		return groups;
	}
	private ApplianceGroup parseGroup(JSONObject jsonObject) throws JSONException {
		ApplianceGroup group = new ApplianceGroup();
		group.setId(jsonObject.getLong("id"));
		group.setName(jsonObject.getString("name"));
		List<Lamp> lamps = new ArrayList<Lamp>();
		
		JSONArray appliancesArray = jsonObject.getJSONArray("appliances");
		for(int i=0; i < appliancesArray.length(); ++i){
			lamps.add(parseLamp(appliancesArray.getJSONObject(i)));
		}
		group.setAppliances(lamps);
		return group;
	}
	private Lamp parseLamp(JSONObject applianceJson) throws JSONException {
		Lamp lamp = new Lamp();
		lamp.setId(applianceJson.getLong("id"));
		lamp.setName(applianceJson.getString("name"));
		JSONArray status = applianceJson.getJSONArray("status");
		for(int i=0; i <  status.length(); ++i){
			JSONObject state = status.getJSONObject(i);
			String name = state.names().getString(0);
			if(name.equals("ligada")){
				lamp.setOn(state.getInt(name) == 1);
			}
			else if(name.equals("bright")){
				lamp.setBright(state.getInt(name)/100f);
			}
		}
		return lamp;
	}
}