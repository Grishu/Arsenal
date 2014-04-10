/*
 * Created on Aug 31, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.ui.comp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author charles
 *
 */
public abstract class AbstractComponent extends FrameLayout {

	/**
	 * @param context
	 */
	public AbstractComponent(Context context) {
		super(context);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AbstractComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AbstractComponent(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		buildLayout();
	}
	
	protected abstract void buildLayout();
	public abstract void init(Object... objs);
	
	/**
	 * Does the necessary validation.
	 * @return
	 */
	public boolean validate() {
		return true;
	}

}
