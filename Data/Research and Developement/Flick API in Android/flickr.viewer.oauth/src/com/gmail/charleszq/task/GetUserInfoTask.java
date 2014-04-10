/*
 * Created on Jun 8, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.task;

import java.lang.ref.WeakReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.gmail.charleszq.event.IImageDownloadDoneListener;
import com.gmail.charleszq.event.IUserInfoFetchedListener;
import com.gmail.charleszq.task.ImageDownloadTask.ParamType;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.charleszq.utils.ImageUtils.DownloadedDrawable;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.people.PeopleInterface;
import com.gmail.yuyang226.flickr.people.User;

/**
 * Represents the task to get user information, and set the buddy icon to a
 * provided <code>ImageView</code>.
 * 
 * @author charles
 */
public class GetUserInfoTask extends AsyncTask<String, Integer, User> {

    private static final Logger logger = LoggerFactory.getLogger(GetUserInfoTask.class);

    /**
     * The image view to show the buddy icon.
     */
    private WeakReference<ImageView> mImageViewRef;

    /**
     * The task done listener.
     */
    private IUserInfoFetchedListener mListener;

    /**
     * The image downloaded listener.
     */
    private IImageDownloadDoneListener mImageDownloadedListener;

    /**
     * Constructor.
     * 
     * @param userId the flickr user id.
     */
    public GetUserInfoTask(ImageView imageView,
            IUserInfoFetchedListener listener, IImageDownloadDoneListener imageDownloadedListener) {
        this.mImageViewRef = new WeakReference<ImageView>(imageView);
        this.mListener = listener;
        this.mImageDownloadedListener = imageDownloadedListener;
    }

    @Override
    protected User doInBackground(String... params) {
        String userId = params[0];
        Flickr f = FlickrHelper.getInstance().getFlickr();
        PeopleInterface pi = f.getPeopleInterface();
        try {
            User user = pi.getInfo(userId);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User result) {
        if (result == null) {
        	logger.warn("Unable to get user information."); //$NON-NLS-1$
            return;
        }
        if (mListener != null) {
            mListener.onUserInfoFetched(result);
        }

        String buddyIconUrl = result.getBuddyIconUrl();
        ImageView image = mImageViewRef.get();
        if (image != null) {
            ImageDownloadTask task = new ImageDownloadTask(image, ParamType.PHOTO_URL,
                    mImageDownloadedListener);
            Drawable drawable = new DownloadedDrawable(task);
            image.setImageDrawable(drawable);
            task.execute(buddyIconUrl);
        }
    }
}
