/**
 * 
 */
package com.gmail.charleszq.actions;

import android.app.Activity;

import com.gmail.charleszq.task.AddPhotoAsFavoriteTask;

/**
 * @author charles
 * 
 */
public class AddFavAction extends ActivityAwareAction {

	private String mPhotoId;

	/**
	 * @param activity
	 */
	public AddFavAction(Activity activity, String photoId) {
		super(activity);
		this.mPhotoId = photoId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.actions.IAction#execute()
	 */
	@Override
	public void execute() {
		AddPhotoAsFavoriteTask task = new AddPhotoAsFavoriteTask(mActivity);
		task.execute(mPhotoId);
	}

}
