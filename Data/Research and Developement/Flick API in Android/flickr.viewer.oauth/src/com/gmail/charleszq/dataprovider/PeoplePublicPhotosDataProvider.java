/**
 * 
 */

package com.gmail.charleszq.dataprovider;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.people.PeopleInterface;
import com.gmail.yuyang226.flickr.photos.Extras;
import com.gmail.yuyang226.flickr.photos.PhotoList;

/**
 * Represents the data provider to get public and private photos.
 * <p>
 * The class name should be changed, since now it can retrieve not only public
 * photos.
 * 
 * @author charles
 */
public class PeoplePublicPhotosDataProvider extends
		PaginationPhotoListDataProvider {

	/**
	 * auto gen sid
	 */
	private static final long serialVersionUID = -1826894885770697192L;

	/**
	 * the flickr user id, whose photos to be fetched. <code>null</code> means
	 * to fetch my own photos.
	 */
	private String mUserId;

	/**
	 * The user name.
	 */
	private String mUserName;

	/**
	 * my own auth token. Some photos needs to know who's viewing the photos.
	 */
	private String mToken;

	/**
	 * The oauth token secret.
	 */
	private String mSecret;

	/**
	 * Constructor.
	 * 
	 * @param userId
	 * @param token
	 */
	public PeoplePublicPhotosDataProvider(String userId, String token,
			String userName, String secret) {
		this.mUserId = userId;
		this.mToken = token;
		this.mUserName = userName;
		this.mSecret = secret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.dataprovider.IPhotoListDataProvider#getPhotoList()
	 */
	@Override
	public PhotoList getPhotoList() throws Exception {

		Flickr f = null;
		if (mToken == null) {
			f = FlickrHelper.getInstance().getFlickr();
		} else {
			f = FlickrHelper.getInstance().getFlickrAuthed(mToken, mSecret);
		}

		PeopleInterface pi = f.getPeopleInterface();
		Set<String> extras = new HashSet<String>();
		extras.add(Extras.TAGS);
		extras.add(Extras.GEO);
		extras.add(Extras.OWNER_NAME);
		extras.add(Extras.VIEWS);

		if (mToken == null) {
			return pi.getPublicPhotos(mUserId, extras, mPageSize, mPageNumber);
		} else {
			return pi.getPhotos(mUserId, extras, mPageSize, mPageNumber);
		}
	}

	@Override
	public String getDescription(Context context) {
		return String.format(context.getResources().getString(
				R.string.photo_stream_of), mUserName);
	}

}
