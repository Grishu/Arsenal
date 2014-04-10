/**
 * 
 */

package com.gmail.charleszq.actions;

import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.Toast;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.task.GetActivitiesTask;
import com.gmail.charleszq.task.GetActivitiesTask.IActivityFetchedListener;
import com.gmail.charleszq.ui.RecentActivityFragment;
import com.gmail.charleszq.utils.Constants;
import com.gmail.yuyang226.flickr.activity.Item;

/**
 * @author charles
 */
public class GetActivitiesAction extends ActivityAwareAction {

	private IActivityFetchedListener mTaskDoneListener = new IActivityFetchedListener() {

		@Override
		public void onActivityFetched(List<Item> items) {
			if (items.isEmpty()) {
				Toast.makeText(
						mActivity,
						mActivity.getResources().getString(
								R.string.toast_no_activities),
						Toast.LENGTH_SHORT).show();
			} else {
				FragmentManager fm = mActivity.getFragmentManager();
				fm.popBackStack(Constants.ACTIVITY_BACK_STACK,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				FragmentTransaction ft = fm.beginTransaction();

				RecentActivityFragment frag = new RecentActivityFragment(items);
				ft.replace(R.id.main_area, frag);
				ft.addToBackStack(Constants.ACTIVITY_BACK_STACK);
				ft.commitAllowingStateLoss();
			}
		}

	};

	public GetActivitiesAction(Activity activity) {
		super(activity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.actions.IAction#execute()
	 */
	@Override
	public void execute() {
		FlickrViewerApplication app = (FlickrViewerApplication) mActivity
				.getApplication();
		String token = app.getFlickrToken();
		GetActivitiesTask task = new GetActivitiesTask(mActivity,
				mTaskDoneListener);
		task.execute(token, app.getFlickrTokenSecret());
	}

}
