/**
 * 
 */
package com.gmail.charleszq.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.ViewSwitcher;
import android.widget.SearchView.OnCloseListener;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.event.IFlickrViewerMessageHandler;
import com.gmail.charleszq.ui.comp.UserPhotoCollectionComponent;
import com.gmail.charleszq.utils.Constants;

/**
 * Represents the fragment to show user's galleries, photo sets and photo
 * groups.
 * 
 * @author charles
 */
public class PhotoCollectionFragment extends Fragment implements
		OnClickListener, IFlickrViewerMessageHandler {

	private UserPhotoCollectionComponent mCollectionComponent;
	private ViewSwitcher mSwitcher;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_collection_frag, null);

		mCollectionComponent = (UserPhotoCollectionComponent) view
				.findViewById(R.id.user_collection_list);
		FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
				.getApplication();
		String userId = app.getUserId();
		String token = app.getFlickrToken();
		mCollectionComponent.initialize(userId, token, app
				.getFlickrTokenSecret());

		ImageButton btnBack = (ImageButton) view.findViewById(R.id.btn_back);
		btnBack.setTag(R.id.btn_back);
		btnBack.setOnClickListener(this);

		ImageButton btnRefresh = (ImageButton) view
				.findViewById(R.id.btn_refresh);
		btnRefresh.setTag(R.id.btn_refresh);
		btnRefresh.setOnClickListener(this);

		mSwitcher = (ViewSwitcher) view.findViewById(R.id.switcher);
		ImageButton btnSearch = (ImageButton) view
				.findViewById(R.id.btn_search);
		btnSearch.setTag(R.id.btn_search);
		btnSearch.setOnClickListener(this);

		SearchView searchView = (SearchView) view
				.findViewById(R.id.search_view);
		searchView.setSubmitButtonEnabled(true);
		searchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				if (mSwitcher != null) {
					mSwitcher.showPrevious();
				}
				return true;
			}
		});

		return view;
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if (tag == null) {
			return;
		}

		Integer nTag = (Integer) tag;
		FragmentManager fm = getFragmentManager();
		switch (nTag) {
		case R.id.btn_back:
			fm.popBackStack(Constants.USER_COLL_BACK_STACK,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			break;
		case R.id.btn_refresh:
			if (mCollectionComponent != null) {
				mCollectionComponent.refreshList();
			}
			break;
		case R.id.btn_search:
			if (mSwitcher != null) {
				mSwitcher.showNext();
			}
			break;
		}
	}

	@Override
	public void onDetach() {
		FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
				.getApplication();
		app.unregisterMessageHandler(this);
		super.onDetach();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		FlickrViewerApplication app = (FlickrViewerApplication) activity
				.getApplication();
		app.registerMessageHandler(this);
	}

	@Override
	public void handleMessage(FlickrViewerMessage message) {
		if (FlickrViewerMessage.REFRESH_LOCAL_COLLECTION.equals(message
				.getMessageId())) {
			if (mCollectionComponent != null) {
				mCollectionComponent.refreshList();
			}
		}
	}

}
