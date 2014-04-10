/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.gmail.charleszq.FlickrViewerActivity;
import com.gmail.charleszq.R;
import com.gmail.charleszq.actions.ShowMyContactsAction;
import com.gmail.charleszq.actions.ShowPeoplePhotosAction;
import com.gmail.charleszq.event.IContactsFetchedListener;
import com.gmail.charleszq.task.ImageDownloadTask;
import com.gmail.charleszq.utils.ImageCache;
import com.gmail.charleszq.utils.ImageUtils.DownloadedDrawable;
import com.gmail.yuyang226.flickr.contacts.Contact;

/**
 * @author charles
 */
public class ContactsFragment extends Fragment implements
		IContactsFetchedListener, OnItemClickListener {

	private static final String FAMILY_ONLY = "family_only"; //$NON-NLS-1$
	private static final String FAMILY_AND_FRIEND = "family_and_friend"; //$NON-NLS-1$
	private static final String CONTACT_ALL = "contact_all"; //$NON-NLS-1$
	private static final String HAS_NEW_PHOTO = "has_new_photo"; //$NON-NLS-1$

	private MyAdapter mAdapter;
	private List<Contact> mContacts = null;
	private Set<String> mContactIdsWithPhotoUploaded = null;
	
	/**
	 * The current filter string.
	 */
	private String mCurrentFilterString = null;

	private boolean mCreatedByOS = false;

	/**
	 * Default constructor.
	 */
	public ContactsFragment() {
		mContacts = new ArrayList<Contact>();
		mCreatedByOS = true;
	}

	public ContactsFragment(List<Contact> contacts) {
		mContacts = contacts;
	}

	/**
	 * Constructor. This constructor will be called when the background task
	 * detects that one or more of my contacts have photo uploaded recently.
	 * 
	 * @param hasPhotoUploadedIds
	 */
	public ContactsFragment(Set<String> hasPhotoUploadedIds) {
		this();
		mContactIdsWithPhotoUploaded = hasPhotoUploadedIds;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GridView gv = new GridView(getActivity());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		gv.setLayoutParams(layoutParams);
		gv.setNumColumns(3);
		gv.setHorizontalSpacing(20);
		gv.setVerticalSpacing(10);
		mAdapter = new MyAdapter(getActivity(), mContacts,
				mContactIdsWithPhotoUploaded);
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);

		FlickrViewerActivity act = (FlickrViewerActivity) getActivity();
		act.changeActionBarTitle(null);
		return gv;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mCreatedByOS) {
			ShowMyContactsAction action = new ShowMyContactsAction(
					getActivity(), this);
			action.execute();
			mCreatedByOS = false;
		}
		
		if( mCurrentFilterString != null && mAdapter != null ) {
			mAdapter.getFilter().filter(mCurrentFilterString);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_contacts, menu);
		MenuItem item = menu.findItem(R.id.menu_item_search);
		SearchView sview = (SearchView) item.getActionView();
		sview.setQueryHint(getActivity().getString(R.string.contact_filter_hint));
		sview.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String filterString) {
				mAdapter.getFilter().filter(filterString);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		item.setChecked(true);
		String filterString = null;
		switch (item.getItemId()) {
		case R.id.family_only:
			filterString = FAMILY_ONLY;
			break;
		case R.id.family_and_friend:
			filterString = FAMILY_AND_FRIEND;
			break;
		case R.id.contact_all:
			filterString = CONTACT_ALL;
			break;
		case R.id.has_new_photos:
			filterString = HAS_NEW_PHOTO;
			break;
		}
		
		mCurrentFilterString = filterString;
		if (filterString != null) {
			mAdapter.getFilter().filter(filterString);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}

	}

	private static class MyAdapter extends BaseAdapter implements Filterable {

		private List<Contact> mContacts;
		private Context mContext;
		private Set<String> mIdsWithPhotoUploaded = null;
		private Filter mFilter;

		public MyAdapter(Context context, List<Contact> contacts,
				Set<String> uploadedCIds) {
			this.mContacts = contacts;
			this.mContext = context;
			mIdsWithPhotoUploaded = uploadedCIds;
		}

		@Override
		public int getCount() {
			List<Contact> list = ((ContactFilter) getFilter()).getFilterdList();
			return list.size();
		}

		@Override
		public Object getItem(int pos) {
			if (getCount() > 0 && pos < getCount()) {
				return ((ContactFilter) getFilter()).getFilterdList().get(pos);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = convertView;
			if (view == null) {
				LayoutInflater li = LayoutInflater.from(mContext);
				view = li.inflate(R.layout.contact_item, null);
			}

			Contact contact = (Contact) getItem(position);
			if( contact == null ) {
				return view;
			}

			ImageView photoImage, notifImage;
			TextView titleView;
			CheckBox cbFamily, cbFriend;

			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				photoImage = (ImageView) view.findViewById(R.id.contact_icon);
				titleView = (TextView) view.findViewById(R.id.contact_name);
				notifImage = (ImageView) view.findViewById(R.id.has_new_photo);
				cbFamily = (CheckBox) view.findViewById(R.id.cb_family);
				cbFriend = (CheckBox) view.findViewById(R.id.cb_friend);

				holder = new ViewHolder();
				holder.image = photoImage;
				holder.titleView = titleView;
				holder.notifImage = notifImage;
				holder.cbFamily = cbFamily;
				holder.cbFriend = cbFriend;
				view.setTag(holder);

			} else {
				photoImage = holder.image;
				titleView = holder.titleView;
				notifImage = holder.notifImage;
				cbFamily = holder.cbFamily;
				cbFriend = holder.cbFriend;
			}
			titleView.setText(contact.getUsername());
			photoImage.setScaleType(ScaleType.CENTER_CROP);
			if (mIdsWithPhotoUploaded != null
					&& mIdsWithPhotoUploaded.contains(contact.getId())) {
				notifImage.setVisibility(View.VISIBLE);
			} else {
				notifImage.setVisibility(View.GONE);
			}
			cbFamily.setChecked(contact.isFamily());
			cbFriend.setChecked(contact.isFriend());

			Drawable drawable = photoImage.getDrawable();
			String buddyIconUrl = contact.getBuddyIconUrl();
			if (drawable != null && drawable instanceof DownloadedDrawable) {
				ImageDownloadTask task = ((DownloadedDrawable) drawable)
						.getBitmapDownloaderTask();
				if (!buddyIconUrl.equals(task)) {
					task.cancel(true);
				}
			}

			if (buddyIconUrl == null) {
				photoImage.setImageDrawable(null);
			} else {
				Bitmap cacheBitmap = ImageCache.getFromCache(buddyIconUrl);
				if (cacheBitmap != null) {
					photoImage.setImageBitmap(cacheBitmap);
				} else {
					ImageDownloadTask task = new ImageDownloadTask(photoImage);
					drawable = new DownloadedDrawable(task);
					photoImage.setImageDrawable(drawable);
					task.execute(buddyIconUrl);
				}
			}

			return view;
		}

		private static class ViewHolder {
			ImageView image;
			TextView titleView;
			ImageView notifImage;

			CheckBox cbFamily, cbFriend;
		}

		@Override
		public Filter getFilter() {
			if (mFilter == null) {
				mFilter = new ContactFilter(this.mContacts, this,
						mIdsWithPhotoUploaded);
			}
			return mFilter;
		}

	}

	private static class ContactFilter extends Filter {

		private List<Contact> mOriginalContacts;
		private BaseAdapter mAdapter;
		private List<Contact> mFilterdContacts;
		private Set<String> mContactIds;

		ContactFilter(List<Contact> contacts, BaseAdapter adapter,
				Set<String> contactIds) {
			this.mOriginalContacts = contacts;
			this.mAdapter = adapter;
			mFilterdContacts = mOriginalContacts;
			mContactIds = contactIds == null ? new HashSet<String>()
					: contactIds;
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (constraint != null) {
				int count = 0;
				List<Contact> filterdList = new ArrayList<Contact>();
				for (Contact contact : mOriginalContacts) {
					if (FAMILY_ONLY.equals(constraint.toString())
							&& contact.isFamily()) {
						count++;
						filterdList.add(contact);
					} else if (FAMILY_AND_FRIEND.equals(constraint.toString())
							&& (contact.isFamily() || contact.isFriend())) {
						count++;
						filterdList.add(contact);
					} else if (CONTACT_ALL.equals(constraint.toString())) {
						count++;
						filterdList.add(contact);
					} else if (contact.getUsername().toLowerCase()
							.contains(constraint.toString())) {
						count++;
						filterdList.add(contact);
					} else if (HAS_NEW_PHOTO.equals(constraint.toString())) {
						if (mContactIds.contains(contact.getId())) {
							count++;
							filterdList.add(contact);
						}
					}
				}
				results.count = count;
				results.values = filterdList;
				mFilterdContacts = filterdList;
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			mAdapter.notifyDataSetChanged();
		}

		List<Contact> getFilterdList() {
			return mFilterdContacts;
		}

	}

	@Override
	public void onContactsFetched(Collection<Contact> contacts) {
		mContacts.clear();
		mContacts.addAll(contacts);
		mAdapter.notifyDataSetChanged();
		if (mContactIdsWithPhotoUploaded != null) {
			mAdapter.getFilter().filter(HAS_NEW_PHOTO);
			mCurrentFilterString = HAS_NEW_PHOTO;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Contact c = (Contact) mAdapter.getItem(position);
		if (c != null) {
			String userId = c.getId();
			ShowPeoplePhotosAction action = new ShowPeoplePhotosAction(
					getActivity(), userId, c.getUsername());
			action.execute();
		}
	}
}
