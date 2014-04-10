/*
 * Created on Sep 27, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;

import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.photosets.Photoset;
import com.gmail.yuyang226.flickr.photosets.PhotosetsInterface;

/**
 * Represents the task to create a photo set, the passed in parameters should be
 * [title, primary photo id, description], in which, 'description' can be null.
 * 
 * @author charles
 * 
 */
public class CreatePhotoSetTask extends AsyncTask<String, Integer, String> {
	
	private static Logger logger = LoggerFactory.getLogger(CreatePhotoSetTask.class.getSimpleName());

	private String mToken, mTokenSecret;
	private IPhotoSetCreationListener mListener;

	public CreatePhotoSetTask(String token, String secret,
			IPhotoSetCreationListener listener) {
		this.mToken = token;
		this.mTokenSecret = secret;
		this.mListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(String... params) {
		if (params.length < 2) {
			throw new IllegalArgumentException(
					"CreatePhotoSetTask, parameter should be [title, primary photo id, [description]."); //$NON-NLS-1$
		}
		String title = params[0];
		String primaryPhotoId = params[1];
		String description = title;
		if (params.length > 2) {
			description = params[2];
		}

		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken,
				mTokenSecret);
		PhotosetsInterface pi = f.getPhotosetsInterface();
		try {
			Photoset ps = pi.create(title, description, primaryPhotoId);
			return ps.getId();
		} catch (Exception ee) {
			logger.warn(ee.getMessage());
			return "error: " + ee.getMessage(); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		boolean fail = result.startsWith("error");  //$NON-NLS-1$
		if( mListener != null ) {
			mListener.onPhotoSetCreated(!fail, result);
		}
	}

	public interface IPhotoSetCreationListener {
		/**
		 * 
		 * @param success
		 *            <code>true</code> says success, in this case,
		 *            <code>msg</code> is the photo set id which is just
		 *            created; <code>false</code> says failure, <code>msg</code>
		 *            in this case, is the error message.
		 * @param msg
		 */
		void onPhotoSetCreated(boolean success, String msg);
	}

}
