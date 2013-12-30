/**
 * 
 */
package com.gmail.charleszq.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.charleszq.utils.Constants;

/**
 * Represents the receiver to be notified when time is up to check whether there
 * is contact uploads, or comments on my photos, etc.
 * 
 * @author charles
 * 
 */
public class TimeUpReceiver extends BroadcastReceiver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Constants.INTENT_ACTION_CHECK_CONTACT_UPLOAD_RECEIVER.equals(intent
				.getAction())) {
			Intent serviceIntent = new Intent(context.getApplicationContext(),
					ContactUploadService.class);
			context.startService(serviceIntent);
			return;
		} else if (Constants.INTENT_ACTION_CHECK_PHOTO_ACTIVITY_RECEIVER
				.equals(intent.getAction())) {
			Intent photoIntent = new Intent(context.getApplicationContext(),
					PhotoActivityService.class);
			context.startService(photoIntent);
			return;
		}
	}

}
