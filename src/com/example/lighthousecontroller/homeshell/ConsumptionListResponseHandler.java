package com.example.lighthousecontroller.homeshell;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lighthousecontroller.model.ConsumptionEvent;
import com.example.lighthousecontroller.model.Lamp;

public class ConsumptionListResponseHandler implements
		ResponseHandler<List<ConsumptionEvent>> {

	@Override
	public List<ConsumptionEvent> handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		
		InputStream in = response.getEntity().getContent();
		String encoding =  "UTF-8";
		String body = IOUtils.toString(in, encoding);
		
		try {
			JSONObject obj = new JSONObject(body);
			
			List<ConsumptionEvent> consumptionHistory = order(parseJsonConsumptionHistory(obj));
			
			return consumptionHistory;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	private List<ConsumptionEvent> order(List<ConsumptionEvent> parseJsonConsumptionHistory) {
		//TODO: garantir que eventos de consumo sejam retornados de forma ordenada (?)
		return parseJsonConsumptionHistory;
	}

	private List<ConsumptionEvent> parseJsonConsumptionHistory(JSONObject obj) throws JSONException {
		List<ConsumptionEvent> events = new ArrayList<ConsumptionEvent>();
		
		JSONArray groupsArray = obj.getJSONObject("contents").getJSONArray("extras");
		for(int i=0; i < groupsArray.length(); ++i){
			events.add(parseEvent(groupsArray.getJSONObject(i)));
		}
		
		return events;
	}

	private ConsumptionEvent parseEvent(JSONObject jsonObject) throws JSONException {
		Long timestamp = (long)(jsonObject.getDouble("date") * 1000);
		double consumptionValue = jsonObject.getDouble("value");
		ConsumptionEvent event = new ConsumptionEvent(0, timestamp, consumptionValue);
		return event;
	}

}
