package com.gmail.charleszq.task;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;

import com.gmail.charleszq.event.IExifListener;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.photos.Exif;
import com.gmail.yuyang226.flickr.photos.PhotosInterface;

public class GetPhotoExifTask extends
		AsyncTask<String, Integer, Collection<Exif>> {

	private static final Logger logger = LoggerFactory.getLogger("GetPhotoExifTask"); //$NON-NLS-1$

	private IExifListener mExifListener;

	public GetPhotoExifTask(IExifListener exifListener) {
		this.mExifListener = exifListener;
	}

	@Override
	protected Collection<Exif> doInBackground(String... params) {
		if (this.isCancelled()) {
			return null;
		}

		PhotosInterface pi = FlickrHelper.getInstance().getPhotosInterface();
		if (pi == null) {
			return null;
		}

		String photoId = params[0];
		String secret = params[1];
		Collection<Exif> exifs = null;
		try {
			exifs = pi.getExif(photoId, secret);
		} catch (Exception e) {
			logger.error("Error to get exif information", e); //$NON-NLS-1$
		}

		return exifs;
	}

	@Override
	protected void onPostExecute(Collection<Exif> result) {
		super.onPostExecute(result);
		mExifListener.onExifInfoFetched(result);
	}

}
