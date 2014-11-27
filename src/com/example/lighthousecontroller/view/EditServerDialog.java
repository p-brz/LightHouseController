package com.example.lighthousecontroller.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.lighthousecontroller.R;
import com.example.lighthousecontroller.model.HomeServer;
import com.example.lighthousecontroller.view.SimpleDialogFragment.DialogResponseListener;


public class EditServerDialog extends SimpleDialogFragment implements DialogResponseListener {
	private static final String DEFAULT_CANCEL_MESSAGE = "Cancelar";
	private static final String DEFAULT_OK_MESSAGE 	   = "Salvar";
	private static final String DEFAULT_DIALOG_MESSAGE = "Editar informações de servidor";
	private HomeServer homeServer;
	private DialogResponseListener externalDialogResponseListener;

	private EditText nameText;
	private EditText urlText;
	
	public EditServerDialog() {

		this.setLayoutId(R.layout.dialog_edit_server);
		this.setDialogMessage(DEFAULT_DIALOG_MESSAGE);
		this.setOkMessage(DEFAULT_OK_MESSAGE);
		this.setCancelMessage(DEFAULT_CANCEL_MESSAGE);
		super.setDialogResponseListener(this);
	}
	
	public HomeServer getHomeServer() {
		return homeServer;
	}

	public void setHomeServer(HomeServer homeServer) {
		this.homeServer = homeServer;
		if(homeServer != null && nameText != null && urlText != null){
			populateViewFromModel(homeServer);
		}
	}

	@Override
	public DialogResponseListener getDialogResponseListener() {
		return externalDialogResponseListener;
	}
	@Override
	public void setDialogResponseListener(DialogResponseListener dialogResponseListener) {
		this.externalDialogResponseListener = dialogResponseListener;
	}

	@Override
	protected View createView(int layoutId, LayoutInflater inflater,
			ViewGroup container) {
		View view = super.createView(layoutId, inflater, container);
		setupViews(view);
		return view;
	}
	private void setupViews(View view) {
		nameText = (EditText) view.findViewById(R.id.editServer_fieldName);
		urlText  = (EditText) view.findViewById(R.id.editServer_fieldUrl);
		
		if(homeServer != null){
			populateViewFromModel(homeServer);
		}
	}
	
	@Override
	public void onResponse(boolean positive) {
		if(positive){
			if(homeServer != null){
				populateModelFromView(homeServer);
			}
		}
		if(this.externalDialogResponseListener != null){
			externalDialogResponseListener.onResponse(positive);
		}
	}

	protected void populateViewFromModel(HomeServer homeServer) {
		this.nameText.setText(homeServer.getServerName());
		this.urlText.setText(homeServer.getServerUrl());
	}
	protected void populateModelFromView(HomeServer homeServer) {
		homeServer.setServerName(nameText.getText().toString());
		homeServer.setServerUrl(urlText.getText().toString());
	}
	
}
