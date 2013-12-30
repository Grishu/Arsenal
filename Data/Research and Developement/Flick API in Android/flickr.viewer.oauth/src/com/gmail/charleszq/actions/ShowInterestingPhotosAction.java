/*
 * Created on Jun 15, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.actions;

import android.app.Activity;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.InterestingPhotosDataProvider;
import com.gmail.charleszq.dataprovider.PaginationPhotoListDataProvider;
import com.gmail.charleszq.task.AsyncPhotoListTask;

/**
 * @author charles
 */
public class ShowInterestingPhotosAction extends ActivityAwareAction {

	public ShowInterestingPhotosAction(Activity activity) {
		super(activity);
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
		final PaginationPhotoListDataProvider photoListDataProvider = new InterestingPhotosDataProvider();
		photoListDataProvider.setPageSize(app.getPageSize());
		final AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity,
				photoListDataProvider, null, mActivity.getResources()
						.getString(R.string.task_loading_interest));
		task.execute();
	}

}
