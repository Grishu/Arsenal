/**
 * 
 */
package com.gmail.charleszq.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents the user comment.
 * <p>
 * <code>Comment</code> class in flickrj does not have the buddy icon for the
 * people who comment;
 * 
 * @author charles
 * 
 */
public final class UserComment {

	private String mBuddyIconUrl;
	private String mUserName;
	private String mCommentText;
	private Date mCommentDate;

	public String getBuddyIconUrl() {
		return mBuddyIconUrl;
	}

	public void setBuddyIconUrl(String mBuddyIconUrl) {
		this.mBuddyIconUrl = mBuddyIconUrl;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	/**
	 * TODO the comment text needs to be handled, there are links in it, like <a
	 * href=""></a> or <img src=""/>
	 * 
	 * @return
	 */
	public String getCommentText() {
		return mCommentText;
	}

	public void setCommentText(String mCommentText) {
		this.mCommentText = mCommentText;
	}

	public String getCommentDateString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"); //$NON-NLS-1$
		try {
			String s = format.format(mCommentDate);
			return s;
		} catch (Exception ex) {
			return mCommentDate.toString();
		}
	}

	public void setCommentDate(Date mCommentDate) {
		this.mCommentDate = mCommentDate;
	}
}
