/**
 * 
 */

package com.gmail.charleszq.actions;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.Toast;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.PeoplePublicPhotosDataProvider;
import com.gmail.charleszq.event.IPhotoListReadyListener;
import com.gmail.charleszq.task.AsyncPhotoListTask;
import com.gmail.charleszq.ui.PhotoListFragment;
import com.gmail.charleszq.utils.Constants;
import com.gmail.yuyang226.flickr.photos.PhotoList;

/**
 * Represents the action to show all the public photos of a given user.
 * 
 * @author charles
 */
public class ShowPeoplePhotosAction extends ActivityAwareAction {

    private String mUserId;
    private String mUserName;
    private PeoplePublicPhotosDataProvider mDataProvider;
    private IPhotoListReadyListener mPhotosReadyListener = new IPhotoListReadyListener() {

        @Override
        public void onPhotoListReady(PhotoList list, boolean cancelled) {
			if (cancelled) {
        		return;
        	}

        	if (list == null) {
                Toast.makeText(mActivity,
                		mActivity.getString(R.string.toast_error_get_photos),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            PhotoListFragment fragment = new PhotoListFragment(list,
                    mDataProvider);
            FragmentManager fm = mActivity.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(R.id.main_area, fragment);
            ft.addToBackStack(Constants.PHOTO_LIST_BACK_STACK);
            ft.commitAllowingStateLoss();
        }

    };

    /**
     * @param resId
     */
	public ShowPeoplePhotosAction(Activity context, String userId,
			String userName) {
        super(context);
        this.mUserId = userId;
        this.mUserName = userName;
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

        if (mUserId != null && mUserId.equals(app.getUserId())) {
            mActivity.onBackPressed();
            return;
        }

        String token = app.getFlickrToken();
        if (mUserId == null) {
            mUserId = app.getUserId();
        }
		if (mUserName == null) {
            mUserName = app.getUserName();
        }
		mDataProvider = new PeoplePublicPhotosDataProvider(mUserId, token,
				mUserName, app.getFlickrTokenSecret());
        AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity,
                mDataProvider, mPhotosReadyListener);
        task.execute();
    }

}
