/**
 * 
 */
package com.gmail.charleszq.services;

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
import com.gmail.charleszq.dataprovider.RecentActivitiesDataProvider;
import com.gmail.charleszq.utils.Constants;
import com.gmail.yuyang226.flickr.activity.Item;

/**
 * Represents the service to check whether my photos got comments or not.s
 * 
 * @author charles
 * 
 */
public class PhotoActivityService extends IntentService {

	private static final Logger logger = LoggerFactory
			.getLogger(PhotoActivityService.class);

	public PhotoActivityService() {
		super(Constants.ENABLE_PHOTO_ACT_NOTIF);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String token = null;
		String secret = null;
		int intervalInHours = Constants.SERVICE_CHECK_INTERVAL;

		Context context = getApplicationContext();
		if (context instanceof FlickrViewerApplication) {
			FlickrViewerApplication app = (FlickrViewerApplication) context;
			token = app.getFlickrToken();
			secret = app.getFlickrTokenSecret();
			if (token == null || secret == null
					|| !app.isPhotoActivityCheckEnabled()) {
				logger.debug("User not login yet.");  //$NON-NLS-1$
				return;
			}
		} else {
			logger.warn("Error, application context is not the application."); //$NON-NLS-1$
			return;
		}

		checkPhotoActivities(token, secret, intervalInHours);

	}

	private void checkPhotoActivities(String token, String secret,
			int intervalInHours) {
		RecentActivitiesDataProvider dp = new RecentActivitiesDataProvider(
				token, secret, true);
        dp.setCheckInterval(intervalInHours);
        List<Item> items = dp.getRecentActivities();
		logger.debug("Recent activity task executed, item size={}, items={}", //$NON-NLS-1$
				items.size(), items);
        if (!items.isEmpty()) {
            sendNotification();
        }
	}

	private void sendNotification() {

		Context context = getApplicationContext();

        // notification manager.
        NotificationManager notifManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // notification itself.
		Notification notif = new Notification(R.drawable.icon, context
				.getResources().getString(
                        R.string.notif_message_act_on_my_photo), System
                        .currentTimeMillis());
        notif.defaults = Notification.DEFAULT_SOUND;
        notif.flags = Notification.FLAG_AUTO_CANCEL;

        // notification intent.
        CharSequence contentTitle = context.getResources().getString(
                R.string.app_name);
        CharSequence contentText = context.getResources().getString(
                R.string.notif_message_act_on_my_photo);
        Intent notificationIntent = new Intent(
                Constants.ACT_ON_MY_PHOTO_NOTIF_INTENT_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        notif.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);

        // send out the notif
        notifManager.notify(Constants.ACT_ON_MY_PHOTO_NOTIF_ID, notif);
    }

}
