package com.mythems.themedemo;

import android.app.Activity;
import android.os.Bundle;

public class ViewThemeActivity extends Activity
{
	@Override
	protected void onCreate(Bundle p_savedInstanceState)
	{
		super.onCreate(p_savedInstanceState);
		setTheme(ThemeActivity.m_nThemeId);
		setContentView(R.layout.view_theme_layout);
	}
}
