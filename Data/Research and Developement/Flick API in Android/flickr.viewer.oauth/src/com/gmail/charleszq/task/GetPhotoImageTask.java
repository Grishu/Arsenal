/*
 * Created on Jul 25, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.task;

import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.charleszq.utils.ImageUtils;
import com.gmail.yuyang226.flickr.photos.GeoData;
import com.gmail.yuyang226.flickr.photos.Photo;
import com.gmail.yuyang226.flickr.photos.PhotosInterface;

/**
 * Represents the task to fetch photo image from flickr.
 * 
 * @author charles
 */
public class GetPhotoImageTask extends
		ProgressDialogAsyncTask<String, Integer, Bitmap> {

	private static final int MSG_ID = R.string.loading_photo_detail;
	private static final Logger logger = LoggerFactory
			.getLogger(GetPhotoImageTask.class);
	private Photo mCurrentPhoto;

	private float mCacheImageScale = 0.5f;

	public static enum PhotoType {
		SMALL_SQR_URL, SMALL_URL, MEDIUM_URL, LARGE_URL, ORG_URL
	}

	private PhotoType mPhotoType = PhotoType.MEDIUM_URL;
	private IPhotoFetchedListener mPhotoFetchedListener;

	public GetPhotoImageTask(Activity activity, IPhotoFetchedListener listener) {
		super(activity, MSG_ID);
		this.mPhotoFetchedListener = listener;
	}

	public GetPhotoImageTask(Activity activity, PhotoType photoType,
			IPhotoFetchedListener listener) {
		super(activity, MSG_ID);
		mPhotoType = photoType;
		this.mPhotoFetchedListener = listener;
	}

	public GetPhotoImageTask(Activity act, PhotoType photoType,
			IPhotoFetchedListener listener, String msg) {
		super(act, msg);
		mPhotoType = photoType;
		this.mPhotoFetchedListener = listener;
	}

	public void setCacheImageScale(float scale) {
		this.mCacheImageScale = scale;
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {

		if (this.isCancelled()) {
			return null;
		}

		FlickrViewerApplication app = (FlickrViewerApplication) mActivity
				.getApplication();
		String token = app.getFlickrToken();
		String tokenSecret = app.getFlickrTokenSecret();
		PhotosInterface pi = null;
		if (token != null) {
			pi = FlickrHelper.getInstance().getFlickrAuthed(token, tokenSecret)
					.getPhotosInterface();
		} else {
			pi = FlickrHelper.getInstance().getPhotosInterface();
		}

		if (pi == null) {
			return null;
		}

		String photoId = arg0[0];
		String secret = arg0[1];

		try {
			mCurrentPhoto = pi.getInfo(photoId, secret);
			logger.debug(
					"Photo description: {}", mCurrentPhoto.getDescription()); //$NON-NLS-1$
			GeoData geo = mCurrentPhoto.getGeoData();
			if (geo != null) {
				logger.debug("Geo data: latitude={}, longtitude={}", //$NON-NLS-1$
						geo.getLatitude(), geo.getLongitude());
			}
			String url = mCurrentPhoto.getMediumUrl();
			switch (mPhotoType) {
			case LARGE_URL:
				url = mCurrentPhoto.getLargeUrl();
				break;
			case SMALL_URL:
				url = mCurrentPhoto.getSmallUrl();
				break;
			case SMALL_SQR_URL:
				url = mCurrentPhoto.getSmallSquareUrl();
				break;
			case MEDIUM_URL:
				url = mCurrentPhoto.getMediumUrl();
				break;
			case ORG_URL:
				url = mCurrentPhoto.getOriginalUrl();
				break;
			}

			File root = new File(Environment.getExternalStorageDirectory(),
					Constants.SD_CARD_FOLDER_NAME);
			File imageFile = new File(root, photoId + ".jpg"); //$NON-NLS-1$

			Bitmap mDownloadedBitmap = null;
			if (imageFile.exists()) {
				mDownloadedBitmap = BitmapFactory
						.decodeStream(new FileInputStream(imageFile));
				mDownloadedBitmap = ImageUtils.resize(mDownloadedBitmap,
						mCacheImageScale);
			} else {
				mDownloadedBitmap = ImageUtils.downloadImage(url);
			}
			return mDownloadedBitmap;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (mPhotoFetchedListener != null) {
			mPhotoFetchedListener.onPhotoFetched(mCurrentPhoto, result);
		}
	}

	public static interface IPhotoFetchedListener {
		void onPhotoFetched(Photo photo, Bitmap bitmap);
	}

}
