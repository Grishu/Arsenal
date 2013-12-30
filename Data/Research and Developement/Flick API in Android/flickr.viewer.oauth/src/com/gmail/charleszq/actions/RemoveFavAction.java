/*
 * Created on Jul 26, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.actions;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.favorites.FavoritesInterface;

/**
 * Represents the action to remove a photo from the favorite list.
 * 
 * @author charles
 */
public class RemoveFavAction extends ActivityAwareAction {

    private String mPhotoId;

    /**
     * @param activity
     */
    public RemoveFavAction(Activity activity, String photoId) {
        super(activity);
        this.mPhotoId = photoId;
    }

    /*
     * (non-Javadoc)
	 * 
     * @see com.gmail.charleszq.actions.IAction#execute()
     */
    @Override
    public void execute() {
        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(String... arg0) {
                String photoId = arg0[0];
				FlickrViewerApplication app = (FlickrViewerApplication) mActivity
						.getApplication();
                String mToken = app.getFlickrToken();
				String secret = app.getFlickrTokenSecret();
				Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken,
						secret);
                FavoritesInterface fi = f.getFavoritesInterface();
                try {
                    fi.remove(photoId);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
				String msg = mActivity.getResources().getString(
						R.string.remove_fav_done);
                if (!result) {
					msg = mActivity.getResources().getString(
							R.string.remove_fav_error);
                }
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();

                if (result) {
                    FlickrViewerMessage fmsg = new FlickrViewerMessage(
                            FlickrViewerMessage.FAV_PHOTO_REMOVED, null);
                    FlickrViewerApplication app = (FlickrViewerApplication) mActivity
                            .getApplication();
                    app.handleMessage(fmsg);
                }
            }
        };

        task.execute(mPhotoId);
    }
}
