/**
 * 
 */
package com.gmail.charleszq.actions;

import android.app.Activity;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.PopularPhotoListProvider;
import com.gmail.charleszq.task.AsyncPhotoListTask;

/**
 * Represents the action to get my popular photos.
 * 
 * @author charles
 * 
 */
public class ShowMyPopularPhotosAction extends ActivityAwareAction {

	/**
	 * @param activity
	 */
	public ShowMyPopularPhotosAction(Activity activity) {
		super(activity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.actions.IAction#execute()
	 */
	@Override
	public void execute() {

		//TODO: sort should be settings.
		
		FlickrViewerApplication app = (FlickrViewerApplication) mActivity
				.getApplication();
		PopularPhotoListProvider mDataProvider = new PopularPhotoListProvider(
				app.getFlickrToken(), app.getFlickrTokenSecret());
		AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity,
				mDataProvider, null, mActivity.getString(R.string.task_loading_populars));
		task.execute();
	}

}
