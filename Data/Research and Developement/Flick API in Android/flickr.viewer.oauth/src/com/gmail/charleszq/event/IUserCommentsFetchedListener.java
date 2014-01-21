/**
 * 
 */
package com.gmail.charleszq.event;

import java.util.List;

import com.gmail.charleszq.model.UserComment;

/**
 * Represents the listener to get notified when user comments of a photo is
 * fetched from server.
 * 
 * @author charles
 * 
 */
public interface IUserCommentsFetchedListener {

	/**
	 * Notifies that the user comments of a photo is fetched from server.
	 * 
	 * @param comments
	 */
	void onCommentFetched(List<UserComment> comments);
}
