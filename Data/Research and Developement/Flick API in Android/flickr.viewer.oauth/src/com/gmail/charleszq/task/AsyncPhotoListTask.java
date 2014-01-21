/**
 * 
 */

package com.gmail.charleszq.task;

import android.app.Activity;
import android.util.Log;

import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.IPhotoListDataProvider;
import com.gmail.charleszq.event.DefaultPhotoListReadyListener;
import com.gmail.charleszq.event.IPhotoListReadyListener;
import com.gmail.yuyang226.flickr.photos.PhotoList;

/**
 * Represents the task to fetch the photo list of a user.
 * <p>
 * By default, if no photo list ready listener is specified, we're going to show
 * the photo list into the photo list fragment.
 * 
 * @author charles
 */
public class AsyncPhotoListTask extends
		ProgressDialogAsyncTask<Void, Integer, PhotoList> {

	private IPhotoListDataProvider mPhotoListProvider;
	private IPhotoListReadyListener mPhotoListReadyListener;

	public AsyncPhotoListTask(Activity context,
			IPhotoListDataProvider photoListProvider,
			IPhotoListReadyListener listener) {
		this(context, photoListProvider, listener, context
				.getString(R.string.loading_photos));
	}

	public AsyncPhotoListTask(Activity context,
			IPhotoListDataProvider photoListProvider,
			IPhotoListReadyListener listener, String prompt) {
		this(context, photoListProvider, listener, prompt, true);
	}

	public AsyncPhotoListTask(Activity context,
			IPhotoListDataProvider photoListProvider,
			IPhotoListReadyListener listener, String prompt, boolean cleanStack) {
		super(context, prompt);
		this.mPhotoListProvider = photoListProvider;
		if (listener == null) {
			mPhotoListReadyListener = new DefaultPhotoListReadyListener(
					context, photoListProvider, null, cleanStack);
		} else {
			mPhotoListReadyListener = listener;
		}
		this.mDialogMessage = prompt == null ? context.getResources()
				.getString(R.string.loading_photos) : prompt;
	}

	@Override
	protected PhotoList doInBackground(Void... params) {
		try {
			mPhotoListProvider.invalidatePhotoList();
			return mPhotoListProvider.getPhotoList();
		} catch (Exception e) {
			Log.e("AsyncPhotoListTask", "error to get photo list: " //$NON-NLS-1$//$NON-NLS-2$
					+ e.getMessage());
			return null;
		}
	}

	@Override
	protected void onPostExecute(PhotoList result) {
		super.onPostExecute(result);
		if (mPhotoListReadyListener != null) {
			mPhotoListReadyListener.onPhotoListReady(result, false);
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (mPhotoListReadyListener != null) {
			mPhotoListReadyListener.onPhotoListReady(null, true);
		}
	}

}
