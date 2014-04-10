/*
 * Created on Aug 31, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.task;

import android.os.AsyncTask;

import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.galleries.GalleriesInterface;

/**
 * Represents the task to create a gallery.
 * 
 * @author charles
 * 
 */
public class CreateGalleryTask extends AsyncTask<String, Integer, String> {

	private ICreateGalleryListener mListener;
	private String mToken, mTokenSecret;

	public CreateGalleryTask(String token, String secret,
			ICreateGalleryListener listener) {
		this.mListener = listener;
		this.mToken = token;
		this.mTokenSecret = secret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(String... params) {

		if (params.length != 3)
			throw new IllegalArgumentException(
					"Arguments should be title, description and photo id"); //$NON-NLS-1$
		String title = params[0];
		String description = params[1];
		String primaryPhotoId = params[2];

		GalleriesInterface gi = FlickrHelper.getInstance()
				.getFlickrAuthed(mToken, mTokenSecret).getGalleriesInterface();
		try {
			String galleryId = gi.create(title, description, primaryPhotoId);
			return galleryId;
		} catch (Exception e) {
			return "error: " + e.getMessage(); //$NON-NLS-1$
		}
	}

	@Override
	protected void onPostExecute(String result) {
		// if the task succeed, return the gallery id, otherwise the result will
		// be started with "error: ", then error message is appended after.
		if (mListener != null) {
			mListener.onGalleryCreated(!result.startsWith("error:"), result); //$NON-NLS-1$
		}
	}

	/**
	 * Represents the interface to be notified when gallery or photo set is
	 * created.
	 * 
	 * @author charles
	 * 
	 */
	public interface ICreateGalleryListener {

		/**
		 * 
		 * @param ok
		 *            the success or not status.
		 * @param result
		 *            the gallery id created if success, otherwise, the error
		 *            message.
		 */
		void onGalleryCreated(boolean ok, String result);
	}

}
