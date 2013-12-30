/**
 * 
 */

package com.gmail.charleszq.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.gmail.charleszq.FlickrViewerActivity;
import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.ImageCache;

/**
 * Represents the fragment for the setting page of whole application.
 * 
 * @author charles
 */
public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final Logger logger = LoggerFactory
			.getLogger(SettingsFragment.class);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager pm = this.getPreferenceManager();
		pm.setSharedPreferencesName(Constants.DEF_PREF_NAME);
		pm.setSharedPreferencesMode(Context.MODE_PRIVATE);

		this.addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onStart() {
		super.onStart();
		PreferenceManager pm = getPreferenceManager();
		SharedPreferences sp = pm.getSharedPreferences();
		sp.registerOnSharedPreferenceChangeListener(this);

		FlickrViewerActivity act = (FlickrViewerActivity) getActivity();
		act.changeActionBarTitle(null);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (Constants.PHOTO_LIST_CACHE_SIZE.equals(key)) {
			String size = sharedPreferences.getString(key, String
					.valueOf(Constants.DEF_CACHE_SIZE));
			ImageCache.CACHE_SIZE = Integer.parseInt(size);
			logger.debug("Cache size changed: {}", size); //$NON-NLS-1$
			return;
		}

		FlickrViewerActivity act = (FlickrViewerActivity) getActivity();
		FlickrViewerApplication app = (FlickrViewerApplication) act
				.getApplication();
		if (Constants.ENABLE_CONTACT_UPLOAD_NOTIF.equals(key)
				|| Constants.NOTIF_CONTACT_UPLOAD_INTERVAL.equals(key)) {
			app.handleContactUploadService();
			return;
		}

		if (Constants.ENABLE_PHOTO_ACT_NOTIF.equals(key)
				|| Constants.NOTIF_PHOTO_ACT_INTERVAL.equals(key)) {
			app.handlePhotoActivityService();
			return;
		}

		if (Constants.SETTING_SHOW_APP_TITLE.equals(key)) {
			boolean result = sharedPreferences.getBoolean(key, true);
			int option = ActionBar.DISPLAY_SHOW_HOME
					| ActionBar.DISPLAY_SHOW_CUSTOM;
			if (result) {
				option |= ActionBar.DISPLAY_SHOW_TITLE;
			}
			getActivity().getActionBar().setDisplayOptions(option);
			return;
		}
	}

	@Override
	public void onStop() {
		SharedPreferences sp = this.getPreferenceManager()
				.getSharedPreferences();
		sp.unregisterOnSharedPreferenceChangeListener(this);
		super.onStop();
	}

}
