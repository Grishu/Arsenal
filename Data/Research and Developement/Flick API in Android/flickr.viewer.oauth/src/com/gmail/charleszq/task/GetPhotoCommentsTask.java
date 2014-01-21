/**
 * 
 */
package com.gmail.charleszq.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;

import com.gmail.charleszq.event.IUserCommentsFetchedListener;
import com.gmail.charleszq.model.UserComment;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.people.PeopleInterface;
import com.gmail.yuyang226.flickr.people.User;
import com.gmail.yuyang226.flickr.photos.comments.Comment;
import com.gmail.yuyang226.flickr.photos.comments.CommentsInterface;

/**
 * Represents the async task to get the comment for a specified photo which is
 * identified by its id.
 * <p>
 * Since the <code>CommentsInterface</code> only returns the user name who
 * comments, we still need the buddy icon for the commenter, then in this task,
 * after calling <code>CommentsInterface</code>, we need to call
 * <code>PeopleInterface</code> to get buddy icon url.
 * 
 * @author charles
 * 
 */
public class GetPhotoCommentsTask extends
		AsyncTask<String, Integer, List<UserComment>> {

	private static final Logger logger = LoggerFactory.getLogger(GetPhotoCommentsTask.class);
	private IUserCommentsFetchedListener mListener;
	
	public GetPhotoCommentsTask(IUserCommentsFetchedListener listener) {
		this.mListener = listener;
	}

	@Override
	protected List<UserComment> doInBackground(String... params) {

		List<UserComment> comments = new ArrayList<UserComment>();

		String photoId = params[0];
		Flickr f = FlickrHelper.getInstance().getFlickr();
		if (f != null) {
			CommentsInterface ci = f.getCommentsInterface();
			PeopleInterface pi = f.getPeopleInterface();
			try {
				List<Comment> flickrComments = ci.getList(photoId, null, null);
				for (Comment c : flickrComments) {
					UserComment userComment = new UserComment();
					userComment.setUserName(c.getAuthorName());
					userComment.setCommentText(c.getText());
					userComment.setCommentDate(c.getDateCreate());

					User flickrUser = pi.getInfo(c.getAuthor());
					userComment.setBuddyIconUrl(flickrUser.getBuddyIconUrl());
					comments.add(userComment);
				}
			} catch (Exception e) {
				logger.warn(e.getLocalizedMessage(), e);
			}
		}
		return comments;
	}

	@Override
	protected void onPostExecute(List<UserComment> result) {
		mListener.onCommentFetched(result);
	}
}
