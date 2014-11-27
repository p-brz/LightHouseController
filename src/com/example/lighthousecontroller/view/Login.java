package com.example.lighthousecontroller.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lighthousecontroller.R;
import com.example.lighthousecontroller.model.HomeServer;
import com.example.lighthousecontroller.view.SimpleDialogFragment.DialogResponseListener;

public class Login extends FragmentActivity {
	class ServersAdapter extends ArrayAdapter<HomeServer>{
		public ServersAdapter(Context context) {
			super(context, R.layout.listitem_server_layout, R.id.serverListItem_name);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view =  super.getView(position, convertView, parent);
			setupView(position, view);
			
			return view;
		}
		@Override
	    public View getDropDownView(int position, View convertView,
	            ViewGroup parent) {
			View view =  super.getDropDownView(position, convertView, parent);
			
			LinearLayout linearLayout = (LinearLayout)view;
			float minHeightPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
					48,  getResources().getDisplayMetrics());
			
			linearLayout.setMinimumHeight((int) minHeightPixels);
			
			setupView(position, view);
	        return view;
	    }

		private void setupView(int position, View view) {
			TextView itemName = (TextView) view.findViewById(R.id.serverListItem_name);
			TextView itemUrl = (TextView) view.findViewById(R.id.serverListItem_url);
			
			HomeServer homeServer = this.getItem(position);
			itemName.setText(homeServer.getServerName());
			itemUrl.setText(homeServer.getServerUrl());
		}
		
	}
	private static final HomeServer NOVO_HOME_SERVER = new HomeServer("Inserir Novo","");
	private static final String EDIT_SERVER_DIALOG_TAG = "EDIT_SERVER_DIALOG_TAG";
	private static final String CREATE_SERVER_DIALOG_TAG = "CREATE_SERVER_DIALOG_TAG";
	private static final String DELETE_SERVER_DIALOG_TAG = "DELETE_SERVER_DIALOG_TAG";
	Spinner serversSpinner;
	ServersAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		adapter = new ServersAdapter(this);
		adapter.addAll(new HomeServer("Server exemplo", "http://MeuServer.com"), 
				new HomeServer("Outro servidor", "http://outroservidor.com"));
		setupView();
	}

	private void setupView() {
		serversSpinner = (Spinner)findViewById(R.id.serversSpinner);
		serversSpinner.setAdapter(adapter);
	}
	
	public void onClickEditServerButton(View view){
		final HomeServer serverToEdit = (HomeServer) serversSpinner.getSelectedItem();
		final EditServerDialog simpleDialogFragment = new EditServerDialog();
		simpleDialogFragment.setHomeServer(serverToEdit);
		simpleDialogFragment.setDialogResponseListener(new DialogResponseListener() {
			
			@Override
			public void onResponse(boolean positive) {
				if(positive){
					editServer(serverToEdit);
				}
			}

			private void editServer(final HomeServer serverToEdit) {
				serverToEdit.getClass();
				adapter.notifyDataSetChanged();
			}
		});
		simpleDialogFragment.show(getSupportFragmentManager(), EDIT_SERVER_DIALOG_TAG);
	}
	public void onClickCreateServerButton(View view){
		final HomeServer newServer = new HomeServer();
		final EditServerDialog simpleDialogFragment = new EditServerDialog();
		simpleDialogFragment.setHomeServer(newServer);
		simpleDialogFragment.setLayoutId(R.layout.dialog_edit_server);
		simpleDialogFragment.setDialogMessage("Insira as informações do novo servidor");
		simpleDialogFragment.setOkMessage("Criar");
		simpleDialogFragment.setCancelMessage("Cancelar");
		simpleDialogFragment.setDialogResponseListener(new DialogResponseListener() {
			@Override
			public void onResponse(boolean positive) {
				if(positive){
					addHomeServer(newServer);
				}
			}
		});
		simpleDialogFragment.show(getSupportFragmentManager(), CREATE_SERVER_DIALOG_TAG);
	}
	public void onClickDeleteServerButton(View view){
		final HomeServer selectedServer = (HomeServer) serversSpinner.getSelectedItem();
		SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
		simpleDialogFragment.setDialogMessage("Excluir configurações de servidor?");
		simpleDialogFragment.setOkMessage("Ok");
		simpleDialogFragment.setCancelMessage("Cancelar");
		simpleDialogFragment.setDialogResponseListener(new DialogResponseListener() {
			@Override
			public void onResponse(boolean positive) {
				if(positive){
					removeHomeServer(selectedServer);
				}
			}
		});
		simpleDialogFragment.show(getSupportFragmentManager(), DELETE_SERVER_DIALOG_TAG);
	}
	protected void addHomeServer(final HomeServer newServer) {
		adapter.add(newServer);
		adapter.notifyDataSetChanged();
		serversSpinner.setSelection(adapter.getCount() - 1);
	}
	protected void removeHomeServer(HomeServer serverToDelete) {
		this.adapter.remove(serverToDelete);
		adapter.notifyDataSetChanged();
	}
	public void submitLogin(View v){
		if(this.serversSpinner.getSelectedItem() == NOVO_HOME_SERVER){
			showInvalidServerErrorMessage();
			return;
		}
		
		EditText loginView = (EditText) findViewById(R.id.loginField);
		EditText passwordView = (EditText) findViewById(R.id.passField);
		
		String login = loginView.getText().toString();
		String password = passwordView.getText().toString();
		//TODO: realizar Login
		if(isValidPassword(login, password)){
			Intent intent = new Intent(this, LampListActivity.class);
			startActivity(intent);
			
			finish();
		}else{
			Toast.makeText(this, "Login incorreto", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void showInvalidServerErrorMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 builder.setMessage("Por favor selecione um servidor válido ou adicione um novo.")
		        .setNeutralButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
		 AlertDialog alert = builder.create();
		     alert.show();
	}

	private boolean isValidPassword(String username, String password){
		return username.equalsIgnoreCase("foo") && password.equals("bar");
	}
}
