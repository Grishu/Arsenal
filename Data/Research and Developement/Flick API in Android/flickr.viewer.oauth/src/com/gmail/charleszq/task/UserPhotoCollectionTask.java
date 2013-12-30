/**
 * 
 */
package com.gmail.charleszq.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;
import android.os.Environment;

import com.gmail.charleszq.R;
import com.gmail.charleszq.model.IListItemAdapter;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.charleszq.utils.StringUtils;
import com.gmail.yuyang226.flickr.galleries.GalleriesInterface;
import com.gmail.yuyang226.flickr.galleries.Gallery;
import com.gmail.yuyang226.flickr.groups.Group;
import com.gmail.yuyang226.flickr.groups.pools.PoolsInterface;
import com.gmail.yuyang226.flickr.photosets.Photoset;
import com.gmail.yuyang226.flickr.photosets.Photosets;
import com.gmail.yuyang226.flickr.photosets.PhotosetsInterface;

/**
 * Represents the task to fetch the collection of a user, his gallery list,
 * photo set list, and photo group list.
 * <p>
 * To run this task, the parameters must be [user_id, token]
 * 
 * @author charles
 * 
 */
public class UserPhotoCollectionTask extends
		AsyncTask<String, Integer, Map<Integer, List<IListItemAdapter>>> {

	private static final Logger logger = LoggerFactory
			.getLogger(UserPhotoCollectionTask.class);
	private IUserPhotoCollectionFetched mListener;

	/**
	 * The auth token
	 */
	private String mToken;
	private String mSecret;

	private boolean mIsForceFromServer = false;

	/**
	 * Constructor.
	 * 
	 * @param listener
	 */
	public UserPhotoCollectionTask(IUserPhotoCollectionFetched listener) {
		this(listener, false);
	}

	public UserPhotoCollectionTask(IUserPhotoCollectionFetched listener,
			boolean forceFetch) {
		this.mListener = listener;
		this.mIsForceFromServer = forceFetch;
	}

	private Map<Integer, List<IListItemAdapter>> tryGetFromCache()
			throws IOException, JSONException {
		File root = new File(Environment.getExternalStorageDirectory(),
				Constants.SD_CARD_FOLDER_NAME);
		File cacheFile = new File(root, mToken + ".dat"); //$NON-NLS-1$
		if (!cacheFile.exists()) {
			return null;
		}

		List<IListItemAdapter> list = StringUtils.readItemsFromCache(cacheFile);
		Map<Integer, List<IListItemAdapter>> result = new LinkedHashMap<Integer, List<IListItemAdapter>>();

		List<IListItemAdapter> galleries = new ArrayList<IListItemAdapter>();
		List<IListItemAdapter> sets = new ArrayList<IListItemAdapter>();
		List<IListItemAdapter> groups = new ArrayList<IListItemAdapter>();
		for (IListItemAdapter item : list) {
			if (Gallery.class.getName().equals(item.getObjectClassType())) {
				galleries.add(item);
			} else if (Photoset.class.getName().equals(
					item.getObjectClassType())) {
				sets.add(item);
			} else {
				groups.add(item);
			}
		}

		if (!galleries.isEmpty()) {
			result.put(R.string.section_photo_gallery, galleries);
		}
		if (!sets.isEmpty()) {
			result.put(R.string.section_photo_set, sets);
		}
		if (!groups.isEmpty()) {
			result.put(R.string.section_photo_group, groups);
		}
		return result;
	}

	@Override
	protected Map<Integer, List<IListItemAdapter>> doInBackground(
			String... params) {
		String userId = params[0];
		mToken = params[1];
		mSecret = params[2];

		// the key of this map is the string resource id of gallery, or photo
		// set, or photo group.
		Map<Integer, List<IListItemAdapter>> result = null;
		if (!mIsForceFromServer) {
			try {
				result = tryGetFromCache();
			} catch (Exception e1) {
				logger.debug("Can not get item list from cache.", e1); //$NON-NLS-1$
			}

			if (result != null) {
				return result;
			}
		}

		result = new LinkedHashMap<Integer, List<IListItemAdapter>>();
		// galleries
		GalleriesInterface gi = FlickrHelper.getInstance().getFlickrAuthed(
				mToken, mSecret).getGalleriesInterface();
		try {
			List<Gallery> galleries = gi.getList(userId, -1, -1);
			if (!galleries.isEmpty()) {
				List<IListItemAdapter> ga = new ArrayList<IListItemAdapter>();
				for (Gallery gallery : galleries) {
					ga.add(new ListItemAdapter(gallery));
					logger.debug(
							"Gallery item count: {}", gallery.getTotalCount()); //$NON-NLS-1$
				}
				result.put(R.string.section_photo_gallery, ga);
			}
		} catch (Exception e) {
			// just ignore it.
		}

		// photo sets
		PhotosetsInterface psi = FlickrHelper.getInstance().getFlickrAuthed(mToken,mSecret)
				.getPhotosetsInterface();
		try {
			Photosets photosets = psi.getList(userId);
			Collection<?> photosetList = photosets.getPhotosets();
			if (!photosetList.isEmpty()) {
				List<IListItemAdapter> psa = new ArrayList<IListItemAdapter>();
				for (Object photoset : photosetList) {
					psa.add(new ListItemAdapter(photoset));
				}
				result.put(R.string.section_photo_set, psa);
			}
		} catch (Exception e) {
			// just ignore
		}

		// photo groups
		PoolsInterface poolInterface = FlickrHelper.getInstance()
				.getFlickrAuthed(mToken, mSecret).getPoolsInterface();
		try {
			Collection<?> groups = poolInterface.getGroups();
			if (!groups.isEmpty()) {
				List<IListItemAdapter> groupAdapters = new ArrayList<IListItemAdapter>();
				for (Object group : groups) {
					groupAdapters.add(new ListItemAdapter(group));
				}
				result.put(R.string.section_photo_group, groupAdapters);
			}
		} catch (Exception e) {
			// Ignore.
		}

		return result;
	}

	@Override
	protected void onPostExecute(Map<Integer, List<IListItemAdapter>> result) {
		try {
			tryWriteToCache(result);
		} catch (Exception e) {
			logger.warn("Error to write the cache file.", e); //$NON-NLS-1$
		}
		if (mListener != null) {
			mListener.onUserPhotoCollectionFetched(result);
		}
	}

	private void tryWriteToCache(Map<Integer, List<IListItemAdapter>> result)
			throws IOException, JSONException {
		List<IListItemAdapter> list = new ArrayList<IListItemAdapter>();
		for (List<IListItemAdapter> items : result.values()) {
			list.addAll(items);
		}

		File root = new File(Environment.getExternalStorageDirectory(),
				Constants.SD_CARD_FOLDER_NAME);
		if (!root.exists()) {
			root.mkdir();
		}
		File cacheFile = new File(root, mToken + ".dat"); //$NON-NLS-1$
		StringUtils.writeItemsToFile(list, cacheFile);
	}

	public interface IUserPhotoCollectionFetched {

		/**
		 * Notifies the listener when collection information is fetched.
		 * 
		 * @param map
		 *            the key will be R.string.xxx, which will identify the
		 *            section name.
		 */
		void onUserPhotoCollectionFetched(
				Map<Integer, List<IListItemAdapter>> map);
	}

	/**
	 * Represents the model for photo gallery, set and groups.
	 */
	private static class ListItemAdapter implements IListItemAdapter {

		private Object mObject;

		ListItemAdapter(Object object) {
			this.mObject = object;
		}

		@Override
		public String getTitle() {
			if (mObject instanceof Gallery) {
				return ((Gallery) mObject).getTitle();
			} else if (mObject instanceof Photoset) {
				return ((Photoset) mObject).getTitle();
			} else if (mObject instanceof Group) {
				return ((Group) mObject).getName();
			} else {
				throw new IllegalArgumentException("Object type not supported."); //$NON-NLS-1$
			}
		}

		@Override
		public String getBuddyIconPhotoIdentifier() {
			if (mObject instanceof Gallery) {
				return ((Gallery) mObject).getPrimaryPhotoId();
			} else if (mObject instanceof Photoset) {
				return ((Photoset) mObject).getPrimaryPhoto().getId();
			} else if (mObject instanceof Group) {
				return ((Group) mObject).getId();
			} else {
				throw new IllegalArgumentException("Object type not supported."); //$NON-NLS-1$
			}
		}

		@Override
		public int getType() {
			if (mObject instanceof Gallery || mObject instanceof Photoset) {
				return PHOTO_ID;
			} else if (mObject instanceof Group) {
				return PHOTO_GROUP_ID;
			} else {
				throw new IllegalArgumentException("Object type not supported."); //$NON-NLS-1$
			}
		}

		@Override
		public String getObjectClassType() {
			return mObject.getClass().getName();
		}

		@Override
		public String getId() {
			if (mObject instanceof Gallery) {
				return ((Gallery) mObject).getGalleryId();
			} else if (mObject instanceof Photoset) {
				return ((Photoset) mObject).getId();
			} else if (mObject instanceof Group) {
				return ((Group) mObject).getId();
			} else {
				throw new IllegalArgumentException("Object type not supported."); //$NON-NLS-1$
			}
		}

		@Override
		public int getItemCount() {
			if (mObject instanceof Gallery) {
				return ((Gallery) mObject).getTotalCount();
			} else {
				return 0;
			}
		}

	}

}
