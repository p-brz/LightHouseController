package com.example.lighthousecontroller.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/* adaptado de: https://developer.android.com/guide/topics/ui/dialogs.html#DialogFragment*/
public class SimpleDialogFragment extends DialogFragment {
	public interface DialogResponseListener{
		public void onResponse(boolean positive);
	}
	private String dialogMessage;
	private String okMessage, cancelMessage;
	private DialogResponseListener dialogResponseListener;
	private int layoutId;
	private boolean isModal;
	private String title;
	
	public SimpleDialogFragment() {
		super();
		this.dialogMessage = "";
		this.okMessage = null;
		this.cancelMessage = null;
		this.dialogResponseListener = null;
		this.layoutId = 0;
		this.isModal = false;
		this.title = null;
	}

	public String getDialogMessage() { return dialogMessage; }
	public String getOkMessage() 	 { return okMessage; }
	public String getCancelMessage() { return cancelMessage;}
	public DialogResponseListener getDialogResponseListener() { return dialogResponseListener;}

	public void setDialogMessage(String dialogMessage) {
		this.dialogMessage = dialogMessage;
	}
	public void setOkMessage(String okMessage) {
		this.okMessage = okMessage;
	}
	public void hideOkButton() {
		this.okMessage = null;
	}
	public void setCancelMessage(String cancelMessage) {
		this.cancelMessage = cancelMessage;
	}
	public void hideCancelButton() {
		this.cancelMessage = null;
	}
	public void setDialogResponseListener(DialogResponseListener dialogResponseListener) {
		this.dialogResponseListener = dialogResponseListener;
	}

	public int getLayoutId() {
		return layoutId;
	}
	public void setLayoutId(int layoutId) {
		this.layoutId = layoutId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(layoutId > 0 && !isModal){
        	return createView(layoutId, inflater, container);
        }
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		isModal = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(layoutId > 0 && isModal){
        	LayoutInflater inflater = getActivity().getLayoutInflater();
        	View view = createView(layoutId, inflater, null);
        	builder.setView(view);
        }
        builder.setMessage(this.getDialogMessage());
        if(this.getTitle() != null){
        	builder.setTitle(getTitle());
        }
        if(isValidMessage(getOkMessage())){
        	builder.setPositiveButton(getOkMessage(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    notifyResponse(true);
                }
            });
        };
        if(isValidMessage(getCancelMessage())){
        	builder.setNegativeButton(getCancelMessage(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    notifyResponse(false);
                }
            })
            .setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
                    notifyResponse(false);
				}
			});
        };
               
               

        return builder.create();

	}	

	protected View createView(int layoutId, LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(layoutId, container, false);
	}

	private boolean isValidMessage(String message) {
		return message != null && message.length() != 0;
	}

	private void notifyResponse(boolean response) {
		if(dialogResponseListener != null){
			dialogResponseListener.onResponse(response);
		}
	}

}
