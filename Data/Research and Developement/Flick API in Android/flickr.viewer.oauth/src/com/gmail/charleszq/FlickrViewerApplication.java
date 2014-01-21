/**
 * 
 */

package com.gmail.charleszq;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.Handler;

import com.gmail.charleszq.dataprovider.PopularPhotoListProvider;
import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.event.IFlickrViewerMessageHandler;
import com.gmail.charleszq.services.TimeUpReceiver;
import com.gmail.charleszq.utils.Constants;
import com.gmail.yuyang226.flickr.RequestContext;
import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;

/**
 * Represents the main application.
 * 
 * @author charles
 */
public class FlickrViewerApplication extends Application {

	private static final Logger logger = LoggerFactory
			.getLogger(FlickrViewerApplication.class);

	private Set<IFlickrViewerMessageHandler> mMessageHandlers = new HashSet<IFlickrViewerMessageHandler>();

	/**
	 * Returns the defiend page size of the grid view.
	 * 
	 * @return the page size.
	 */
	public int getPageSize() {
		String pageSize = getSharedPreferenceValue(Constants.PHOTO_PAGE_SIZE,
				String.valueOf(Constants.DEF_GRID_PAGE_SIZE));
		return Integer.valueOf(pageSize);
	}

	/**
	 * Returns the user defined column number of the grid view.
	 * 
	 * @return
	 */
	public int getGridNumColumns() {
		String count = getSharedPreferenceValue(Constants.PHOTO_GRID_COL_COUNT,
				String.valueOf(Constants.DEF_GRID_COL_COUNT));
		return Integer.parseInt(count);
	}

	public String getFlickrToken() {
		String token = getSharedPreferenceValue(Constants.FLICKR_TOKEN, null);
		return token;
	}

	public OAuth loadSavedOAuth() {
		String userId = getUserId();
		String userName = getUserName();
		String token = getFlickrToken();
		String tokenSecret = getFlickrTokenSecret();
		if (userId == null || token == null || tokenSecret == null) {
			return null;
		}
		OAuth oauth = new OAuth();
		oauth.setToken(new OAuthToken(token, tokenSecret));
		User user = new User();
		user.setId(userId);
		user.setRealName(userName);
		oauth.setUser(user);
		RequestContext.getRequestContext().setOAuth(oauth);
		return oauth;
	}

	public void saveFlickrAuthToken(OAuth oauth) {
		SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		String oauthToken = null;
		String tokenSecret = null;
		String userId = null;
		String userName = null;
		if (oauth != null) {
			oauthToken = oauth.getToken().getOauthToken();
			tokenSecret = oauth.getToken().getOauthTokenSecret();
			userId = oauth.getUser().getId();
			userName = oauth.getUser().getUsername();
		}
		editor.putString(Constants.FLICKR_TOKEN, oauthToken);
		editor.putString(Constants.FLICKR_TOKEN_SECRENT, tokenSecret);
		editor.putString(Constants.FLICKR_USER_ID, userId);
		editor.putString(Constants.FLICKR_USER_NAME, userName);
		editor.commit();
	}

	public void saveFlickrTokenSecret(String tokenSecrent) {
		SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(Constants.FLICKR_TOKEN_SECRENT, tokenSecrent);
		editor.commit();
	}

	public String getFlickrTokenSecret() {
		return getSharedPreferenceValue(Constants.FLICKR_TOKEN_SECRENT, null);
	}

	public String getUserName() {
		return getSharedPreferenceValue(Constants.FLICKR_USER_NAME, null);
	}

	public String getUserId() {
		return getSharedPreferenceValue(Constants.FLICKR_USER_ID, null);
	}

	/**
	 * Returns the contact upload check interval settings, in 'hour' unit, the
	 * default value is 24 hours.
	 * 
	 * @return
	 */
	public int getContactUploadCheckInterval() {
		String interval = getSharedPreferenceValue(
				Constants.NOTIF_CONTACT_UPLOAD_INTERVAL, String
						.valueOf(Constants.SERVICE_CHECK_INTERVAL));
		return Integer.parseInt(interval);
	}

	/**
	 * Returns the photo activity check interval settings, in 'hour' unit, the
	 * default value is 24 hours.
	 * 
	 * @return
	 */
	public int getPhotoActivityCheckInterval() {
		String interval = getSharedPreferenceValue(
				Constants.NOTIF_PHOTO_ACT_INTERVAL, String
						.valueOf(Constants.SERVICE_CHECK_INTERVAL));
		return Integer.parseInt(interval);
	}

	/**
	 * Whether to start the service to check the contact upload.
	 * 
	 * @return
	 */
	public boolean isContactUploadCheckEnabled() {
		SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
				Context.MODE_APPEND);
		boolean result = sp.getBoolean(Constants.ENABLE_CONTACT_UPLOAD_NOTIF,
				true);
		return result;
	}

	public boolean isPhotoActivityCheckEnabled() {
		SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
				Context.MODE_APPEND);
		return sp.getBoolean(Constants.ENABLE_PHOTO_ACT_NOTIF, true);
	}

	/**
	 * Clear the user token
	 */
	public void logout() {
		// delete the user cache file.
		String token = getFlickrToken();
		File root = new File(Environment.getExternalStorageDirectory(),
				Constants.SD_CARD_FOLDER_NAME);
		if (root.exists()) {
			File cacheFile = new File(root, token + ".dat"); //$NON-NLS-1$
			if (cacheFile.exists()) {
				cacheFile.delete();
			}
		}
		saveFlickrAuthToken(null);
	}

	/**
	 * Returns the saved value in the shared preferences.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private String getSharedPreferenceValue(String key, String defaultValue) {
		SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
				Context.MODE_APPEND);
		String value = sp.getString(key, defaultValue);
		return value;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerTimeCheckReceiver();
		
		DataProviderDelegate delegate = DataProviderDelegate.getInstance();
		delegate.registerOptionMenuResource(PopularPhotoListProvider.class, R.menu.menu_popular_photos);
	}

	/**
	 * Registers a broadcast receiver on the alert manager to check photo
	 * activity or contact upload in the fixed time schedule.
	 */
	private void registerTimeCheckReceiver() {
		String token = getFlickrToken();
		if (token == null) {
			return;
		}

		handleContactUploadService();
		handlePhotoActivityService();
	}

	/**
	 * Registers alarm to check activities on my photoes.
	 */
	public void handlePhotoActivityService() {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pendingIntent = getPhotoCommentPendingIntent();
		am.cancel(pendingIntent);
		if (isPhotoActivityCheckEnabled()) {
			int pIntervalInHours = getPhotoActivityCheckInterval();
			am.setRepeating(AlarmManager.RTC,
					System.currentTimeMillis() + 5 * 60 * 1000L,
					pIntervalInHours * 60 * 60 * 1000L, pendingIntent);
			logger.debug("Receiver registered to check comments on my photos."); //$NON-NLS-1$
		}
	}

	/**
	 * Registers alaram to check contact upload.
	 */
	public void handleContactUploadService() {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pendingIntent = getContactUploadPendingIntent();
		am.cancel(pendingIntent);
		if (isContactUploadCheckEnabled()) {

			int cIntervalInHours = getContactUploadCheckInterval();
			am.setRepeating(AlarmManager.RTC,
					System.currentTimeMillis() + 2 * 60 * 1000L,
					cIntervalInHours * 60 * 60 * 1000L, pendingIntent);
			logger.debug("Receiver registered to check contact upload."); //$NON-NLS-1$
		}
	}

	private PendingIntent getContactUploadPendingIntent() {
		Intent contactUploadIntent = new Intent(this, TimeUpReceiver.class);
		contactUploadIntent
				.setAction(Constants.INTENT_ACTION_CHECK_CONTACT_UPLOAD_RECEIVER);
		PendingIntent contactUploadPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, contactUploadIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return contactUploadPendingIntent;
	}

	private PendingIntent getPhotoCommentPendingIntent() {
		Intent photoIntent = new Intent(this, TimeUpReceiver.class);
		photoIntent
				.setAction(Constants.INTENT_ACTION_CHECK_PHOTO_ACTIVITY_RECEIVER);
		PendingIntent photoPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, photoIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return photoPendingIntent;
	}

	public void registerMessageHandler(IFlickrViewerMessageHandler handler) {
		mMessageHandlers.add(handler);
	}

	public void unregisterMessageHandler(IFlickrViewerMessageHandler handler) {
		mMessageHandlers.remove(handler);
	}

	public void handleMessage(final FlickrViewerMessage message) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				for (IFlickrViewerMessageHandler handler : mMessageHandlers) {
					handler.handleMessage(message);
				}
			}
		});
	}

}
