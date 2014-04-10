/**
 * 
 */
package com.gmail.charleszq.task;

import java.util.List;

import android.app.Activity;

import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.RecentActivitiesDataProvider;
import com.gmail.yuyang226.flickr.activity.Item;

/**
 * Represents the task to get the rencent activites.
 * <p>
 * Parameter is the token.
 * 
 * @author charles
 * 
 */
public class GetActivitiesTask extends
		ProgressDialogAsyncTask<String, Integer, List<Item>> {

	private IActivityFetchedListener mTaskDoneListener;

	public GetActivitiesTask(Activity activity) {
		super(activity, R.string.loading_recent_act);
	}
	
	public GetActivitiesTask(Activity activity, IActivityFetchedListener listener) {
		this(activity);
		this.mTaskDoneListener = listener;
	}

	@Override
	protected List<Item> doInBackground(String... params) {
		String token = params[0];
		String secret = params[1];
		RecentActivitiesDataProvider dp = new RecentActivitiesDataProvider(token, secret);
		dp.setPageSize(30);
		dp.setCheckInterval(24);
		return dp.getRecentActivities();
	}
	
	@Override
	protected void onPostExecute(List<Item> result) {
		super.onPostExecute(result);
		if( mTaskDoneListener != null ) {
			mTaskDoneListener.onActivityFetched(result);
		}
	}

	public static interface IActivityFetchedListener {
		void onActivityFetched(List<Item> items);
	}

}
