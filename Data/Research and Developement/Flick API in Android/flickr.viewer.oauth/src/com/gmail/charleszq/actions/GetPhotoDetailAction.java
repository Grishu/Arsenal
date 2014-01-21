/*
 * Created on Jul 5, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.actions;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.gmail.charleszq.R;
import com.gmail.charleszq.task.GetPhotoImageTask;
import com.gmail.charleszq.task.GetPhotoImageTask.IPhotoFetchedListener;
import com.gmail.charleszq.ui.ViewImageDetailFragment;
import com.gmail.yuyang226.flickr.photos.Photo;
import com.gmail.yuyang226.flickr.photos.PhotoList;

/**
 * @author charles
 */
public class GetPhotoDetailAction extends ActivityAwareAction implements
		IPhotoFetchedListener {

	private String mPhotoId;
	private String mPhotoSecret;

	private Photo mCurrentPhoto = null;
	private PhotoList mPhotoList = null;

	public GetPhotoDetailAction(Activity activity, String photoId,
			String photoSecret) {
		super(activity);
		this.mPhotoId = photoId;
		this.mPhotoSecret = photoSecret;
	}

	public GetPhotoDetailAction(Activity activity, Photo photo) {
		super(activity);
		this.mPhotoId = photo.getId();
		this.mPhotoSecret = photo.getSecret();
		this.mCurrentPhoto = photo;
	}

	/**
	 * @param activity
	 * @param mCurrentPhoto
	 * @param mPhotoList
	 */
	public GetPhotoDetailAction(Activity activity, Photo photo,
			PhotoList mPhotoList) {
		super(activity);
		this.mPhotoId = photo.getId();
		this.mPhotoSecret = photo.getSecret();
		this.mCurrentPhoto = photo;
		this.mPhotoList = mPhotoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.actions.IAction#execute()
	 */
	@Override
	public void execute() {
		GetPhotoImageTask task = new GetPhotoImageTask(mActivity, this);
		task.execute(mPhotoId, mPhotoSecret);
	}

	@Override
	public void onPhotoFetched(Photo photo, Bitmap bitmap) {
		if (photo == null) {
			Toast.makeText(mActivity,
					mActivity.getString(R.string.error_get_photo_detail),
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (mCurrentPhoto != null && mCurrentPhoto.getGeoData() != null
				&& photo.getGeoData() == null) {
			photo.setGeoData(mCurrentPhoto.getGeoData());
		}
		ViewImageDetailFragment fragment = new ViewImageDetailFragment(photo,
				bitmap, mPhotoList);
		FragmentManager fm = mActivity.getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.main_area, fragment);
		ft.addToBackStack("Detail Image"); //$NON-NLS-1$
		ft.commitAllowingStateLoss();
	}
}
