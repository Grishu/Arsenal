package com.gmail.charleszq.event;

import com.gmail.yuyang226.flickr.photos.PhotoList;

/**
 * @author charles
 *
 */
public interface IPhotoListReadyListener {

	/**
	 * Called when the photo list ready, or the task is cancelled.
	 * @param list
	 * @param cancelled
	 */
	void onPhotoListReady(PhotoList list, boolean cancelled);
}
