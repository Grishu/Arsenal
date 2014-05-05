package com.gmail.listselect;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;

public class ListAdapter extends BaseAdapter {

	Context context;
	ArrayList<MyListItem> mailList;
	Animation animation1;
	Animation animation2;
	ImageView ivFlip;
	int checkedCount = 0;
	ActionMode mMode;
	boolean isActionModeShowing;

	public ListAdapter(Context context, ArrayList<MyListItem> mailList) {
		this.context = context;
		this.mailList = mailList;

		animation1 = AnimationUtils.loadAnimation(context, R.anim.to_middle);
		animation2 = AnimationUtils.loadAnimation(context, R.anim.from_middle);

		isActionModeShowing = false;
	}

	@Override
	public int getCount() {
		return mailList.size();
	}

	@Override
	public MyListItem getItem(int position) {
		return mailList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.selectBox = (ImageView) convertView.findViewById(R.id.selectBox);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();

		holder.title.setText(getItem(position).getTitle());
		holder.selectBox.setTag("" + position);
		holder.selectBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ivFlip = (ImageView) v;
				ivFlip.clearAnimation();
				ivFlip.setAnimation(animation1);
				ivFlip.startAnimation(animation1);
				setAnimListners(mailList.get(Integer.parseInt(v.getTag().toString())));
			}

		});

		if (mailList.get(position).isChecked()) {
			holder.selectBox.setImageResource(R.drawable.cb_checked);
			convertView.setBackgroundColor(context.getResources().getColor(R.color.list_highlight));

		} else {
			holder.selectBox.setImageResource(R.drawable.cb_unchecked);
			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_selector));

		}

		return convertView;

	}

	private void setAnimListners(final MyListItem curMail) {
		AnimationListener animListner;
		animListner = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				if (animation == animation1) {
					if (curMail.isChecked()) {
						ivFlip.setImageResource(R.drawable.cb_unchecked);
					} else {
						ivFlip.setImageResource(R.drawable.cb_checked);
					}
					ivFlip.clearAnimation();
					ivFlip.setAnimation(animation2);
					ivFlip.startAnimation(animation2);
				} else {
					curMail.setIsChecked(!curMail.isChecked());
					setCount();
					setActionMode();
				}
			}

			// Set selected count
			private void setCount() {
				if (curMail.isChecked()) {
					checkedCount++;
				} else {
					if (checkedCount != 0) {
						checkedCount--;
					}
				}

			}

			// Show/Hide action mode
			private void setActionMode() {
				if (checkedCount > 0) {
					if (!isActionModeShowing) {
						mMode = ((MainActivity) context).startActionMode(new MainActivity.AnActionModeOfEpicProportions(context));
						isActionModeShowing = true;
					}
				} else if (mMode != null) {
					mMode.finish();
					isActionModeShowing = false;
				}

				// Set action mode title
				if (mMode != null)
					mMode.setTitle(String.valueOf(checkedCount));

				notifyDataSetChanged();

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub

			}
		};

		animation1.setAnimationListener(animListner);
		animation2.setAnimationListener(animListner);

	}

	static class ViewHolder {
		TextView title;
		ImageView selectBox;
	}

}
