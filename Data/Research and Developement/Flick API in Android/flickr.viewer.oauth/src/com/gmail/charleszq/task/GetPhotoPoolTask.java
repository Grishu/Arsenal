/**
 * 
 */
package com.gmail.charleszq.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.photos.PhotoPlace;
import com.gmail.yuyang226.flickr.photos.PhotosInterface;

/**
 * Represents the task to fetch the photo group/set of a given photo, the
 * execute parameter should be [<photoId>, <token>], the token could be
 * <code>null</code>, in this case, will use the un-authed called.
 * 
 * @author charles
 * 
 */
public class GetPhotoPoolTask extends
		AsyncTask<String, Integer, List<PhotoPlace>> {

	private IPhotoPoolListener mListener;

	public GetPhotoPoolTask(IPhotoPoolListener listener) {
		mListener = listener;
	}

	@Override
	protected List<PhotoPlace> doInBackground(String... arg0) {
		String photoId = arg0[0];
		String flickrToken = arg0[1];
		String tokenSecret = arg0[2];

		Flickr f = null;
		if (flickrToken == null) {
			f = FlickrHelper.getInstance().getFlickr();
		} else {
			f = FlickrHelper.getInstance().getFlickrAuthed(flickrToken,
					tokenSecret);
		}
		if (f != null) {
			PhotosInterface pi = f.getPhotosInterface();
			try {
				return pi.getAllContexts(photoId);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<PhotoPlace> result) {
		if (mListener != null) {
			mListener
					.onPhotoPoolFetched(result == null ? new ArrayList<PhotoPlace>()
							: result);
		}
	}

	/**
	 * Represents the interface.
	 */
	public interface IPhotoPoolListener {
		void onPhotoPoolFetched(List<PhotoPlace> photoPlaces);
	}

}
