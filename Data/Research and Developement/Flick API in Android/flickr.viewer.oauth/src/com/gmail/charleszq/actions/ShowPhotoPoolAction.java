/**
 * 
 */
package com.gmail.charleszq.actions;

import android.app.Activity;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.PaginationPhotoListDataProvider;
import com.gmail.charleszq.dataprovider.PhotoPoolDataProvider;
import com.gmail.charleszq.task.AsyncPhotoListTask;
import com.gmail.yuyang226.flickr.photos.PhotoPlace;

/**
 * @author chalres
 * 
 */
public class ShowPhotoPoolAction extends ActivityAwareAction
{

    private PhotoPlace mPhotoPlace;
    private boolean    mClearStack = false;

    /**
     * @param activity
     */
    public ShowPhotoPoolAction(Activity activity, PhotoPlace photoPlace)
    {
        this(activity, photoPlace, false);
    }

    public ShowPhotoPoolAction(Activity activity, PhotoPlace photoPlace, boolean clearStack)
    {
        super(activity);
        this.mPhotoPlace = photoPlace;
        this.mClearStack = clearStack;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gmail.charleszq.actions.IAction#execute()
     */
    @Override
    public void execute()
    {
        FlickrViewerApplication app = (FlickrViewerApplication) mActivity.getApplication();
        final PaginationPhotoListDataProvider photoListDataProvider = new PhotoPoolDataProvider(mPhotoPlace,
                app.getFlickrToken(), app.getFlickrTokenSecret());
        photoListDataProvider.setPageSize(app.getPageSize());
        final AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity, photoListDataProvider, null, mActivity
                .getResources().getString(R.string.task_loading_photo_pool), mClearStack);
        task.execute();
    }

}
