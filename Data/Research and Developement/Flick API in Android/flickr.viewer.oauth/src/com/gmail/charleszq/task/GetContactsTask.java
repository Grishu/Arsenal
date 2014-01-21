/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.task;

import java.util.Collection;

import android.app.Activity;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.DefaultContactDataProvider;
import com.gmail.charleszq.event.IContactsFetchedListener;
import com.gmail.yuyang226.flickr.contacts.Contact;

/**
 * @author charles
 */
public class GetContactsTask extends
		ProgressDialogAsyncTask<String, Integer, Collection<Contact>> {

    private IContactsFetchedListener mListener;

    public GetContactsTask(Activity activity, IContactsFetchedListener listener) {
        super(activity, R.string.loading_contacts);
        this.mListener = listener;
    }

    @Override
    protected Collection<Contact> doInBackground(String... params) {
		FlickrViewerApplication app = (FlickrViewerApplication) mActivity
				.getApplication();
        String token = app.getFlickrToken();
		String secret = app.getFlickrTokenSecret();
		DefaultContactDataProvider dp = new DefaultContactDataProvider(token,
				secret);
        return dp.getContacts(params[0]);
    }

    @Override
    protected void onPostExecute(Collection<Contact> result) {
        super.onPostExecute(result);
        if (mListener != null) {
            mListener.onContactsFetched(result);
        }
    }

}
