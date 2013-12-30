/**
 * 
 */
package com.gmail.charleszq.ui.comp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.actions.ShowPhotoPoolAction;
import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.event.IFlickrViewerMessageHandler;
import com.gmail.charleszq.task.GetPhotoPoolTask;
import com.gmail.charleszq.task.ImageDownloadTask;
import com.gmail.charleszq.task.GetPhotoPoolTask.IPhotoPoolListener;
import com.gmail.charleszq.task.ImageDownloadTask.ParamType;
import com.gmail.charleszq.utils.ImageCache;
import com.gmail.charleszq.utils.ImageUtils.DownloadedDrawable;
import com.gmail.yuyang226.flickr.photos.PhotoPlace;

/**
 * Represents the ui component to show the photo pool or set information of a
 * given photo.
 * 
 * @author charles
 * 
 */
public class PhotoPoolComponent extends FrameLayout implements
		IPhotoPoolListener, OnItemClickListener, IFlickrViewerMessageHandler {

	private ListView mPhotoPoolListView;
	private ProgressBar mProgressBar;
	private SectionAdapter mSectionAdapter = null;

	private String mCurrentPhotoId = null;

	/**
	 * @param context
	 */
	public PhotoPoolComponent(Context context) {
		super(context);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PhotoPoolComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PhotoPoolComponent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		buildLayout();
	}

	private void buildLayout() {
		LayoutInflater li = LayoutInflater.from(getContext());
		li.inflate(R.layout.photo_pool_view, this, true);
		mPhotoPoolListView = (ListView) findViewById(R.id.listPools);
		mProgressBar = (ProgressBar) findViewById(R.id.photoPoolProgressBar);
	}

	/**
	 * Sets the photo information.
	 * 
	 * @param photoId
	 */
	public void initialize(String photoId, OnTouchListener listener) {
		this.mCurrentPhotoId = photoId;
		mPhotoPoolListView.setOnTouchListener(listener);
		startFetchPoolTask(mCurrentPhotoId);
	}

	private void startFetchPoolTask(String photoId) {

		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.VISIBLE);
		}

		String token = null;
		String secret = null;
		if (getContext() instanceof Activity) {
			FlickrViewerApplication app = (FlickrViewerApplication) ((Activity) getContext())
					.getApplication();
			token = app.getFlickrToken();
			secret = app.getFlickrTokenSecret();
		}
		GetPhotoPoolTask task = new GetPhotoPoolTask(this);
		task.execute(photoId, token, secret);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		FlickrViewerApplication app = (FlickrViewerApplication) ((Activity) getContext())
				.getApplication();
		app.registerMessageHandler(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		FlickrViewerApplication app = (FlickrViewerApplication) ((Activity) getContext())
				.getApplication();
		app.unregisterMessageHandler(this);
		super.onDetachedFromWindow();
	}

	@Override
	public void onPhotoPoolFetched(List<PhotoPlace> photoPlaces) {
		mProgressBar.setVisibility(View.INVISIBLE);

		if (photoPlaces.isEmpty()) {
			return;
		}

		List<PhotoPlace> sets = new ArrayList<PhotoPlace>();
		List<PhotoPlace> groups = new ArrayList<PhotoPlace>();
		for (PhotoPlace place : photoPlaces) {
			if (place.getKind() == PhotoPlace.SET) {
				sets.add(place);
			} else {
				groups.add(place);
			}
		}
		if (mSectionAdapter == null) {
			mSectionAdapter = new SimpleSectionAdapter(getContext());
		}
		mSectionAdapter.clearSections();
		if (!sets.isEmpty()) {
			mSectionAdapter.addSection(getContext().getString(
					R.string.section_photo_set), new PhotoPoolAdapter(
					getContext(), sets));
		}
		if (!groups.isEmpty()) {
			mSectionAdapter.addSection(getContext().getString(
					R.string.section_photo_group), new PhotoPoolAdapter(this
					.getContext(), groups));
		}
		mPhotoPoolListView.setAdapter(mSectionAdapter);
		mPhotoPoolListView.setOnItemClickListener(this);
	}

	private class PhotoPoolAdapter extends BaseAdapter {

		private Context mContext;
		private List<PhotoPlace> mPlaces;

		PhotoPoolAdapter(Context context, List<PhotoPlace> places) {
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

			PhotoPlace place = (PhotoPlace) getItem(position);
			poolTitle.setText(place.getTitle());

			Drawable drawable = poolIcon.getDrawable();
			String photoPoolId = place.getId();
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
							place.getKind() == PhotoPlace.SET ? ParamType.PHOTO_SET_ID
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
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
		PhotoPlace photoPlace = (PhotoPlace) mSectionAdapter.getItem(pos);
		if (photoPlace != null) {
			ShowPhotoPoolAction action = new ShowPhotoPoolAction(
					(Activity) getContext(), photoPlace, false);
			action.execute();
		}
	}

	@Override
	public void handleMessage(FlickrViewerMessage message) {
		if (FlickrViewerMessage.REFRESH_PHOTO_POOLS.equals(message
				.getMessageId())
				&& mCurrentPhotoId != null
				&& mCurrentPhotoId.equals(message.getMessageData())) {
			startFetchPoolTask(mCurrentPhotoId);
		}
	}
}
