/**
 * 
 */

package com.gmail.charleszq.dataprovider;

import android.content.Context;

import com.gmail.charleszq.utils.Constants;
import com.gmail.yuyang226.flickr.photos.PhotoList;

/**
 * Represents the photo list data provider which has the pagination feature.
 * 
 * @author charles
 */
public abstract class PaginationPhotoListDataProvider implements
        IPhotoListDataProvider {

    /**
     * auto generated sid.
     */
    private static final long serialVersionUID = 4702763028164978288L;

    protected int mPageSize = Constants.DEF_GRID_PAGE_SIZE;
    protected int mPageNumber = 1;
    
    /**
     * Don't serialize this. 
     */
    transient protected PhotoList mPhotoList = null;

    public void setPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }

    public void setPageNumber(int mPageNumber) {
        this.mPageNumber = mPageNumber;
    }

    @Override
    public void invalidatePhotoList() {
        this.mPhotoList = null;
    }
    
	@Override
	public boolean hasPrivateInfo() {
		return true;
	}

    /**
     * @return
     */
    public abstract String getDescription(Context context);
}
