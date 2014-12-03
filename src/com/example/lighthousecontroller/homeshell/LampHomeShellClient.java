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
import com.example.lighthousecontroller.model.ConsumptionEvent;
import com.example.lighthousecontroller.model.Lamp;

/** Esta classe é responsável por fazer comunicação com o web service, obtendo e enviando os dados 
 * necessários.
 * */
public class LampHomeShellClient {
	private static final String GROUPS_ROUTE = "groups";
	private static final String SERVICE_DESLIGAR = "desligar";
	private static final String SERVICE_LIGAR = "ligar";
	private static final String APPLIANCES_ROUTE = "appliances";
	private static final Object EXTRAS_ROUTE = "extras";
	private static final Object CONSUMPTION_EXTRA = "CONSUMPTION";
	private static final String SERVICE_DEFINIRBRILHO = "definir_brilho";
	private static final Object SERVICES_ROUTE = "services";
	
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

	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl){
		this.serverUrl = serverUrl;
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
	public List<ConsumptionEvent> getLampConsumption(long lampId){
		return getLampConsumption(lampId, null);
	}
	public List<ConsumptionEvent> getLampConsumption(long lampId,
			ConsumptionEvent event) {
		HttpClient httpclient = new DefaultHttpClient();
		URI uri = getLampConsumptionUrl(lampId, event);
		HttpGet httpget = new HttpGet(uri);
		
		ResponseHandler<List<ConsumptionEvent> > rh = new ConsumptionListResponseHandler();

		List<ConsumptionEvent> response = null;
		try {
			response = httpclient.execute(httpget, rh);
			
			for(ConsumptionEvent evt : response){
				evt.setId(lampId);
			}
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
	
	/* ************************************ Generate Urls **********************************************/
	private URI getLampUrl(long lampId) {
		try {
			return new URI(buildUrl(getServerUrl(),APPLIANCES_ROUTE, lampId));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private URI getLampConsumptionUrl(long lampId, ConsumptionEvent event) {
		try {
			long timestampInSeconds = event.getTimestamp()/1000;
			String startTime = event != null ? ("?start_date=" + timestampInSeconds)  : "";
			String url = buildUrl(getServerUrl(), APPLIANCES_ROUTE, lampId, EXTRAS_ROUTE, CONSUMPTION_EXTRA) + startTime;
			return new URI(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private URI changeLampPowerUrl(long lampId, boolean on) {
		String service = (on ? SERVICE_LIGAR : SERVICE_DESLIGAR);
		return getServiceUrl(service, lampId);
	}
	private URI changeLampBrightUrl(long lampId, float bright) {
		return getServiceUrl(SERVICE_DEFINIRBRILHO, lampId);
	}

	private URI getServiceUrl(String serviceName, long applianceId) {
		try {
//			return new URI(getServerUrl() + "/appliances/" + lampId + "/services/" + service + "/");
			return new URI(buildUrl(getServerUrl(),APPLIANCES_ROUTE, applianceId, SERVICES_ROUTE, serviceName));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private URI getGroupsUrl() {
		try {
//			return new URI(getServerUrl() + "/groups");
			return new URI(buildUrl(getServerUrl(), GROUPS_ROUTE));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String buildUrl(Object ... paths) {
		StringBuilder builder = new StringBuilder();
		for(Object path : paths){
			builder.append(path).append("/");
		}
		return builder.toString();
	}
}
