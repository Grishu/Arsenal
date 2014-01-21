package com.gmail.charleszq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SearchView.OnSuggestionListener;

import com.gmail.charleszq.actions.GetActivitiesAction;
import com.gmail.charleszq.actions.TagSearchAction;
import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.event.IFlickrViewerMessageHandler;
import com.gmail.charleszq.model.RecentTagsCursor;
import com.gmail.charleszq.ui.ContactsFragment;
import com.gmail.charleszq.ui.HelpFragment;
import com.gmail.charleszq.ui.MainNavFragment;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.ImageCache;

public class FlickrViewerActivity extends Activity implements
		OnQueryTextListener {

	/**
	 * The search view to search tags.
	 */
	private SearchView mSearchView;

	/**
	 * The list to store the recent searched tags.
	 */
	private List<String> mRecentSuggestions = new ArrayList<String>();

	/**
	 * The filterd suggestion list.
	 */
	private List<String> mRecentSuggestionFilterd = new ArrayList<String>();

	/**
	 * The adapter for the suggestion list.
	 */
	private CursorAdapter mSuggestionAdapter;

	/**
	 * The message handler to handle the <code>FlickrViewerMessage</code>s.
	 */
	private IFlickrViewerMessageHandler mMessageHandler = new IFlickrViewerMessageHandler() {

		@Override
		public void handleMessage(FlickrViewerMessage message) {
			if (FlickrViewerMessage.ICONFY_TAG_SEARCH_VIEW.equals(message
					.getMessageId())) {
				if (mSearchView != null) {
					mSearchView.setIconified(true);
				}
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		addTagSearchButton();
		addMessageHandler();

		initializeFragments();
		handleIntent();

	}

	/**
	 * Registers message handlers.
	 */
	private void addMessageHandler() {
		FlickrViewerApplication app = (FlickrViewerApplication) getApplication();
		app.registerMessageHandler(mMessageHandler);
	}

	/**
	 * Initializes the action bar, adds the tag search view.
	 */
	private void addTagSearchButton() {
		ActionBar actionBar = getActionBar();
		mSearchView = new SearchView(this);
		mSearchView.setIconified(true);
		mSearchView.setSubmitButtonEnabled(true);
		mSearchView.setQueryHint(getString(R.string.tag_search_hint));
		int option = ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME;
		SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
				Context.MODE_APPEND);
		boolean result = sp.getBoolean(Constants.SETTING_SHOW_APP_TITLE, true);
		if (result) {
			option |= ActionBar.DISPLAY_SHOW_TITLE;
		}
		actionBar.setDisplayOptions(option);
		actionBar.setCustomView(mSearchView);

		mSearchView.setOnQueryTextListener(this);
		final RecentTagsCursor cursor = new RecentTagsCursor(
				mRecentSuggestionFilterd);
		mSuggestionAdapter = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_list_item_1,
				cursor,
				new String[] { "_id" }, new int[] { android.R.id.text1 }, CursorAdapter.FLAG_AUTO_REQUERY); //$NON-NLS-1$
		mSearchView.setSuggestionsAdapter(mSuggestionAdapter);
		mSearchView.setOnSuggestionListener(new OnSuggestionListener() {

			@Override
			public boolean onSuggestionClick(int arg0) {
				cursor.moveToPosition(arg0);
				String tag = cursor.getString(0);
				mSearchView.setQuery(tag, false);
				return true;
			}

			@Override
			public boolean onSuggestionSelect(int arg0) {
				return false;
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent();
	}

	/**
	 * Checks the intent of this activity.
	 */
	private void handleIntent() {
		Intent intent = getIntent();
		if (Constants.CONTACT_UPLOAD_PHOTO_NOTIF_INTENT_ACTION.equals(intent
				.getAction())) {
			showContactsUploads(intent);
		} else if (Constants.ACT_ON_MY_PHOTO_NOTIF_INTENT_ACTION.equals(intent
				.getAction())) {
			GetActivitiesAction aaction = new GetActivitiesAction(this);
			aaction.execute();
		}
	}

	/**
	 * Shows 'my contacts' page with recent uploads.
	 */
	private void showContactsUploads(Intent intent) {
		final String[] cids = intent
				.getStringArrayExtra(Constants.CONTACT_IDS_WITH_PHOTO_UPLOADED);

		Set<String> cidSet = new HashSet<String>();
		for (String cid : cids) {
			cidSet.add(cid);
		}

		FragmentManager fm = getFragmentManager();
		fm.popBackStack(Constants.CONTACT_BACK_STACK,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		FragmentTransaction ft = fm.beginTransaction();
		ContactsFragment fragment = new ContactsFragment(cidSet);
		ft.replace(R.id.main_area, fragment);
		ft.addToBackStack(Constants.CONTACT_BACK_STACK);
		ft.commit();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageCache.dispose();
	}

	public void changeActionBarTitle(String title) {
		String appName = this.getResources().getString(R.string.app_name);

		StringBuilder sb = new StringBuilder(appName);
		if (title != null) {
			sb.append(" - ").append(title); //$NON-NLS-1$
		}

		getActionBar().setTitle(sb.toString());
	}

	private void initializeFragments() {

		final FlickrViewerApplication app = (FlickrViewerApplication) getApplication();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				FlickrViewerMessage msg = new FlickrViewerMessage(
						FlickrViewerMessage.ICONFY_TAG_SEARCH_VIEW, null);
				app.handleMessage(msg);
			}
		});

		MainNavFragment menu = new MainNavFragment();
		ft.replace(R.id.nav_frg, menu, Constants.FRG_TAG_MAIN_NAV);
		ft.commit();

		showHelp();
	}

	/**
	 * Shows the help fragment.
	 */
	private void showHelp() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		HelpFragment help = new HelpFragment();
		ft.replace(R.id.main_area, help, Constants.HELP_BACK_STACK);
		ft.addToBackStack(Constants.HELP_BACK_STACK);
		ft.commit();
	}

	@Override
	public void onBackPressed() {
		FragmentManager fm = getFragmentManager();
		int count = fm.getBackStackEntryCount();
		if (count == 1) {
			Fragment frg = fm.findFragmentByTag(Constants.HELP_BACK_STACK);
			if (frg == null) {
				// the current fragment is not help.
				super.onBackPressed();
				showHelp();
			} else {
				finish();
			}
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			FragmentManager fm = getFragmentManager();
			int count = fm.getBackStackEntryCount();
			for (int i = 0; i < count; i++) {
				fm.popBackStack();
			}
			showHelp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (newText.trim().length() > 0) {
			mRecentSuggestionFilterd.clear();
			for (String tag : mRecentSuggestions) {
				if (tag.contains(newText)) {
					mRecentSuggestionFilterd.add(tag);
				}
			}
			mSuggestionAdapter.notifyDataSetChanged();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if (!mRecentSuggestions.contains(query)) {
			mRecentSuggestions.add(query);
		}
		TagSearchAction action = new TagSearchAction(this, query);
		action.execute();
		mSearchView.setIconified(true);
		return true;
	}
}
