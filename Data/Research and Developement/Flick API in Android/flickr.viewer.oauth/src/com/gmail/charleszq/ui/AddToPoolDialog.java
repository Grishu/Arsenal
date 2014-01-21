/**
 * 
 */
package com.gmail.charleszq.ui;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.model.IListItemAdapter;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.FlickrException;
import com.gmail.yuyang226.flickr.galleries.GalleriesInterface;
import com.gmail.yuyang226.flickr.galleries.Gallery;
import com.gmail.yuyang226.flickr.groups.pools.PoolsInterface;
import com.gmail.yuyang226.flickr.photos.Photo;
import com.gmail.yuyang226.flickr.photosets.Photoset;
import com.gmail.yuyang226.flickr.photosets.PhotosetsInterface;

/**
 * Represents the dialog to perform the task to add a given photo to pools.
 * 
 * @author charles
 * 
 */
public class AddToPoolDialog extends DialogFragment implements OnClickListener,
		OnCheckedChangeListener {

	static final int PROGRESS_MAX = 100;

	/**
	 * The 2 buttons
	 */
	private Button mBeginButton, mCancelButton;

	/**
	 * The progress bar.
	 */
	private ProgressBar mProgressBar;

	/**
	 * The photo to be added to pools.
	 */
	private Photo mCurrentPhoto;

	/**
	 * The checkbox to say whether starts the task next time.
	 */
	private CheckBox mAutoStart;

	/**
	 * The progress text
	 */
	TextView mProgressMessage;

	/**
	 * The available pools.
	 */
	Map<String, IListItemAdapter> mAvailablePools;

	/**
	 * The pools which the current photo is going to add to.
	 */
	Set<String> mCheckedPools;

	/**
	 * The handler to update the progress message in another thread.
	 */
	private Handler mHandler = new Handler();

	/**
	 * Constructor.
	 */
	public AddToPoolDialog(Photo photo,
			Map<String, IListItemAdapter> availablePools,
			Set<String> checkedPools) {
		this.mCurrentPhoto = photo;
		this.mAvailablePools = availablePools;
		this.mCheckedPools = checkedPools;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().setTitle(
				getActivity().getString(R.string.add_photo_to_pool_dlg_title));

		View view = inflater.inflate(R.layout.add_photo_to_pool_dlg, null);
		mBeginButton = (Button) view.findViewById(R.id.begin_btn);
		mCancelButton = (Button) view.findViewById(R.id.cancel_btn);

		mBeginButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);

		mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		mProgressBar.setMax(PROGRESS_MAX);
		mProgressBar.setProgress(0);

		mAutoStart = (CheckBox) view.findViewById(R.id.cb_donot_show);
		mAutoStart.setChecked(isAutoStart());
		mBeginButton.setEnabled(!mAutoStart.isChecked());
		mAutoStart.setOnCheckedChangeListener(this);

		mProgressMessage = (TextView) view.findViewById(R.id.process_msg);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (isAutoStart()) {
			onClick(mBeginButton);
		}
	}

	/**
	 * Returns the preference setting.
	 * 
	 * @return
	 */
	private boolean isAutoStart() {
		SharedPreferences sp = getActivity().getSharedPreferences(
				Constants.DEF_PREF_NAME, Context.MODE_APPEND);
		return sp.getBoolean(Constants.SETTING_ADD_TO_POOL_AUTO_BEGIN, false);
	}

	@Override
	public void onClick(View v) {
		if (v == mBeginButton) {
			FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
					.getApplication();
			String token = app.getFlickrToken();
			String tokenSecret = app.getFlickrTokenSecret();
			AddPhotoToPoolTask task = new AddPhotoToPoolTask(this);
			task.execute(mCurrentPhoto.getId(), token, tokenSecret);
		} else if (v == mCancelButton) {
			this.dismiss();
		}
	}

	void updateProgressMessage(final String msg) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressMessage.setText(msg);
			}
		});
	}

	/**
	 * Represents the task to add the photo to given pools.
	 * 
	 * @author charles
	 */
	private static class AddPhotoToPoolTask extends
			AsyncTask<String, Integer, String> {

		/**
		 * The logger.
		 */
		private static final Logger logger = LoggerFactory
				.getLogger(AddPhotoToPoolTask.class.getSimpleName());

		/**
		 * The dialog.
		 */
		private AddToPoolDialog mDialog;

		/**
		 * Constructor.
		 */
		AddPhotoToPoolTask(AddToPoolDialog dialog) {
			this.mDialog = dialog;
		}

		@Override
		protected String doInBackground(String... params) {

			String photoId = params[0];
			String token = params[1];
			String tokenSecret = params[2];

			StringBuilder builder = new StringBuilder();

			int index = 0;
			for (String id : mDialog.mCheckedPools) {
				IListItemAdapter item = mDialog.mAvailablePools.get(id);
				index++;
				if (item != null) {
					try {
						addItemToPool(item, photoId, token, tokenSecret);
						builder.append("\n").append("Pool ").append( //$NON-NLS-1$//$NON-NLS-2$
								item.getTitle()).append(" done"); //$NON-NLS-1$
						mDialog.updateProgressMessage(mDialog
								.getString(R.string.photo_added_to)
								+ " " + item.getTitle()); //$NON-NLS-1$
					} catch (Exception e) {
						builder.append("\n").append("Pool ").append( //$NON-NLS-1$//$NON-NLS-2$
								item.getTitle()).append(" fail: ").append( //$NON-NLS-1$
								e.getMessage());
						mDialog
								.updateProgressMessage(mDialog
										.getString(R.string.photo_failed_to_add_to)
										+ " " //$NON-NLS-1$
										+ item.getTitle()
										+ ", " + mDialog.getString(R.string.photo_add_to_pool_fail_reason) //$NON-NLS-1$
										+ e.getMessage());
					}
				} else {
					logger.error("Unable to find the pool by id: " + id); //$NON-NLS-1$
				}
				int percent = index / mDialog.mCheckedPools.size() * 100;
				this.publishProgress(percent);

			}
			return builder.append("\n").toString(); //$NON-NLS-1$
		}

		private void addItemToPool(IListItemAdapter item, String photoId,
				String token, String secret) throws IOException, JSONException,
				FlickrException {
			Flickr f = FlickrHelper.getInstance()
					.getFlickrAuthed(token, secret);
			String objectTypeClass = item.getObjectClassType();
			if (Gallery.class.getName().equals(objectTypeClass)) {
				GalleriesInterface gi = f.getGalleriesInterface();
				gi.addPhoto(item.getId(), photoId, ""); //$NON-NLS-1$
			} else if (Photoset.class.getName().equals(objectTypeClass)) {
				PhotosetsInterface pi = f.getPhotosetsInterface();
				pi.addPhoto(item.getId(), photoId);
			} else {
				PoolsInterface pools = f.getPoolsInterface();
				pools.add(photoId, item.getId());
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// Toast.makeText(mDialog.getActivity(), result, Toast.LENGTH_LONG)
			// .show();
			FlickrViewerMessage msg = new FlickrViewerMessage(
					FlickrViewerMessage.REFRESH_PHOTO_POOLS,
					mDialog.mCurrentPhoto.getId());
			FlickrViewerApplication app = (FlickrViewerApplication) mDialog
					.getActivity().getApplication();
			app.handleMessage(msg);

			FlickrViewerMessage msg2 = new FlickrViewerMessage(
					FlickrViewerMessage.REFRESH_USER_POOL,
					mDialog.mCurrentPhoto.getId());
			app.handleMessage(msg2);

			mDialog.dismiss();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mDialog.mProgressBar.setProgress(values[0]);
			super.onProgressUpdate(values);
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean checked) {
		SharedPreferences sp = getActivity().getSharedPreferences(
				Constants.DEF_PREF_NAME, Context.MODE_APPEND);
		Editor editor = sp.edit();
		editor.putBoolean(Constants.SETTING_ADD_TO_POOL_AUTO_BEGIN, checked);
		editor.commit();
	}

}
