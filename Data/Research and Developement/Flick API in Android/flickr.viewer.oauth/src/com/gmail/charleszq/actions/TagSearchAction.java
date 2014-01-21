/**
 * 
 */
package com.gmail.charleszq.actions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.PaginationPhotoListDataProvider;
import com.gmail.charleszq.dataprovider.TagSearchPhotoListDataProvider;
import com.gmail.charleszq.dataprovider.TagSearchPhotoListDataProvider.TagSearchMode;
import com.gmail.charleszq.task.AsyncPhotoListTask;
import com.gmail.charleszq.utils.Constants;

/**
 * @author charles
 * 
 */
public class TagSearchAction extends ActivityAwareAction {

	private String mTags;

	/**
	 * @param activity
	 */
	public TagSearchAction(Activity activity, String tags) {
		super(activity);
		this.mTags = tags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.actions.IAction#execute()
	 */
	@Override
	public void execute() {
		FlickrViewerApplication app = (FlickrViewerApplication) mActivity
				.getApplication();
		SharedPreferences sp = app.getSharedPreferences(
				Constants.DEF_PREF_NAME, Context.MODE_APPEND);
		boolean useAndMode = sp.getBoolean(Constants.SETTING_TAG_SRH_MODE_AND,
				false);
		final PaginationPhotoListDataProvider photoListDataProvider = new TagSearchPhotoListDataProvider(
				mTags, useAndMode ? TagSearchMode.AND : TagSearchMode.ANY);
		photoListDataProvider.setPageSize(app.getPageSize());
		final AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity,
				photoListDataProvider, null, mActivity.getResources()
						.getString(R.string.task_searching_tags));
		task.execute();
	}

}
