/*
 * Created on Aug 31, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.ui.comp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.charleszq.R;

/**
 * Represents the ui component to create a gallery, or a photo set.
 * 
 * @author charles
 * 
 */
public class CreateGalleryComponent extends AbstractComponent {

	private EditText mTitle, mDescription;
	private TextView mError;

	/**
	 * @param context
	 */
	public CreateGalleryComponent(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CreateGalleryComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CreateGalleryComponent(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ui.comp.AbstractComponent#buildLayout()
	 */
	@Override
	protected void buildLayout() {
		LayoutInflater li = LayoutInflater.from(getContext());
		li.inflate(R.layout.create_gallery, this, true);
		mTitle = (EditText) findViewById(R.id.title);
		mDescription = (EditText) findViewById(R.id.description);
		mError = (TextView) findViewById(R.id.error_msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.ui.comp.AbstractComponent#init(java.lang.Object[])
	 */
	@Override
	public void init(Object... objs) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ui.comp.AbstractComponent#validate()
	 */
	@Override
	public boolean validate() {

		String title = null;
		if (mTitle.getText() != null) {
			title = mTitle.getText().toString();
		}
		if (title == null || title.trim().length() == 0) {
			mError.setText(getContext().getString(R.string.error_no_title));
			return false;
		} else {
			mError.setText(""); //$NON-NLS-1$
			return super.validate();
		}
	}

	public String getGalleryTile() {
		return mTitle.getText().toString();
	}

	public String getGalleryDescription() {
		if (mDescription.getText() == null) {
			return null;
		} else {
			return mDescription.getText().toString();
		}
	}

}
