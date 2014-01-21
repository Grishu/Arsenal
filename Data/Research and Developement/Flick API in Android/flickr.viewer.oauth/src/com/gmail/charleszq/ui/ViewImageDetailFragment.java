/**
 * 
 */

package com.gmail.charleszq.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ViewAnimator;
import android.widget.ViewSwitcher;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.ViewBigPhotoActivity;
import com.gmail.charleszq.actions.AddFavAction;
import com.gmail.charleszq.actions.IAction;
import com.gmail.charleszq.actions.SaveImageWallpaperAction;
import com.gmail.charleszq.actions.SharePhotoAction;
import com.gmail.charleszq.actions.ShowAuthDialogAction;
import com.gmail.charleszq.actions.ShowPeoplePhotosAction;
import com.gmail.charleszq.actions.ShowWriteCommentAction;
import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.event.IExifListener;
import com.gmail.charleszq.event.IFlickrViewerMessageHandler;
import com.gmail.charleszq.event.IUserCommentsFetchedListener;
import com.gmail.charleszq.model.UserComment;
import com.gmail.charleszq.task.GetPhotoCommentsTask;
import com.gmail.charleszq.task.GetPhotoExifTask;
import com.gmail.charleszq.task.ImageDownloadTask;
import com.gmail.charleszq.ui.comp.AddPhotoToGroupComponent;
import com.gmail.charleszq.ui.comp.PhotoDetailActionBar;
import com.gmail.charleszq.ui.comp.PhotoPoolComponent;
import com.gmail.charleszq.utils.ImageCache;
import com.gmail.charleszq.utils.ImageUtils.DownloadedDrawable;
import com.gmail.charleszq.utils.StringUtils;
import com.gmail.yuyang226.flickr.people.User;
import com.gmail.yuyang226.flickr.photos.Exif;
import com.gmail.yuyang226.flickr.photos.Photo;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.gmail.yuyang226.flickr.tags.Tag;

/**
 * The fragment to view the detail information of a picture, including exif,
 * author, title and comments.
 * 
 * @author charles
 */
public class ViewImageDetailFragment extends Fragment implements
		IUserCommentsFetchedListener, IExifListener,
		IFlickrViewerMessageHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(ViewImageDetailFragment.class);

	private static final String PHOTO_ID_ATTR = "photo.id"; //$NON-NLS-1$
	private static final String PHOTO_TITLE_ATTR = "photo.title"; //$NON-NLS-1$
	private static final String PHOTO_OWNER_ID = "photo.owner.id"; //$NON-NLS-1$
	private static final String PHOTO_DESC_ATTR = "photo.desc"; //$NON-NLS-1$

	private WeakReference<Bitmap> mBitmapRef;
	private Photo mCurrentPhoto;
	private UserCommentAdapter mCommentAdapter;
	private ExifAdapter mExifAdapter;

	/**
	 * The user comments of this photo.
	 */
	private List<UserComment> mComments = new ArrayList<UserComment>();
	private List<Exif> mExifs = new ArrayList<Exif>();
	private PhotoList mPhotoList;

	private ViewAnimator mViewSwitcher;
	private View mCommentProgressBar;
	private View mExifProgressBar;

	private ViewSwitcher mAddGroupViewSwither;
	private AddPhotoToGroupComponent mAddPhotoToGroupComponent;

	/**
	 * The radio group to switch among exif, comment and pool views.
	 */
	private RadioGroup mRadioGroup;

	/**
	 * Default constructor for the framework.
	 */
	public ViewImageDetailFragment() {
		initEmptyPhoto();
	}

	private void initEmptyPhoto() {
		mCurrentPhoto = new Photo();
		mCurrentPhoto.setDescription(StringUtils.EMPTY_STRING);
		mCurrentPhoto.setTitle(StringUtils.EMPTY_STRING);
		User user = new User();
		user.setUsername(StringUtils.EMPTY_STRING);
		mCurrentPhoto.setOwner(user);
	}

	/**
	 * Constructor.
	 * 
	 * @param photo
	 * @param bitmap
	 * @param exifs
	 */
	public ViewImageDetailFragment(Photo photo, Bitmap bitmap, PhotoList mPhotoList) {
		this.mPhotoList = mPhotoList;
		this.mCurrentPhoto = photo;
		mBitmapRef = new WeakReference<Bitmap>(bitmap);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_view_image, menu);
		inflater.inflate(R.menu.menu_view_big_image, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_share:
			IAction action = new SharePhotoAction(getActivity(), mBitmapRef
					.get(), this.mCurrentPhoto.getUrl());
			action.execute();
			return true;
		case R.id.menu_item_write_comment:
			FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
					.getApplication();
			String token = app.getFlickrToken();
			String tokenSecret = app.getFlickrTokenSecret();
			ShowWriteCommentAction commentAction = new ShowWriteCommentAction(
					getActivity(), mCurrentPhoto.getId());
			if (token == null || tokenSecret == null) {
				ShowAuthDialogAction act = new ShowAuthDialogAction(
						getActivity(), commentAction);
				act.execute();
			} else {
				commentAction.execute();
			}
			return true;
		case R.id.menu_item_add_as_fav:
			app = (FlickrViewerApplication) getActivity().getApplication();
			token = app.getFlickrToken();
			tokenSecret = app.getFlickrTokenSecret();
			AddFavAction addfavAction = new AddFavAction(getActivity(),
					mCurrentPhoto.getId());
			if (token == null || tokenSecret == null) {
				ShowAuthDialogAction dlgact = new ShowAuthDialogAction(
						getActivity(), addfavAction);
				dlgact.execute();
			} else {
				addfavAction.execute();
			}
			return true;
		case R.id.menu_item_show_owner_photos:
			ShowPeoplePhotosAction showOwnerPhotosAction = new ShowPeoplePhotosAction(
					getActivity(), mCurrentPhoto.getOwner().getId(),
					mCurrentPhoto.getOwner().getUsername());
			showOwnerPhotosAction.execute();
			return true;
		case R.id.menu_item_view_big_photo:
			showBigImage();
			return true;
		case R.id.menu_item_save:
			SaveImageWallpaperAction sa = new SaveImageWallpaperAction(
					getActivity(), mCurrentPhoto);
			sa.execute();
			return true;
		case R.id.menu_item_wallpaper:
			SaveImageWallpaperAction wallAction = new SaveImageWallpaperAction(
					getActivity(), mCurrentPhoto, true);
			wallAction.execute();
			return true;
		case R.id.menu_item_add_photo_to_group:
			app = (FlickrViewerApplication) getActivity().getApplication();
			token = app.getFlickrToken();
			tokenSecret = app.getFlickrTokenSecret();

			IAction addToPoolAction = new AddPhotoToPoolAction(getActivity(),
					mAddGroupViewSwither, mAddPhotoToGroupComponent,
					mCurrentPhoto);
			if (token == null || tokenSecret == null) {
				ShowAuthDialogAction dlgact = new ShowAuthDialogAction(
						getActivity(), addToPoolAction);
				dlgact.execute();
			} else {
				addToPoolAction.execute();
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Represents the action to show the UI that adds a photo to galleries, sets
	 * or groups
	 * 
	 * @author charles
	 * 
	 */
	private static class AddPhotoToPoolAction implements IAction {

		private Activity mContext;
		private AddPhotoToGroupComponent mComponent;
		private Photo mCurrentPhoto;
		private ViewSwitcher mContainer;

		AddPhotoToPoolAction(Activity context, ViewSwitcher container,
				AddPhotoToGroupComponent component, Photo photo) {
			this.mContext = context;
			this.mContainer = container;
			this.mComponent = component;
			this.mCurrentPhoto = photo;
		}

		@Override
		public void execute() {
			FlickrViewerApplication app = (FlickrViewerApplication) mContext
					.getApplication();
			String userId = app.getUserId();
			String token = app.getFlickrToken();
			String secret = app.getFlickrTokenSecret();

			mComponent.init(mCurrentPhoto, userId, token, secret);
			mContainer.setInAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_right_in));
			mContainer.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_left_out));
			mContainer.showNext();
		}

	}

	private void showBigImage() {
		Intent intent = new Intent(getActivity(), ViewBigPhotoActivity.class);
		intent.putExtra(ViewBigPhotoActivity.PHOTO_ID_KEY, mCurrentPhoto
				.getId());
		intent.putExtra(ViewBigPhotoActivity.PHOTO_SECRET_KEY, mCurrentPhoto
				.getSecret());
		getActivity().startActivity(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_image_detail, null);
		ImageView image = (ImageView) view.findViewById(R.id.image);
		if (mBitmapRef != null && mBitmapRef.get() != null) {
			image.setImageBitmap(mBitmapRef.get());
		}
		image.setFocusable(true);
		image.setClickable(true);
		hookDoubleTapOnImage(image);
		/*if (this.mPhotoList != null) {
			mImageGestureDector = new GestureDetector(mImageGestureListener);
			image.setOnTouchListener(mOnImageTouchListener);
		}*/

		if (savedInstanceState != null) {
			logger
					.debug(
							"Restore photo information from bundle: {}", savedInstanceState); //$NON-NLS-1$
			String photoId = savedInstanceState.getString(PHOTO_ID_ATTR);
			String photoTitle = savedInstanceState.getString(PHOTO_TITLE_ATTR);
			String ownerId = savedInstanceState.getString(PHOTO_OWNER_ID);
			String desc = savedInstanceState.getString(PHOTO_DESC_ATTR);

			if (mCurrentPhoto == null) {
				initEmptyPhoto();
			}

			mCurrentPhoto.setId(photoId);
			mCurrentPhoto.setTitle(photoTitle);
			mCurrentPhoto.setDescription(desc);
			User user = new User();
			user.setId(ownerId);
			mCurrentPhoto.setOwner(user);
		}

		// photo title.
		TextView photoTitle = (TextView) view.findViewById(R.id.titlebyauthor);
		photoTitle.setText(mCurrentPhoto.getTitle());

		// photo description
		TextView photoDesc = (TextView) view.findViewById(R.id.photo_desc);
		if (mCurrentPhoto.getDescription() == null) {
			mCurrentPhoto.setDescription(getActivity().getResources()
					.getString(R.string.no_photo_desc));
		}
		photoDesc.setMovementMethod(LinkMovementMethod.getInstance());
		StringUtils.formatHtmlString(mCurrentPhoto.getDescription(), photoDesc);

		// tags
		TextView tagsText = (TextView) view.findViewById(R.id.photo_tags);
		Collection<Tag> tags = mCurrentPhoto.getTags();
		if (tags == null || tags.isEmpty()) {
			tagsText.setVisibility(View.GONE);
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(getActivity().getString(R.string.msg_tags));
			sb.append(" "); //$NON-NLS-1$
			for (Tag tag : tags) {
				sb.append(tag.getValue()).append(" "); //$NON-NLS-1$
			}
			tagsText.setText(sb.toString());
			tagsText.setSelected(true);
		}
		
		if (mCurrentPhoto.getViews() >= 0) {
			TextView viewsText = (TextView) view.findViewById(R.id.views_text);
			viewsText.setText(String.valueOf(mCurrentPhoto.getViews()));
		}
		
		if (mCurrentPhoto.getComments() >= 0) {
			TextView commentsText = (TextView) view.findViewById(R.id.comments_text);
			commentsText.setText(String.valueOf(mCurrentPhoto.getComments()));
		}
		
		if (mCurrentPhoto.getFavorites() >= 0) {
			TextView favsText = (TextView) view.findViewById(R.id.favourites_text);
			favsText.setText(String.valueOf(mCurrentPhoto.getFavorites()));
		}

		// exif list.
		ListView list = (ListView) view.findViewById(R.id.exifList);
		mExifAdapter = new ExifAdapter(getActivity(), mExifs);
		list.setAdapter(mExifAdapter);
		mExifProgressBar = view.findViewById(R.id.exifProgressBar);

		// comment list.
		ListView commentListView = (ListView) view
				.findViewById(R.id.listComments);
		mCommentAdapter = new UserCommentAdapter(getActivity(), this.mComments);
		commentListView.setAdapter(mCommentAdapter);

		// radio group
		mRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

		// view swithcer
		mViewSwitcher = (ViewAnimator) view.findViewById(R.id.switcher);

		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.exifRadio:
					mViewSwitcher.setDisplayedChild(0);
					break;
				case R.id.commentRadio:
					mViewSwitcher.setDisplayedChild(1);
					break;
				case R.id.poolRadio:
					mViewSwitcher.setDisplayedChild(2);
					break;
				}
			}
		});

		mViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(
				getActivity(), R.anim.push_left_in));
		mViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(
				getActivity(), R.anim.push_right_out));
		mGestureDector = new GestureDetector(mGestureListener);
		list.setOnTouchListener(mOnTouchListener);
		commentListView.setOnTouchListener(mOnTouchListener);

		// photo pool
		PhotoPoolComponent photoPool = (PhotoPoolComponent) view
				.findViewById(R.id.photo_detail_pool);
		photoPool.initialize(mCurrentPhoto.getId(), mOnTouchListener);

		// comment progress bar
		mCommentProgressBar = view.findViewById(R.id.commentProgressBar);

		// get user information.
		PhotoDetailActionBar pBar = (PhotoDetailActionBar) view
				.findViewById(R.id.user_action_bar);
		pBar.setPhoto(mCurrentPhoto);

		// the add group view switcher
		mAddGroupViewSwither = (ViewSwitcher) view
				.findViewById(R.id.add_group_switcher);
		mAddPhotoToGroupComponent = (AddPhotoToGroupComponent) view
				.findViewById(R.id.add_photo_to_set_view);

		return view;
	}

	private void hookDoubleTapOnImage(ImageView image) {
		final GestureDetector imageGestureDector = new GestureDetector(
				new SimpleOnGestureListener());
		imageGestureDector.setOnDoubleTapListener(new OnDoubleTapListener() {

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				showBigImage();
				return true;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				return false;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				return false;
			}
		});
		image.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return imageGestureDector.onTouchEvent(event);
			}
		});
	}

	private GestureDetector mGestureDector;
	private OnGestureListener mGestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1 == null || e2 == null) {
				return false;
			}

			int dx = (int) (e2.getX() - e1.getX());
			int dy = (int) (e2.getY() - e1.getY());
			if (Math.abs(dx) <= Math.abs(dy)) {
				return false;
			}

			if (dx > 50) {
				mViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.push_left_in));
				mViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.push_right_out));
				mViewSwitcher.showPrevious();
				changeRadioGroupState();
				return true;
			} else if (dx < -50) {
				mViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.push_right_in));
				mViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.push_left_out));
				mViewSwitcher.showNext();
				changeRadioGroupState();
				return true;
			} else {
				return false;
			}
		}

		private void changeRadioGroupState() {
			int index = mViewSwitcher.getDisplayedChild();
			switch (index) {
			case 0:
				mRadioGroup.check(R.id.exifRadio);
				break;
			case 1:
				mRadioGroup.check(R.id.commentRadio);
				break;
			case 2:
				mRadioGroup.check(R.id.poolRadio);
				break;
			}
		}
	};

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			return mGestureDector.onTouchEvent(event);
		}

	};
	
	/*private GestureDetector mImageGestureDector;
	private OnGestureListener mImageGestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1 == null || e2 == null) {
				return false;
			}

			int dx = (int) (e2.getX() - e1.getX());
			int dy = (int) (e2.getY() - e1.getY());
			if (Math.abs(dx) <= Math.abs(dy)) {
				return false;
			}
			
			if (dx >= -50 && dx <= 50) {
				return false;
			}
			
			int index = mPhotoList.indexOf(mCurrentPhoto);
			if (index < 0) {
				return false;
			}

			Photo newPhoto = null;
			if (dx > 50 && index > 0) {
				//previous
				newPhoto = mPhotoList.get(index - 1);
			} else if (dx < -50 && index < (mPhotoList.size() - 1)) {
				//next
				newPhoto = mPhotoList.get(index + 1);
			}
			
			if (newPhoto != null) {
				GetPhotoDetailAction action = new GetPhotoDetailAction(getActivity(),
						newPhoto, mPhotoList);
				action.execute();
				return true;
			}
			return false;
		}

	};

	private OnTouchListener mOnImageTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			return mImageGestureDector.onTouchEvent(event);
		}

	};*/

	private GetPhotoCommentsTask mPhotoCommentTask;
	private GetPhotoExifTask mExifTask;

	@Override
	public void onStart() {
		super.onStart();
		String photoId = mCurrentPhoto.getId();
		logger.debug("Current photo id: {}", photoId); //$NON-NLS-1$

		// exif
		mExifTask = new GetPhotoExifTask(this);
		mExifTask.execute(photoId, mCurrentPhoto.getSecret());

		// comments
		mPhotoCommentTask = new GetPhotoCommentsTask(this);
		mPhotoCommentTask.execute(photoId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		FlickrViewerApplication app = (FlickrViewerApplication) activity
				.getApplication();
		app.registerMessageHandler(this);
	}

	@Override
	public void onDetach() {
		FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
				.getApplication();
		app.unregisterMessageHandler(this);
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PHOTO_ID_ATTR, mCurrentPhoto.getId());
		outState.putString(PHOTO_TITLE_ATTR, mCurrentPhoto.getTitle());
		outState.putString(PHOTO_OWNER_ID, mCurrentPhoto.getOwner().getId());
		outState.putString(PHOTO_DESC_ATTR, mCurrentPhoto.getDescription());
		logger.debug("Photo information saved to bundle: {}", outState); //$NON-NLS-1$
	}

	@Override
	public void onPause() {
		if (mPhotoCommentTask != null) {
			mPhotoCommentTask.cancel(true);
		}
		super.onPause();
	}

	/**
	 * Represents the adapter for the user comment list.
	 */
	private static class UserCommentAdapter extends BaseAdapter {

		private List<UserComment> mComments;
		private Context mContext;

		UserCommentAdapter(Context context, List<UserComment> comments) {
			this.mComments = comments;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return mComments.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mComments.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater li = LayoutInflater.from(mContext);
				view = li.inflate(R.layout.user_comment_item, null);
			}

			ImageView buddyIcon;
			TextView author, commentDate, comment;
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				buddyIcon = (ImageView) view.findViewById(R.id.buddy_icon);
				author = (TextView) view.findViewById(R.id.author);
				comment = (TextView) view.findViewById(R.id.comment);
				commentDate = (TextView) view.findViewById(R.id.commentDate);

				holder = new ViewHolder();
				holder.image = buddyIcon;
				holder.author = author;
				holder.comment = comment;
				holder.commentDate = commentDate;
				view.setTag(holder);
			} else {
				buddyIcon = holder.image;
				author = holder.author;
				commentDate = holder.commentDate;
				comment = holder.comment;
			}

			comment.setMovementMethod(LinkMovementMethod.getInstance());
			UserComment userComment = (UserComment) getItem(position);
			author.setText(userComment.getUserName());

			StringUtils.formatHtmlString(userComment.getCommentText(), comment);
			commentDate.setText(userComment.getCommentDateString());

			Drawable drawable = buddyIcon.getDrawable();
			String smallUrl = userComment.getBuddyIconUrl();
			if (drawable != null && drawable instanceof DownloadedDrawable) {
				ImageDownloadTask task = ((DownloadedDrawable) drawable)
						.getBitmapDownloaderTask();
				if (!smallUrl.equals(task.getUrl())) {
					task.cancel(true);
				}
			}

			if (smallUrl == null) {
				buddyIcon.setImageDrawable(null);
			} else {
				Bitmap cacheBitmap = ImageCache.getFromCache(smallUrl);
				if (cacheBitmap != null) {
					buddyIcon.setImageBitmap(cacheBitmap);
				} else {
					ImageDownloadTask task = new ImageDownloadTask(buddyIcon);
					drawable = new DownloadedDrawable(task);
					buddyIcon.setImageDrawable(drawable);
					task.execute(smallUrl);
				}
			}

			return view;
		}

	}

	private static class ViewHolder {
		ImageView image;
		TextView author;
		TextView commentDate;
		TextView comment;
	}

	/**
	 * The adapter for exif list.
	 */
	private static class ExifAdapter extends BaseAdapter {

		private List<Exif> mExifs;
		private Context mContext;

		ExifAdapter(Context context, List<Exif> exifs) {
			this.mExifs = exifs;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return mExifs.size();
		}

		@Override
		public Object getItem(int position) {
			return mExifs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = new TextView(mContext);
			}
			Exif exif = (Exif) getItem(position);
			if (exif != null) {
				((TextView) view).setText(exif.getLabel() + " : " //$NON-NLS-1$
						+ exif.getRaw());
			}
			return view;
		}

	}

	@Override
	public void onCommentFetched(List<UserComment> comments) {
		logger
				.debug(
						"Comments fetched, size={}, contents={}", comments.size(), comments); //$NON-NLS-1$
		this.mComments.clear();
		for (UserComment comment : comments) {
			mComments.add(comment);
		}
		mCommentAdapter.notifyDataSetChanged();
		this.mCommentProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onExifInfoFetched(Collection<Exif> exifs) {
		logger.debug("Exif fetched, contents={}", exifs); //$NON-NLS-1$
		if (exifs == null) {
			mExifProgressBar.setVisibility(View.INVISIBLE);
			return;
		}
		this.mExifs.clear();
		for (Exif exif : exifs) {
			mExifs.add(exif);
		}
		mExifAdapter.notifyDataSetChanged();
		mExifProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem ownerPhotoItem = menu
				.findItem(R.id.menu_item_show_owner_photos);
		MenuItem favItem = menu.findItem(R.id.menu_item_add_as_fav);
		MenuItem addToGroupItem = menu
				.findItem(R.id.menu_item_add_photo_to_group);

		FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
				.getApplication();
		String userId = app.getUserId();
		if (userId == null || mCurrentPhoto == null
				|| mCurrentPhoto.getOwner() == null) {
			return;
		}

		boolean same = userId.equals(mCurrentPhoto.getOwner().getId());
		if (ownerPhotoItem != null) {
			ownerPhotoItem.setVisible(!same);
		}
		if (favItem != null) {
			favItem.setVisible(!same);
		}

		if (mAddGroupViewSwither != null
				&& mAddGroupViewSwither.getCurrentView() instanceof AddPhotoToGroupComponent) {
			if (addToGroupItem != null)
				addToGroupItem.setVisible(false);
		} else {
			if (addToGroupItem != null)
				addToGroupItem.setVisible(true);
		}
	}

	@Override
	public void handleMessage(FlickrViewerMessage message) {
		if (FlickrViewerMessage.REFRESH_PHOTO_COMMENT.equals(message
				.getMessageId())
				&& mCurrentPhoto != null
				&& mCurrentPhoto.getId() != null
				&& mCurrentPhoto.getId().equals(message.getMessageData())) {
			mPhotoCommentTask = new GetPhotoCommentsTask(this);
			mPhotoCommentTask.execute(mCurrentPhoto.getId());
			if (mCommentProgressBar != null) {
				mCommentProgressBar.setVisibility(View.VISIBLE);
			}
		}
	}
}
