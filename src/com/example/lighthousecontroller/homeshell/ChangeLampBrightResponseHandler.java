package com.example.lighthousecontroller.homeshell;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import com.example.lighthousecontroller.model.Lamp;

public class ChangeLampBrightResponseHandler implements ResponseHandler<Lamp> {

	@Override
	public Lamp handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
