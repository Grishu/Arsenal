package com.gmail.listselect;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity implements
		OnItemClickListener {

	ListView lvListView;
	ArrayList<MyListItem> listItems = new ArrayList<MyListItem>();
	ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		lvListView = (ListView) findViewById(R.id.lvMyList);
		lvListView.setOnItemClickListener(this);

		for (int i = 0; i < 20; i++) {
			MyListItem item = new MyListItem();
			item.setTitle("Sample item " + (i + 1));
			listItems.add(item);
		}

		 
		adapter = new ListAdapter(this, listItems);
		lvListView.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// show description
		Toast.makeText(this,
				"Item Clicked is-->" + listItems.get(arg2).getTitle(),
				Toast.LENGTH_LONG).show();
	}

	public static final class AnActionModeOfEpicProportions implements
			ActionMode.Callback {

		Context m_ctx;

		public AnActionModeOfEpicProportions(Context ctx) {
			this.m_ctx = ctx;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			menu.add("Delete").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add("Archive")
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add("Mark unread").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add("Move").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add("Remove star").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			Toast toast = null;

			ArrayList<MyListItem> selectedListItems = new ArrayList<MyListItem>();

			StringBuilder selectedItems = new StringBuilder();

			// get items selected
			for (MyListItem i : ((MainActivity) m_ctx).adapter.mailList) {
				if (i.isChecked()) {
					selectedListItems.add(i);
					selectedItems.append(i.getTitle()).append(", ");
				}
			}

			if (item.getTitle().equals("Delete")) {
				// Delete
				toast = Toast.makeText(m_ctx,
						"Delete: " + selectedItems.toString(),
						Toast.LENGTH_SHORT);

			} else if (item.getTitle().equals("Archive")) {
				// Archive
				toast = Toast.makeText(m_ctx,
						"Archive: " + selectedItems.toString(),
						Toast.LENGTH_SHORT);

			} else if (item.getTitle().equals("Mark unread")) {
				// Mark unread
				toast = Toast.makeText(m_ctx,
						"Mark unread: " + selectedItems.toString(),
						Toast.LENGTH_SHORT);

			} else if (item.getTitle().equals("Move")) {
				// Move
				toast = Toast
						.makeText(m_ctx, "Move: " + selectedItems.toString(),
								Toast.LENGTH_SHORT);

			} else if (item.getTitle().equals("Remove star")) {
				// Remove star
				toast = Toast.makeText(m_ctx,
						"Remove star: " + selectedItems.toString(),
						Toast.LENGTH_SHORT);

			}
			if (toast != null) {
				toast.show();
			}
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// Action mode is finished reset the list and 'checked count' also
			// set all the list items checked states to false
			((MainActivity) m_ctx).adapter.checkedCount = 0;
			((MainActivity) m_ctx).adapter.isActionModeShowing = false;
			// set list items states to false
			for (MyListItem item : ((MainActivity) m_ctx).listItems) {
				item.setIsChecked(false);
			}
			((MainActivity) m_ctx).adapter.notifyDataSetChanged();
			Toast.makeText(m_ctx, "Action mode closed", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	

}
