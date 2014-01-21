/*
 * Created on Jul 26, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.event;

/**
 * represents the message data structure.
 * 
 * @author charles
 * 
 */
public final class FlickrViewerMessage {

	/**
	 * this message says that a photo was removed from my favorites, then
	 * refresh the favorite view.
	 */
	public static final String FAV_PHOTO_REMOVED = "fav.photo.removed"; //$NON-NLS-1$

	/**
	 * this message says to iconfy the tag search view in the action bar.
	 */
	public static final String ICONFY_TAG_SEARCH_VIEW = "iconfy.search.view"; //$NON-NLS-1$
	
	/**
	 * this message says to refresh the photo comments.
	 */
	public static final String REFRESH_PHOTO_COMMENT = "refresh.photo.comment"; //$NON-NLS-1$

	/**
	 * this message says to refresh the local cache user gallery, set and
	 * groups.
	 */
	public static final String REFRESH_LOCAL_COLLECTION = "refresh.user.photo.coll"; //$NON-NLS-1$
	
	/**
	 * this message says that the pool information of a photo is changed.
	 */
	public static final String REFRESH_PHOTO_POOLS = "refresh.photo.pool"; //$NON-NLS-1$
	
	/**
	 * this message says that a photo was added to pools, then refresh the available pool list, it only 
	 * works on my own photos.
	 */
	public static final String REFRESH_USER_POOL = "refresh.user.pools"; //$NON-NLS-1$

	private String mMessageId;
	private Object mMessageData;

	public FlickrViewerMessage(String id, Object data) {
		this.mMessageId = id;
		this.mMessageData = data;
	}

	public String getMessageId() {
		return mMessageId;
	}

	public Object getMessageData() {
		return mMessageData;
	}

}
