/*
 * Created on Jul 26, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.event;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.widget.Toast;

import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.IPhotoListDataProvider;
import com.gmail.charleszq.dataprovider.PaginationPhotoListDataProvider;
import com.gmail.charleszq.ui.PhotoListFragment;
import com.gmail.charleszq.ui.comp.IContextMenuHandler;
import com.gmail.charleszq.utils.Constants;
import com.gmail.yuyang226.flickr.photos.PhotoList;

/**
 * Represents the default photo list ready listener, which shows the default
 * photo grid view after photo fetched.
 * 
 * @author charles
 */
public class DefaultPhotoListReadyListener implements IPhotoListReadyListener {

	private IPhotoListDataProvider mDataProvider;
	private Context mContext;
	private IContextMenuHandler mMenuHandler;
	private boolean mClearStack = true;

	public DefaultPhotoListReadyListener(Context context,
			IPhotoListDataProvider dataProvider) {
		this(context, dataProvider, null);
	}

	public DefaultPhotoListReadyListener(Context context,
			IPhotoListDataProvider dataProvider, IContextMenuHandler handler) {
		this(context, dataProvider, handler, true);
	}

	public DefaultPhotoListReadyListener(Context context,
			IPhotoListDataProvider dataProvider, IContextMenuHandler handler,
			boolean clearStack) {
		this.mContext = context;
		this.mDataProvider = dataProvider;
		this.mMenuHandler = handler;
		this.mClearStack = clearStack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.event.IPhotoListReadyListener#onPhotoListReady(com
	 * .aetrion .flickr.photos.PhotoList, boolean)
	 */
	@Override
	public void onPhotoListReady(PhotoList list, boolean cancelled) {
		if (cancelled) {
			return;
		}
		if (list == null) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.toast_error_get_photos),
					Toast.LENGTH_SHORT).show();
			return;
		}

		PhotoListFragment fragment = new PhotoListFragment(list,
				(PaginationPhotoListDataProvider) mDataProvider, mMenuHandler);
		if( !mDataProvider.hasPrivateInfo() ) {
			fragment.setShowPrivatePhotoMarker(false);
		}
		FragmentManager fm = ((Activity) mContext).getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

		if (mClearStack) {
			int stackCount = fm.getBackStackEntryCount();
			for (int i = 0; i < stackCount; i++) {
				fm.popBackStack();
			}
		}
		ft.replace(R.id.main_area, fragment);
		ft.addToBackStack(Constants.PHOTO_LIST_BACK_STACK);
		ft.commitAllowingStateLoss();
	}
}
