package com.example.lighthousecontroller.homeshell;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import com.example.lighthousecontroller.model.ConsumptionEvent;

public class ConsumptionListResponseHandler implements
		ResponseHandler<List<ConsumptionEvent>> {

	@Override
	public List<ConsumptionEvent> handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
