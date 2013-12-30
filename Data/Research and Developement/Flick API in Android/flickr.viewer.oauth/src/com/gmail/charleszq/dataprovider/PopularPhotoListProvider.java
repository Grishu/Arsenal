/**
 * 
 */
package com.gmail.charleszq.dataprovider;

import android.content.Context;

import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.gmail.yuyang226.flickr.stats.StatsInterface;
import com.gmail.yuyang226.flickr.stats.StatsInterface.SORT;

/**
 * Represents the data provider to fetch my own popular photos.
 * 
 * @author charles
 * 
 */
public class PopularPhotoListProvider extends PaginationPhotoListDataProvider {

	/**
	 * auto gen sid.
	 */
	private static final long serialVersionUID = 786263083339625626L;

	public enum PopularSortType {
		VIEW, COMMENTS, FAV;
	}

	/**
	 * The sort type of my populars.
	 */
	private PopularSortType mSortType = PopularSortType.VIEW;
	private String mAuthToken, mTokenSecret;

	/**
	 * Constructor.
	 */
	public PopularPhotoListProvider(String token, String secret) {
		this(token, secret, PopularSortType.VIEW);
	}

	/**
	 * Constructor.
	 * 
	 * @param token
	 * @param secret
	 * @param sortType
	 */
	public PopularPhotoListProvider(String token, String secret,
			PopularSortType sortType) {
		this.mAuthToken = token;
		this.mTokenSecret = secret;
		this.mSortType = sortType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.gmail.charleszq.dataprovider.PaginationPhotoListDataProvider#
	 * getDescription(android.content.Context)
	 */
	@Override
	public String getDescription(Context context) {
		return context.getString(R.string.dp_desc_pop);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.dataprovider.IPhotoListDataProvider#getPhotoList()
	 */
	@Override
	public PhotoList getPhotoList() throws Exception {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		StatsInterface si = f.getStatsInterface();
		return si.getPopularPhotos(null, getSortString(), mPageSize,
				mPageNumber);
	}

	public void setSortType(PopularSortType type) {
		this.mSortType = type;
	}

	private SORT getSortString() {
		switch (mSortType) {
		case COMMENTS:
			return SORT.COMMENTS;
		case FAV:
			return SORT.FAVORITES;
		default:
			return SORT.VIEWS;
		}
	}

}
