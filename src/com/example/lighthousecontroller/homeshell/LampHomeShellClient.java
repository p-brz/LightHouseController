package com.example.lighthousecontroller.homeshell;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.example.lighthousecontroller.model.ApplianceGroup;
import com.example.lighthousecontroller.model.Lamp;

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
	private String serverUrl;
	public LampHomeShellClient() {
		lamps = new ArrayList<>();
		groups = new ArrayList<>();
	}
	
	/** Consulta o webservice sobre os grupos de lâmpadas e atualiza os dados no banco de dados
	 * @throws IOException 
	 * @throws ClientProtocolException */
	public List<ApplianceGroup> getGroups() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(getGroupsUrl());
		
		ResponseHandler<List<ApplianceGroup> > rh = new GetGroupsResponseHandler();

		List<ApplianceGroup> response = null;
		try {
			response = httpclient.execute(httpget, rh);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}
	public Lamp getLamp(long lampId) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(getLampUrl(lampId));
		
		ResponseHandler<Lamp > rh = new GetLampResponseHandler();

		Lamp response = null;
		try {
			response = httpclient.execute(httpget, rh);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}
	public Lamp changeLampPower(long lampId, boolean on) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(changeLampPowerUrl(lampId, on));

		ResponseHandler<Lamp > rh = new GetLampResponseHandler();
		Lamp response = httpclient.execute(httppost, rh);

		Log.d("Lâmpada", "Executed post!");
		
		
		return response;
	}
	
	private URI changeLampPowerUrl(long lampId, boolean on) {
		try {
			String service = (on ? "ligar" : "desligar");
			return new URI(getServerUrl() + "/appliances/" + lampId + "/services/" + service + "/");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Lamp changeLampBright(long lampId, float bright) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(changeLampBrightUrl(lampId, bright));
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		String param = "brilho=" + (int)(bright *100);
		StringEntity stringEntity = new StringEntity(param);
		request.setEntity(stringEntity);
		
		ResponseHandler<Lamp > rh = new GetLampResponseHandler();
		Lamp response = httpclient.execute(request, rh);

		Log.d("Lâmpada", "Executed post!");
		
		return response;
	}
	private URI changeLampBrightUrl(long lampId, float bright) {
		try {
			String service = "definir_brilho";
			return new URI(getServerUrl() + "/appliances/" + lampId + "/services/" + service + "/");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private URI getLampUrl(long lampId) {
		try {
			return new URI(getServerUrl() + "/appliances/" + lampId + "/");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl){
		this.serverUrl = serverUrl;
	}

	private URI getGroupsUrl() {
		try {
			return new URI(getServerUrl() + "/groups");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
