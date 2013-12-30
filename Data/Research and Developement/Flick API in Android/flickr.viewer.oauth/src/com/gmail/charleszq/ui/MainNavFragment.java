/**
 * 
 */

package com.gmail.charleszq.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.actions.GetActivitiesAction;
import com.gmail.charleszq.actions.IAction;
import com.gmail.charleszq.actions.ShowAuthDialogAction;
import com.gmail.charleszq.actions.ShowFavoritesAction;
import com.gmail.charleszq.actions.ShowInterestingPhotosAction;
import com.gmail.charleszq.actions.ShowMyContactsAction;
import com.gmail.charleszq.actions.ShowMyPopularPhotosAction;
import com.gmail.charleszq.actions.ShowPeoplePhotosAction;
import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.event.IImageDownloadDoneListener;
import com.gmail.charleszq.task.GetUserInfoTask;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.ImageUtils;

/**
 * Represents the fragment to be shown at the left side of the screen, which
 * acts as the main menu.
 * 
 * @author charles
 */
public class MainNavFragment extends Fragment {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(MainNavFragment.class.getSimpleName());

	/**
	 * the handler.
	 */
	private Handler mHandler = new Handler();

	/**
	 * The item click listner to handle the main menus.
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			int itemId = (int) id;
			if (itemId != CommandItem.ID_MY_SETS) {
				cleanFragmentBackStack();
			}

			ListView list = (ListView) parent;
			list.setItemChecked(position, true);

			FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
					.getApplication();
			FlickrViewerMessage iconTagSearchMessage = new FlickrViewerMessage(
					FlickrViewerMessage.ICONFY_TAG_SEARCH_VIEW, null);
			app.handleMessage(iconTagSearchMessage);

			switch (itemId) {
			case CommandItem.ID_INTERESTING:
				ShowInterestingPhotosAction action = new ShowInterestingPhotosAction(
						getActivity());
				action.execute();
				break;
			case CommandItem.ID_MY_PHOTOS:
				ShowPeoplePhotosAction photosAction = new ShowPeoplePhotosAction(
						getActivity(), null, app.getUserName());
				showAuthBeforeRun(photosAction);
				break;
			case CommandItem.ID_MY_SETS: // my photo sets and groups
				IAction switchAction = new IAction() {
					@Override
					public void execute() {
						FragmentManager fm = getActivity().getFragmentManager();
						FragmentTransaction ft2 = fm.beginTransaction();
						Fragment setFragment = new PhotoCollectionFragment();
						Fragment mainNavFragment = fm
								.findFragmentByTag(Constants.FRG_TAG_MAIN_NAV);
						if (mainNavFragment != null) {
							ft2.remove(mainNavFragment);
						}
						ft2.add(R.id.nav_frg, setFragment);
						// ft2.replace(R.id.nav_frg, setFragment);
						ft2.addToBackStack(Constants.USER_COLL_BACK_STACK);
						ft2.commit();
					}
				};
				showAuthBeforeRun(switchAction);

				break;
			case CommandItem.ID_MY_CONTACTS: // contacts
				ShowMyContactsAction contactAction = new ShowMyContactsAction(
						getActivity());
				showAuthBeforeRun(contactAction);
				break;
			case CommandItem.ID_MY_FAV:
				ShowFavoritesAction favAction = new ShowFavoritesAction(
						getActivity(), null);
				showAuthBeforeRun(favAction);
				break;
			case CommandItem.ID_ACTIVITY:
				GetActivitiesAction aaction = new GetActivitiesAction(
						getActivity());
				showAuthBeforeRun(aaction);
				break;
			case CommandItem.ID_SETTINGS:
				Fragment frag = new SettingsFragment();
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.main_area, frag);
				ft.addToBackStack(Constants.SETTING_BACK_STACK);
				ft.commit();
				break;
			case CommandItem.ID_MY_POPULAR:
				ShowMyPopularPhotosAction showPopularAction = new ShowMyPopularPhotosAction(
						getActivity());
				showAuthBeforeRun(showPopularAction);
				break;
			}
		}

		private void showAuthBeforeRun(IAction action) {
			FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
					.getApplication();
			String token = app.getFlickrToken();
			String tokenSecret = app.getFlickrTokenSecret();
			if (token == null || tokenSecret == null) {
				ShowAuthDialogAction showAuthAction = new ShowAuthDialogAction(
						getActivity(), action);
				showAuthAction.execute();
			} else {
				action.execute();
			}
		}
	};

	/**
	 * Clears all the framgment back stack, the right panel can show only the
	 * current selected contents.
	 */
	private void cleanFragmentBackStack() {
		FragmentManager fm = getFragmentManager();
		fm.popBackStack(Constants.PHOTO_LIST_BACK_STACK,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fm.popBackStack(Constants.SETTING_BACK_STACK,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fm.popBackStack(Constants.CONTACT_BACK_STACK,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fm.popBackStack(Constants.ACTIVITY_BACK_STACK,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fm.popBackStack(Constants.HELP_BACK_STACK,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_nav, null);

		// main menu
		final ListView list = (ListView) view.findViewById(R.id.list_menu);
		NavMenuAdapter adapter = new NavMenuAdapter(getActivity(),
				createNavCommandItems());
		list.setAdapter(adapter);
		list.setOnItemClickListener(mItemClickListener);

		handleUserPanel(view);
		return view;
	}

	/**
	 * Deals with the user info panel in the main navigation page, showing the
	 * user information, and fetch the buddy icons.
	 * 
	 * @param view
	 *            the root view of this fragment, that is, the view returned
	 *            from <code>onCreateView</code>.
	 */
	protected void handleUserPanel(View view) {

		// the user panel.
		final View userPanel = view.findViewById(R.id.user_panel);
		final FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
				.getApplication();
		String token = app.getFlickrToken();
		String tokenSecret = app.getFlickrTokenSecret();
		userPanel
				.setVisibility(token == null || tokenSecret == null ? View.INVISIBLE
						: View.VISIBLE);
		if (token != null && tokenSecret != null) {
			TextView userText = (TextView) view.findViewById(R.id.user_name);
			userText.setText(app.getUserName());
		}

		// logout button
		ImageView button = (ImageView) view.findViewById(R.id.buttonLogout);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder
						.setMessage(
								getActivity().getString(R.string.logout_msg))
						.setCancelable(false).setPositiveButton(
								getActivity().getString(R.string.btn_yes),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										app.logout();
										userPanel.setVisibility(View.INVISIBLE);
										deleteBuddyIconFile();
										goHome();
									}

									private void goHome() {
										FragmentManager fm = getActivity()
												.getFragmentManager();
										for (int i = 0; i < fm
												.getBackStackEntryCount(); i++) {
											fm.popBackStack();
										}
									}

									private void deleteBuddyIconFile() {
										File root = new File(Environment
												.getExternalStorageDirectory(),
												Constants.SD_CARD_FOLDER_NAME);
										File buddyIconFile = new File(
												root,
												Constants.FLICKR_BUDDY_IMAGE_FILE_NAME);
										if (!buddyIconFile.exists()) {
											try {
												if (buddyIconFile.delete()) {
													logger
															.debug("Buddy icon cache file deleted."); //$NON-NLS-1$
												} else {
													logger
															.debug("Failed to delete the cached buddy icon."); //$NON-NLS-1$
												}
											} catch (Exception ex) {
												logger
														.warn("Error when trying deleting the cache buddy icon: " //$NON-NLS-1$
																+ ex
																		.getMessage());
											}
										}
									}
								}).setNegativeButton(
								getActivity().getString(R.string.btn_no),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		// buddy icon
		ImageView iconImage = (ImageView) view.findViewById(R.id.user_icon);
		Bitmap cachedIcon = getBuddyIconFromCache();
		if (cachedIcon != null) {
			iconImage.setImageBitmap(cachedIcon);
		} else {
			String userId = app.getUserId();
			GetUserInfoTask task = new GetUserInfoTask(iconImage, null,
					mImageDownloadedListener);
			task.execute(userId);
		}
	}

	/**
	 * Gets the buddy icon from the sd card, which was cached before.
	 * 
	 * @return
	 */
	private Bitmap getBuddyIconFromCache() {
		File root = new File(Environment.getExternalStorageDirectory(),
				Constants.SD_CARD_FOLDER_NAME);
		File buddyIconFile = new File(root,
				Constants.FLICKR_BUDDY_IMAGE_FILE_NAME);
		if (!buddyIconFile.exists()) {
			return null;
		}
		return BitmapFactory.decodeFile(buddyIconFile.getAbsolutePath());
	}

	/**
	 * The image download listener to save the buddy icons.
	 */
	private IImageDownloadDoneListener mImageDownloadedListener = new IImageDownloadDoneListener() {

		@Override
		public void onImageDownloaded(final Bitmap bitmap) {
			File root = new File(Environment.getExternalStorageDirectory(),
					Constants.SD_CARD_FOLDER_NAME);
			if (!root.exists() && !root.mkdirs()) {
				return;
			}

			final File buddyIconFile = new File(root,
					Constants.FLICKR_BUDDY_IMAGE_FILE_NAME);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					ImageUtils.saveImageToFile(buddyIconFile, bitmap);
				}
			});
		}
	};

	/**
	 * The main menu adapter.
	 */
	private static class NavMenuAdapter extends BaseAdapter {

		private List<CommandItem> commands;
		private Context context;

		public NavMenuAdapter(Context context, List<CommandItem> commands) {
			this.commands = commands;
			this.context = context;
		}

		@Override
		public int getCount() {
			return commands.size();
		}

		@Override
		public Object getItem(int arg0) {
			return commands.get(arg0);
		}

		@Override
		public long getItemId(int pos) {
			CommandItem item = (CommandItem) getItem(pos);
			return item.id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater li = LayoutInflater.from(context);
				view = li.inflate(R.layout.main_nav_item, null);
			}
			ViewHolder holder = (ViewHolder) view.getTag();
			CommandItem command = (CommandItem) getItem(position);

			ImageView image;
			TextView text;
			if (holder == null) {
				image = (ImageView) view.findViewById(R.id.nav_item_image);
				text = (TextView) view.findViewById(R.id.nav_item_title);
				holder = new ViewHolder();
				holder.image = image;
				holder.text = text;
				view.setTag(holder);
			} else {
				image = holder.image;
				text = holder.text;
			}
			image.setImageResource(command.imageResId);
			text.setText(command.title);

			return view;
		}

	}

	private static class ViewHolder {
		ImageView image;
		TextView text;
	}

	private static class CommandItem {

		static final int ID_INTERESTING = 0;
		static final int ID_MY_PHOTOS = 1;
		static final int ID_MY_SETS = 2;
		static final int ID_MY_CONTACTS = 3;
		static final int ID_MY_FAV = 4;
		static final int ID_MY_POPULAR = 5;
		static final int ID_ACTIVITY = 6;
		static final int ID_SETTINGS = 7;

		int imageResId;
		String title;
		int id;
	}

	private List<CommandItem> createNavCommandItems() {
		List<CommandItem> list = new ArrayList<CommandItem>();

		// interesting photos
		CommandItem item = new CommandItem();
		item.imageResId = R.drawable.interesting;
		item.title = getActivity().getString(R.string.item_interesting_photo);
		item.id = CommandItem.ID_INTERESTING;
		list.add(item);

		// my photos
		item = new CommandItem();
		item.imageResId = R.drawable.photos;
		item.title = getActivity().getString(R.string.item_my_photo);
		item.id = CommandItem.ID_MY_PHOTOS;
		list.add(item);

		// my gallery, sets and groups.
		item = new CommandItem();
		item.imageResId = R.drawable.gallery;
		item.title = getActivity().getString(R.string.item_my_sets);
		item.id = CommandItem.ID_MY_SETS;
		list.add(item);

		// my popular photos.
		item = new CommandItem();
		item.imageResId = R.drawable.pepper;
		item.title = getActivity().getString(R.string.item_my_popular);
		item.id = CommandItem.ID_MY_POPULAR;
		list.add(item);

		// contacts
		item = new CommandItem();
		item.imageResId = R.drawable.contacts;
		item.title = getActivity().getString(R.string.item_my_contact);
		item.id = CommandItem.ID_MY_CONTACTS;
		list.add(item);

		// favorites
		item = new CommandItem();
		item.imageResId = R.drawable.myfavorite;
		item.title = getActivity().getString(R.string.item_my_fav);
		item.id = CommandItem.ID_MY_FAV;
		list.add(item);

		// recent activities
		item = new CommandItem();
		item.imageResId = R.drawable.activities;
		item.title = getActivity().getString(R.string.item_recent_activities);
		item.id = CommandItem.ID_ACTIVITY;
		list.add(item);

		// settings.
		item = new CommandItem();
		item.imageResId = R.drawable.settings;
		item.title = getActivity().getString(R.string.item_settings);
		item.id = CommandItem.ID_SETTINGS;
		list.add(item);

		return list;
	}

}
