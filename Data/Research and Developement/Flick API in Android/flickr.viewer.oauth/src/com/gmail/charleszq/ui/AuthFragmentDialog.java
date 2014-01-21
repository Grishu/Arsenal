/**
 * 
 */

package com.gmail.charleszq.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.actions.IAction;
import com.gmail.charleszq.task.OAuthTask;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthInterface;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;

/**
 * Represents the auth dialog to grant this application the permission to access
 * user's flickr photos.
 * 
 * @author charles
 */
public class AuthFragmentDialog extends DialogFragment {

	private static final Logger logger = LoggerFactory
			.getLogger(AuthFragmentDialog.class);

	/**
	 * Auth dialog might be brought up in several places if not authed before,
	 * so finish action is that the place where the dialog is brought up, then
	 * after auth, we can continue that action.
	 */
	private IAction mFinishAction;

	/**
	 * The handler to run the finish action.
	 */
	private Handler mHandler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(getActivity().getString(R.string.auth_dlg_title));
		View view = inflater.inflate(R.layout.auth_dlg, null);

		Button authButton = (Button) view.findViewById(R.id.button_auth);
		authButton.setTag(R.id.button_auth);
		authButton.setOnClickListener(mClickListener);

		Button authDoneButton = (Button) view
				.findViewById(R.id.button_auth_done);
		authDoneButton.setTag(R.id.button_auth_done);
		authDoneButton.setOnClickListener(mClickListener);

		return view;
	}

	/**
	 * The button click listener.
	 */
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			Integer tag = (Integer) v.getTag();
			if (tag == R.id.button_auth_done) {
				AuthFragmentDialog.this.dismiss();
				return;
			} else {
				OAuthTask task = new OAuthTask(getActivity());
				task.execute();
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getActivity().getIntent();
		String schema = intent.getScheme();
		if (Constants.ID_SCHEME.equals(schema)) {
			Uri uri = intent.getData();
			String query = uri.getQuery();
			logger.debug("Returned Query: {}", query); //$NON-NLS-1$
			String[] data = query.split("&"); //$NON-NLS-1$
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
				String oauthVerifier = data[1]
						.substring(data[1].indexOf("=") + 1); //$NON-NLS-1$
				logger
						.debug(
								"OAuth Token: {}; OAuth Verifier: {}", oauthToken, oauthVerifier); //$NON-NLS-1$

				String secret = getTokenSecret();
				if (secret != null) {
					GetOAuthTokenTask task = new GetOAuthTokenTask(this);
					task.execute(oauthToken, secret, oauthVerifier);
				}
			}
		}

	}

	private String getTokenSecret() {
		FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
				.getApplication();
		return app.getFlickrTokenSecret();
	}

	/**
	 * Represents the task to get the oauth token and user information.
	 * <p>
	 * This task should be called only after you got the request oauth request
	 * token and the verifier.
	 * 
	 * @author charles
	 * 
	 */
	private static class GetOAuthTokenTask extends
			AsyncTask<String, Integer, OAuth> {

		private AuthFragmentDialog mAuthDialog;

		GetOAuthTokenTask(AuthFragmentDialog context) {
			this.mAuthDialog = context;
		}

		@Override
		protected OAuth doInBackground(String... params) {
			String oauthToken = params[0];
			String oauthTokenSecret = params[1];
			String verifier = params[2];

			Flickr f = FlickrHelper.getInstance().getFlickr();
			OAuthInterface oauthApi = f.getOAuthInterface();
			try {
				return oauthApi.getAccessToken(oauthToken, oauthTokenSecret,
						verifier);
			} catch (Exception e) {
				Log.e(AuthFragmentDialog.class.getName(), e.getMessage());
				return null;
			}

		}

		@Override
		protected void onPostExecute(OAuth result) {
			if (mAuthDialog != null) {
				mAuthDialog.onOAuthDone(result);
			}
		}

	}

	void onOAuthDone(OAuth result) {

		if (result == null) {
			Toast.makeText(getActivity(),
					getActivity().getString(R.string.fail_to_oauth),
					Toast.LENGTH_LONG).show();
		} else {

			User user = result.getUser();
			OAuthToken token = result.getToken();
			if (user == null || user.getId() == null || token == null
					|| token.getOauthToken() == null
					|| token.getOauthTokenSecret() == null) {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.fail_to_oauth),
						Toast.LENGTH_LONG).show();
				return;
			}
			FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
					.getApplication();
			app.saveFlickrAuthToken(result);

			//
			MainNavFragment menuFragment = (MainNavFragment) getFragmentManager()
					.findFragmentById(R.id.nav_frg);
			menuFragment.handleUserPanel(menuFragment.getView());

			this.dismiss();

			if (mFinishAction != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mFinishAction.execute();
					}
				});
			}

		}
	}

	/**
	 * Sets the action after auth.
	 * 
	 * @param action
	 */
	public void setFinishAction(IAction action) {
		this.mFinishAction = action;
	}

}
