/**
 * 
 */
package com.gmail.charleszq.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

/**
 * @author charles
 * 
 */
public abstract class ProgressDialogAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result>{
	
	protected ProgressDialog mDialog;
	protected Activity mActivity;
	protected String mDialogMessage;
	
	/**
	 * Constructor.
	 * @param activity
	 * @param msg if this is <code>null</code>, then don't show the progress dialog.
	 */
	public ProgressDialogAsyncTask(Activity activity, String msg) {
		this.mActivity = activity;
		this.mDialogMessage = msg;
	}
	
	public ProgressDialogAsyncTask(Activity activity, int msgResId ) {
	    this.mActivity = activity;
	    this.mDialogMessage = activity.getResources().getString(msgResId);
	}

	@Override
	protected void onPostExecute(Result result) {
		if(mActivity != null && mDialog != null && mDialog.isShowing() ) {
			mDialog.dismiss();
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if( mDialogMessage == null ) {
			return;
		}
		mDialog = ProgressDialog.show(mActivity, "", mDialogMessage); //$NON-NLS-1$
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            	ProgressDialogAsyncTask.this.cancel(true);
            }
        });
	}
}
