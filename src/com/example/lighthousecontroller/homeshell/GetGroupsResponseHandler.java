package com.example.lighthousecontroller.homeshell;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import com.example.lighthousecontroller.model.ApplianceGroup;

public class GetGroupsResponseHandler implements
		ResponseHandler<List<ApplianceGroup>> {

	@Override
	public List<ApplianceGroup> handleResponse(HttpResponse arg0)
			throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}