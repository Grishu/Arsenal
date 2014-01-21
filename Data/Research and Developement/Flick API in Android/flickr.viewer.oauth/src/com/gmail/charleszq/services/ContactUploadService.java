/**
 * 
 */
package com.gmail.charleszq.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.contacts.Contact;
import com.gmail.yuyang226.flickr.contacts.ContactsInterface;

/**
 * Represents the service to check whether there are new photos uploaded by my
 * contacts.
 * 
 * @author charles
 * 
 */
public class ContactUploadService extends IntentService {

	private static final Logger logger = LoggerFactory
			.getLogger(ContactUploadService.class);

	/**
	 * Constructor.
	 */
	public ContactUploadService() {
		super(Constants.ENABLE_CONTACT_UPLOAD_NOTIF);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Context context = getApplicationContext();

		String token = null;
		String secret = null;
		int intervalInHours = Constants.SERVICE_CHECK_INTERVAL;
		if (context instanceof FlickrViewerApplication) {
			FlickrViewerApplication app = (FlickrViewerApplication) context;
			token = app.getFlickrToken();
			secret = app.getFlickrTokenSecret();
			intervalInHours = app.getContactUploadCheckInterval();

			if (token == null || secret == null
					|| !app.isContactUploadCheckEnabled()) {
				logger.debug("User not login yet.");  //$NON-NLS-1$
				return;
			}

		} else {
			logger.warn("Error to get application context={}", context); //$NON-NLS-1$
			return;
		}

		checkContactUpload(token, secret, intervalInHours);
	}

	private void checkContactUpload(String token, String secret,
			int intervalInHours) {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token, secret);
		ContactsInterface ci = f.getContactsInterface();
		Date sinceDate = new Date();
		Long time = sinceDate.getTime() - intervalInHours * 60 * 60 * 1000;
		sinceDate = new Date(time);
		try {
		    Date now = new Date();
			SimpleDateFormat formater = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss"); //$NON-NLS-1$
			logger.debug("Task runs at {}", formater.format(now)); //$NON-NLS-1$
            Collection<?> col = ci.getListRecentlyUploaded(sinceDate, "all"); //$NON-NLS-1$
			if (col.size() > 0) {
				logger
						.debug(
								"There are {} contacts have new photos uploaded.", col.size()); //$NON-NLS-1$
				sendNotifications(col);
			} else {
				logger.info("No recent uploads from my contacts."); //$NON-NLS-1$
			}
		} catch (Exception e) {
			logger.warn("unable to get recent upload", e); //$NON-NLS-1$
	}
	}

    private void sendNotifications(Collection<?> col) {

    	Context mContext = getApplicationContext();

		// notification manager.
		NotificationManager notifManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// notification itself.
		Notification notif = new Notification(R.drawable.icon,
				mContext.getResources().getString(
						R.string.notif_message_recent_upload), System
						.currentTimeMillis());
		notif.defaults = Notification.DEFAULT_LIGHTS
				| Notification.DEFAULT_SOUND;
		notif.flags =  Notification.FLAG_AUTO_CANCEL;
		// init the contact id string array
		List<String> cIds = new ArrayList<String>();
		Iterator<?> it = col.iterator();
		while (it.hasNext()) {
			Contact c = (Contact) it.next();
			cIds.add(c.getId());
		}

		// notification intent.
		CharSequence contentTitle = mContext.getResources().getString(
				R.string.app_name);
		CharSequence contentText = mContext.getResources().getString(
				R.string.notif_message_recent_upload);
		Intent notificationIntent = new Intent(
				Constants.CONTACT_UPLOAD_PHOTO_NOTIF_INTENT_ACTION);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.putExtra(Constants.CONTACT_IDS_WITH_PHOTO_UPLOADED,
				cIds.toArray(new String[0]));
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				notificationIntent, PendingIntent.FLAG_ONE_SHOT);

		notif.setLatestEventInfo(mContext, contentTitle, contentText,
				contentIntent);

		// send out the notif
		notifManager.notify(Constants.COTACT_UPLOAD_NOTIF_ID, notif);
	}
}
