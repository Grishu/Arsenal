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
import com.gmail.yuyang226.flickr.favorites.FavoritesInterface;
import com.gmail.yuyang226.flickr.photos.Extras;
import com.gmail.yuyang226.flickr.photos.PhotoList;

/**
 * Represents the data provider to get all the favorite photos of a given user.
 * 
 * @author charles
 */
public class FavoritePhotosDataProvider extends PaginationPhotoListDataProvider {

    /**
     * auto gen sid.
     */
    private static final long serialVersionUID = -3266731748865760819L;

    private String mUserId;
    private String mToken;

	private String mSecret;

    /**
     * Constructor.
     */
	public FavoritePhotosDataProvider(String userId, String token, String secret) {
        this.mUserId = userId;
        this.mToken = token;
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
		if (mPhotoList != null) {
            return mPhotoList;
        }
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken, mSecret);
        FavoritesInterface fi = f.getFavoritesInterface();
        Set<String> extras = new HashSet<String>();
        extras.add(Extras.TAGS);
        extras.add(Extras.GEO);
        extras.add(Extras.OWNER_NAME);
        extras.add(Extras.VIEWS);
		mPhotoList = fi.getList(mUserId, null, null, this.mPageSize, this.mPageNumber,
				extras);
        return mPhotoList;
    }

    @Override
    public String getDescription(Context context) {
        return context.getResources().getString(R.string.dp_desc_fav);
    }
}
