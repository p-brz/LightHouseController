package com.example.lighthousecontroller.homeshell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.Lamp;

public class GetGroupsResponseHandler implements
		ResponseHandler<List<ApplianceGroup>> {

	@Override
	public List<ApplianceGroup> handleResponse(HttpResponse arg0)
			throws ClientProtocolException, IOException {
		
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//        arg0.getEntity().writeTo(out);
//        out.close();
//        String responseString = out.toString();
        
        
		ApplianceGroup group = new ApplianceGroup(1, "Sala", Collections.singletonList(new Lamp(1, "Lampada principal", true)));
		
		// TODO Auto-generated method stub
		return Collections.singletonList(group);
	}

}