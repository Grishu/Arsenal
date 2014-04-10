/**
 * 
 */
package com.gmail.charleszq.dataprovider;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.gmail.charleszq.R;
import com.gmail.charleszq.ui.comp.UserPhotoCollectionComponent.ListItemAdapterPhotoPlace;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.galleries.GalleriesInterface;
import com.gmail.yuyang226.flickr.groups.pools.PoolsInterface;
import com.gmail.yuyang226.flickr.photos.Extras;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.gmail.yuyang226.flickr.photos.PhotoPlace;
import com.gmail.yuyang226.flickr.photosets.PhotosetsInterface;

/**
 * Represents the data provider to get the photos from a given photo set or
 * photo group.
 * 
 * @author charles
 * 
 */
public class PhotoPoolDataProvider extends PaginationPhotoListDataProvider {

	/**
	 * auto gened sid.
	 */
	private static final long serialVersionUID = 7813993447701103209L;

	private String mPhotoPlaceId;
	private int mPhotoPlaceKind;
	private String mPhotoPlaceTitle;
	
	/**
	 * The token and secret to make authed calls
	 */
	private String mToken, mTokenSecret;

	/**
	 * Constructor.
	 * @param photoPlace
	 */
	public PhotoPoolDataProvider(PhotoPlace photoPlace) {
		this.mPhotoPlaceId = photoPlace.getId();
		this.mPhotoPlaceKind = photoPlace.getKind();
		this.mPhotoPlaceTitle = photoPlace.getTitle();
	}
	
	/**
	 * Constructor.
	 * @param photoPlace
	 * @param token
	 * @param tokenSecret
	 */
	public PhotoPoolDataProvider(PhotoPlace photoPlace, String token, String tokenSecret) {
	    this(photoPlace);
	    this.mToken = token;
	    this.mTokenSecret = tokenSecret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.dataprovider.IPhotoListDataProvider#getPhotoList()
	 */
	@Override
	public PhotoList getPhotoList() throws Exception {

		Set<String> extras = new HashSet<String>();
		extras.add(Extras.TAGS);
		extras.add(Extras.GEO);
		extras.add(Extras.OWNER_NAME);
		extras.add(Extras.VIEWS);

		Flickr f = null;
		if( mToken == null || mTokenSecret == null ) {
		    f = FlickrHelper.getInstance().getFlickr();
		} else {
		    f = FlickrHelper.getInstance().getFlickrAuthed(mToken,mTokenSecret);
		}
		switch (mPhotoPlaceKind) {
		case PhotoPlace.SET:
			PhotosetsInterface psi = f.getPhotosetsInterface();
			return psi.getPhotos(mPhotoPlaceId, extras,
					Flickr.PRIVACY_LEVEL_NO_FILTER, mPageSize, mPageNumber);
		case PhotoPlace.POOL:
			PoolsInterface gi = f.getPoolsInterface();
			return gi.getPhotos(mPhotoPlaceId, null, extras, mPageSize,
					mPageNumber);
		case ListItemAdapterPhotoPlace.PHOTO_GALLERY:
			GalleriesInterface galleryInterface = FlickrHelper.getInstance().getFlickr()
					.getGalleriesInterface();
			return galleryInterface.getPhotos(mPhotoPlaceId, extras, mPageSize,
					mPageNumber);

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.dataprovider.PaginationPhotoListDataProvider#
	 * getDescription(android.content.Context)
	 */
	@Override
	public String getDescription(Context context) {
		StringBuilder sb = new StringBuilder();
		if (mPhotoPlaceKind == PhotoPlace.SET) {
			sb.append(context.getString(R.string.dp_photo_pool_desc_set));
		} else {
			sb.append(context.getString(R.string.dp_photo_pool_desc_pool));
		}
		sb.append(" \"").append(mPhotoPlaceTitle); //$NON-NLS-1$
		sb.append("\""); //$NON-NLS-1$
		return sb.toString();
	}

	@Override
	public boolean hasPrivateInfo() {
		return false;
	}

}
