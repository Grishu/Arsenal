/**
 * 
 */
package com.gmail.charleszq.model;

/**
 * Represents the interface for the list item model of photo gallery/set/group,
 * the underlying model might be <code>FlickrGallery</code>,
 * <code>Photoset</code> and <code>Group</code>, this interface provides the
 * unified interface so that the adapter can easily handle the titles, and the
 * buddy icons.
 * 
 * @author charles
 * 
 */
public interface IListItemAdapter {
	public static final int PHOTO_GROUP_ID = 0;
	public static final int PHOTO_ID = 1;

	String getId();

	/**
	 * Returns the collection title, that is, the gallery title, photo set title
	 * or the group name.
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Returns the photo url or the photo id.
	 * 
	 * @return
	 */
	String getBuddyIconPhotoIdentifier();

	/**
	 * Returns the photo url type, either 0 or 1, that is, the url or the photo
	 * id.
	 * 
	 * @return
	 */
	int getType();

	/**
	 * Returns the underlying object type, that is, the class name, of this
	 * adapter.
	 * 
	 * @return
	 */
	String getObjectClassType();

	/**
	 * Since the gallery has a limitation that only 18 photos/videos can be put
	 * into it, this method is mainly for this reason, for photo set and photo
	 * group, just return 0 for now.
	 * 
	 * @return
	 */
	int getItemCount();
}
