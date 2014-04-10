/**
 * 
 */
package com.gmail.charleszq.ui.comp;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.charleszq.R;
import com.gmail.charleszq.actions.ShowPhotoPoolAction;
import com.gmail.charleszq.model.IListItemAdapter;
import com.gmail.charleszq.task.ImageDownloadTask;
import com.gmail.charleszq.task.ImageDownloadTask.ParamType;
import com.gmail.charleszq.task.UserPhotoCollectionTask;
import com.gmail.charleszq.task.UserPhotoCollectionTask.IUserPhotoCollectionFetched;
import com.gmail.charleszq.utils.ImageCache;
import com.gmail.charleszq.utils.ImageUtils.DownloadedDrawable;
import com.gmail.yuyang226.flickr.galleries.Gallery;
import com.gmail.yuyang226.flickr.photos.PhotoPlace;
import com.gmail.yuyang226.flickr.photosets.Photoset;

/**
 * Represents the UI component to show the photo collection information for a
 * given user. It will include galleries, photo sets and photo groups.
 * 
 * @author charles
 * 
 */
public class UserPhotoCollectionComponent extends FrameLayout implements
		IUserPhotoCollectionFetched, OnItemClickListener {

	private static final Logger logger = LoggerFactory
			.getLogger(UserPhotoCollectionComponent.class);

	/**
	 * The list view
	 */
	private ListView mListView;

	/**
	 * the progress bar.
	 */
	private ProgressBar mProgressBar;

	/**
	 * The async task to fetch the gallery/set/group list of this user.
	 */
	private UserPhotoCollectionTask task;

	/**
	 * The section adapter.
	 */
	private SectionAdapter mSectionAdapter = null;

	private String mUserId;
	private String mToken;

	private String mSecret;

	/**
	 * @param context
	 */
	public UserPhotoCollectionComponent(Context context) {
		super(context);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public UserPhotoCollectionComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public UserPhotoCollectionComponent(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		buildLayout();
	}

	/**
	 * Builds the ui.
	 */
	private void buildLayout() {
		LayoutInflater li = LayoutInflater.from(getContext());
		li.inflate(R.layout.user_photo_col_view, this, true);
		mListView = (ListView) findViewById(R.id.list);
		mProgressBar = (ProgressBar) findViewById(R.id.progress);
	}

	public void initialize(String userId, String token, String secret) {
		this.mUserId = userId;
		this.mToken = token;
		this.mSecret = secret;

		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
		task = new UserPhotoCollectionTask(this);
		task.execute(userId, token, secret);
	}

	/**
	 * Refreshes the collection list from the server side.
	 */
	public void refreshList() {
		if (mUserId == null || mToken == null) {
			return;
		}
		mProgressBar.setVisibility(View.VISIBLE);
		task = new UserPhotoCollectionTask(this, true);
		task.execute(mUserId, mToken, mSecret);
	}

	@Override
	public void onUserPhotoCollectionFetched(
			Map<Integer, List<IListItemAdapter>> map) {
		this.mListView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);

		if (map == null || map.isEmpty()) {
			return;
		}

		if (mSectionAdapter == null) {
			mSectionAdapter = new SimpleSectionAdapter(getContext());
		}
		mSectionAdapter.clearSections();
		for (Integer key : map.keySet()) {
			mSectionAdapter.addSection(getContext().getString(key),
					new PhotoPoolAdapter(getContext(), map.get(key)));
		}
		mListView.setAdapter(mSectionAdapter);
		mListView.setOnItemClickListener(this);
	}

	private class PhotoPoolAdapter extends BaseAdapter {

		private Context mContext;
		private List<IListItemAdapter> mPlaces;

		PhotoPoolAdapter(Context context, List<IListItemAdapter> places) {
			this.mContext = context;
			this.mPlaces = places;
		}

		@Override
		public int getCount() {
			return mPlaces.size();
		}

		@Override
		public Object getItem(int position) {
			return mPlaces.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater li = LayoutInflater.from(mContext);
				view = li.inflate(R.layout.photo_pool_item, null);
			}
			ImageView poolIcon;
			TextView poolTitle;
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder != null) {
				poolTitle = holder.title;
				poolIcon = holder.image;
			} else {
				poolIcon = (ImageView) view.findViewById(R.id.photo_pool_icon);
				poolTitle = (TextView) view.findViewById(R.id.photo_pool_title);

				holder = new ViewHolder();
				holder.image = poolIcon;
				holder.title = poolTitle;
				view.setTag(holder);
			}

			IListItemAdapter place = (IListItemAdapter) getItem(position);
			poolTitle.setText(place.getTitle());

			Drawable drawable = poolIcon.getDrawable();
			String photoPoolId = place.getBuddyIconPhotoIdentifier();
			if (drawable != null && drawable instanceof DownloadedDrawable) {
				ImageDownloadTask task = ((DownloadedDrawable) drawable)
						.getBitmapDownloaderTask();
				if (!photoPoolId.equals(task.getUrl())) {
					task.cancel(true);
				}
			}

			if (photoPoolId != null) {
				Bitmap cacheBitmap = ImageCache.getFromCache(photoPoolId);
				if (cacheBitmap != null) {
					poolIcon.setImageBitmap(cacheBitmap);
				} else {
					ImageDownloadTask task = new ImageDownloadTask(
							poolIcon,
							place.getType() == IListItemAdapter.PHOTO_ID ? ParamType.PHOTO_ID_SMALL_SQUARE
									: ParamType.PHOTO_POOL_ID);
					drawable = new DownloadedDrawable(task);
					poolIcon.setImageDrawable(drawable);
					task.execute(photoPoolId);
				}
			}

			return view;
		}

		class ViewHolder {
			ImageView image;
			TextView title;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int pos,
			long id) {

		ListView list = (ListView) parentView;
		list.setItemChecked(pos, true);

		IListItemAdapter item = (IListItemAdapter) mSectionAdapter.getItem(pos);
		PhotoPlace photoPlace = new ListItemAdapterPhotoPlace(item);
		if (photoPlace != null) {
			ShowPhotoPoolAction action = new ShowPhotoPoolAction(
					(Activity) getContext(), photoPlace, false);
			action.execute();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (task != null && !task.isCancelled()) {
			logger.debug("cancel the running task: {}", task); //$NON-NLS-1$
			task.cancel(true);
		}
		super.onDetachedFromWindow();
	}

	public static class ListItemAdapterPhotoPlace extends PhotoPlace {

		public static final int PHOTO_GALLERY = 1002;

		public ListItemAdapterPhotoPlace(IListItemAdapter item) {
			super(PHOTO_GALLERY, item.getId(), item.getTitle());
			String obj = item.getObjectClassType();
			if (Gallery.class.getName().equals(obj)) {
				setKind(PHOTO_GALLERY);
			} else if (Photoset.class.getName().equals(obj)) {
				setKind(PhotoPlace.SET);
			} else {
				setKind(PhotoPlace.POOL);
			}
		}

	}

}
