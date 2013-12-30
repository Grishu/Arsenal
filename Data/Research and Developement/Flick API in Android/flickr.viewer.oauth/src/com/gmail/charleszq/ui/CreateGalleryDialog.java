/**
 * 
 */
package com.gmail.charleszq.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.task.CreateGalleryTask;
import com.gmail.charleszq.task.CreatePhotoSetTask;
import com.gmail.charleszq.task.CreateGalleryTask.ICreateGalleryListener;
import com.gmail.charleszq.task.CreatePhotoSetTask.IPhotoSetCreationListener;
import com.gmail.charleszq.ui.comp.CreateGalleryComponent;

/**
 * Represents the dialog to create photo gallery or phot set.
 * 
 * @author charles
 * 
 */
public class CreateGalleryDialog extends DialogFragment implements
		OnClickListener, ICreateGalleryListener, IPhotoSetCreationListener {

	/**
	 * The enum type to represent what to be created, photo set, or gallery.
	 */
	public enum CollectionCreationType {
		GALLERY, PHOTO_SET;
	}

	/**
	 * The creation type.
	 */
	private CollectionCreationType mCreationType;

	/**
	 * The photo id, which could be <code>null</code> for <code>GALLERY</code>.
	 */
	private String mPrimaryPhotoId;

	/**
	 * The ui component to create gallery.
	 */
	private CreateGalleryComponent mCreateGalleryComponent;

	/**
	 * Constructor.
	 * 
	 */
	public CreateGalleryDialog(CollectionCreationType createType, String photoId) {
		this.mCreationType = createType;
		this.mPrimaryPhotoId = photoId;
		if (mCreationType == CollectionCreationType.PHOTO_SET
				&& mPrimaryPhotoId == null) {
			throw new IllegalArgumentException(
					"To create photo set, the primary photo id must be provided."); //$NON-NLS-1$
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog()
				.setTitle(
						getActivity()
								.getString(
										mCreationType == CollectionCreationType.GALLERY ? R.string.dlg_title_crt_gallery
												: R.string.dlg_title_crt_photo_set));
		View view = inflater.inflate(R.layout.create_gallery_dlg, null);
		mCreateGalleryComponent = (CreateGalleryComponent) view
				.findViewById(R.id.crt_gallery);
		mCreateGalleryComponent.init();

		// buttons
		Button ok = (Button) view.findViewById(R.id.btn_ok);
		ok.setTag(R.id.btn_ok);
		Button cancel = (Button) view.findViewById(R.id.btn_cancel);
		cancel.setTag(R.id.btn_cancel);

		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View view) {
		Integer tag = (Integer) view.getTag();
		if (tag == R.id.btn_ok) {
			if (mCreateGalleryComponent.validate()) {
				String title = mCreateGalleryComponent.getGalleryTile();
				String description = mCreateGalleryComponent
						.getGalleryDescription();
				if (description == null) {
					description = title;
				}

				FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
						.getApplication();
				if (this.mCreationType == CollectionCreationType.GALLERY) {
					CreateGalleryTask task = new CreateGalleryTask(app
							.getFlickrToken(), app.getFlickrTokenSecret(),
							this);
					task.execute(title, description, mPrimaryPhotoId);
				} else {
					CreatePhotoSetTask psTask = new CreatePhotoSetTask(app
							.getFlickrToken(), app.getFlickrTokenSecret(),
							this);
					psTask.execute(title, mPrimaryPhotoId, description);
				}
			}
		} else if (tag == R.id.btn_cancel) {
			this.dismiss();
		}
	}

	@Override
	public void onGalleryCreated(boolean ok, String result) {
		if (ok) {
			Toast.makeText(getActivity(),
					getActivity().getString(R.string.gallery_created),
					Toast.LENGTH_SHORT).show();
			notifyMessageHandlers(CollectionCreationType.GALLERY);
			dismiss();
		} else {
			Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
		}
	}

	private void notifyMessageHandlers(CollectionCreationType type) {
		FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
				.getApplication();
		FlickrViewerMessage msg = new FlickrViewerMessage(
				FlickrViewerMessage.REFRESH_LOCAL_COLLECTION, null);
		app.handleMessage(msg);

		if (CollectionCreationType.PHOTO_SET == type) {
			FlickrViewerMessage refreshPhotoPoolMessage = new FlickrViewerMessage(
					FlickrViewerMessage.REFRESH_PHOTO_POOLS, mPrimaryPhotoId);
			app.handleMessage(refreshPhotoPoolMessage);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.task.CreatePhotoSetTask.IPhotoSetCreationListener
	 * #onPhotoSetCreated(boolean, java.lang.String)
	 */
	@Override
	public void onPhotoSetCreated(boolean success, String msg) {
		if (success) {
			Toast.makeText(getActivity(),
					getActivity().getString(R.string.photo_set_created),
					Toast.LENGTH_SHORT).show();
			notifyMessageHandlers(CollectionCreationType.PHOTO_SET);
			dismiss();
		} else {
			Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
		}
	}

}
