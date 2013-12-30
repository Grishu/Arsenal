/**
 * 
 */
package com.gmail.charleszq.ui.menu;

import android.app.Activity;
import android.view.MenuItem;

import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.PopularPhotoListProvider;
import com.gmail.charleszq.dataprovider.PopularPhotoListProvider.PopularSortType;
import com.gmail.charleszq.event.IPhotoListReadyListener;
import com.gmail.charleszq.task.AsyncPhotoListTask;

/**
 * @author charles
 * 
 */
public class PopularPhotoOptionMenuHandler implements IOptionMenuHandler {

	private PopularPhotoListProvider mPopularPhotoDataProvider;
	private Activity mActivity;
	private IPhotoListReadyListener mPhotoListReadyListener;

	/**
	 * Constructor.
	 * 
	 * @param activity
	 * @param provider
	 */
	public PopularPhotoOptionMenuHandler(Activity activity,
			PopularPhotoListProvider provider, IPhotoListReadyListener listener) {
		this.mActivity = activity;
		this.mPopularPhotoDataProvider = provider;
		this.mPhotoListReadyListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.ui.menu.IOptionMenuHandler#onOptionMenuClicked(android
	 * .view.MenuItem)
	 */
	@Override
	public boolean onOptionMenuClicked(MenuItem item) {
		int itemId = item.getItemId();
		
		PopularSortType type = null;
		switch (itemId) {
		case R.id.menu_item_sort_by_views:
			type = PopularSortType.VIEW;
			item.setChecked(true);
			break;
		case R.id.menu_item_sort_by_comments:
			item.setChecked(true);
			type = PopularSortType.COMMENTS;
			break;
		case R.id.menu_item_sort_by_favs:
			item.setChecked(true);
			type = PopularSortType.FAV;
			break;
		default:
			return false;
		}
		
		mPopularPhotoDataProvider.setSortType(type);
		mPopularPhotoDataProvider.invalidatePhotoList();
		mPopularPhotoDataProvider.setPageNumber(1);
		AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity,
				mPopularPhotoDataProvider, mPhotoListReadyListener);
		task.execute();
		return true;
	}

}
