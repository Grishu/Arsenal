/**
 * 
 */

package com.gmail.charleszq.actions;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.FavoritePhotosDataProvider;
import com.gmail.charleszq.dataprovider.IPhotoListDataProvider;
import com.gmail.charleszq.dataprovider.PaginationPhotoListDataProvider;
import com.gmail.charleszq.event.DefaultPhotoListReadyListener;
import com.gmail.charleszq.event.IPhotoListReadyListener;
import com.gmail.charleszq.task.AsyncPhotoListTask;
import com.gmail.charleszq.ui.comp.IContextMenuHandler;
import com.gmail.yuyang226.flickr.photos.Photo;

/**
 * @author charles
 */
public class ShowFavoritesAction extends ActivityAwareAction {

    private String mUserId;

    /**
     * @param activity
     */
    public ShowFavoritesAction(Activity activity, String userId) {
        super(activity);
        this.mUserId = userId;
    }

    /*
     * (non-Javadoc)
	 * 
     * @see com.gmail.charleszq.actions.IAction#execute()
     */
    @Override
    public void execute() {
        FlickrViewerApplication app = (FlickrViewerApplication) mActivity
                .getApplication();
        String token = app.getFlickrToken();
        if (mUserId == null) {
            mUserId = app.getUserId();
        }
        PaginationPhotoListDataProvider dp = new FavoritePhotosDataProvider(
				mUserId, token, app.getFlickrTokenSecret());
		IContextMenuHandler menuHandler = new FavContextMenuHandler(mActivity,
				dp);
		IPhotoListReadyListener photoReadyListener = new DefaultPhotoListReadyListener(
				mActivity, dp, menuHandler);
		AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity, dp,
				photoReadyListener, mActivity.getString(
						R.string.task_loading_favs));
        task.execute();
    }

    private class FavContextMenuHandler implements IContextMenuHandler {

        private Activity mActivity;
        private IPhotoListDataProvider mDataProvider;

        /**
         * Constructor.
         * 
         * @param activity
         */
		FavContextMenuHandler(Activity activity,
				IPhotoListDataProvider dataProvider) {
            this.mActivity = activity;
            this.mDataProvider = dataProvider;
        }

        @Override
		public void onCreateContextMenu(ContextMenu menu, View view,
				ContextMenuInfo info) {
            MenuInflater mi = mActivity.getMenuInflater();
            int start = menu.size();
            mi.inflate(R.menu.menu_remove_fav, menu);
            for (int i = start; i < menu.size(); i++) {
                menu.getItem(i).setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ContextMenuInfo info = item.getMenuInfo();
            int pos = ((AdapterContextMenuInfo) info).position;
            try {
                Photo photo = mDataProvider.getPhotoList().get(pos);
				RemoveFavAction action = new RemoveFavAction(mActivity, photo
						.getId());
                action.execute();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

}
