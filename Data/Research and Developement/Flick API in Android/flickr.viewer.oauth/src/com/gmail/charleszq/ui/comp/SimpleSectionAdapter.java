/**
 * 
 */
package com.gmail.charleszq.ui.comp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.charleszq.R;

/**
 * @author charles
 */
public class SimpleSectionAdapter extends SectionAdapter {

	/**
	 * The context
	 */
	private Context mContext;

	public SimpleSectionAdapter(Context context) {
		this.mContext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.ui.comp.SectionAdapter#getHeaderView(java.lang.String
	 * , int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	protected View getHeaderView(String caption, int index, View convertView,
			ViewGroup parent) {

		TextView result = (TextView) convertView;
		if (convertView == null) {
			LayoutInflater li = LayoutInflater.from(mContext);
			result = (TextView) li.inflate(R.layout.section_header, null);
		}

		result.setText(caption);
		return result;
	}

}
