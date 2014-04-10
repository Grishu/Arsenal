/**
 * 
 */

package com.gmail.charleszq.dataprovider;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.activity.ActivityInterface;
import com.gmail.yuyang226.flickr.activity.Item;
import com.gmail.yuyang226.flickr.activity.ItemList;

/**
 * Represents the data provider for the activities.
 * 
 * @author charles
 */
public class RecentActivitiesDataProvider {

	private static final int PER_PAGE = 20;
	private static final Logger logger = LoggerFactory
			.getLogger(RecentActivitiesDataProvider.class);

	private String mToken;
	private String mSecret;
	private boolean mOnlyMyPhoto = false;

	/**
	 * The check interval of activities on my photos.
	 */
	private int mMyPhotoInterval = Constants.SERVICE_CHECK_INTERVAL;

	/**
	 * The page size.
	 */
	private int mPageSize = -1;

	/**
	 * Constructor.
	 * 
	 * @param token
	 *            the access token
	 */
	public RecentActivitiesDataProvider(String token, String secret) {
		this.mToken = token;
		this.mSecret = secret;
	}

	public RecentActivitiesDataProvider(String token, String secret,
			boolean onlyMyPhoto) {
		this(token, secret);
		this.mOnlyMyPhoto = onlyMyPhoto;
	}

	/**
	 * Returns the recent activities, including both the activities of my own
	 * photo, and that on the photos commented by me.
	 * <p>
	 * TODO only support photo item right now, for 'photoset', support later.
	 * 
	 * @return
	 */
	public List<Item> getRecentActivities() {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken, mSecret);
		ActivityInterface ai = f.getActivityInterface();

		List<Item> items = new ArrayList<Item>();
		try {
			if (!mOnlyMyPhoto) {
				ItemList userComments = ai.userComments(getPageSize(), 1);
				if (userComments != null) {
					for (int i = 0; i < userComments.size(); i++) {
						Item item = userComments.get(i);
						logger.debug("Activity item type: {}", item.getType()); //$NON-NLS-1$
						if ("photo".equals(item.getType())) { //$NON-NLS-1$
							items.add(item);
						}
					}
				}
			}

			String sInterval = String.valueOf(mMyPhotoInterval) + "h"; //$NON-NLS-1$
			if (mMyPhotoInterval == 24) {
				sInterval = "1d"; //$NON-NLS-1$
			}

			ItemList photoComments = ai.userPhotos(getPageSize(), 1, sInterval);
			if (photoComments != null) {
				for (int j = 0; j < photoComments.size(); j++) {
					Item item = photoComments.get(j);
					logger.debug("Activity item type: {}", item.getType()); //$NON-NLS-1$
					if ("photo".equals(item.getType())) { //$NON-NLS-1$
						items.add(item);
					}
				}
			}

		} catch (Exception e) {
		}
		return items;
	}

	/**
	 * Sets the check interval which indicates that since when we're going to
	 * check activities on my photos.
	 * 
	 * @param interval
	 */
	public void setCheckInterval(int interval) {
		this.mMyPhotoInterval = interval;
	}

	private int getPageSize() {
		return mPageSize == -1 ? PER_PAGE : mPageSize;
	}

	public void setPageSize(int mPageSize) {
		this.mPageSize = mPageSize;
	}

}
